package com.ngw.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ngw.common.Constants;
import com.ngw.domain.ResponseCode;
import com.ngw.domain.Sql;
import com.ngw.domain.SqlParam;
import com.ngw.exception.BaseBizException;
import com.ngw.service.api.SqlService;
import com.ngw.util.SqlUtil;
import com.ngw.domain.ResponseModel;
import jodd.bean.BeanUtil;
import jodd.util.StringUtil;
import org.elasticsearch.action.search.SearchRequest;
import org.nlpcn.es4sql.Util;
import org.nlpcn.es4sql.query.SqlElasticRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zy-xx on 2019/8/22.
 */
@Service
public class SqlServiceImpl implements SqlService {
    private static Logger logger = LoggerFactory.getLogger(SqlServiceImpl.class);

    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String fieldAggsSql = "select '(' || nvl(f.fieldaggsformula,f.fieldcode) || ')' from t_dsmanager_field f where f.deleteflag='0' and f.fieldisaggs='1' and exists (select 1 from t_dsmanager_data d where d.id = f.dataid and d.datacode in %s) order by f.fieldshowordernum";

    private String fieldDetailSql = "select fieldcode from t_dsmanager_field f where deleteflag='0' and fielddetailishiden='0' and exists (select 1 from t_dsmanager_data d where d.id = f.dataid and d.datacode in %s) order by fieldshowordernum";

    private String fieldSql = "select fieldcode from t_dsmanager_field f where deleteflag='0' and fieldishiden='0' and exists (select 1 from t_dsmanager_data d where d.id = f.dataid and d.datacode in %s) order by fieldshowordernum";


    private String logSql = "insert into t_dssearch_searchlog(ID,EXPLAIN,SQL,TOOK,CREATOR,CREATETIME,DELETEFLAG) values (?,?,?,?,?,sysdate,'0')";

    @Value("${elasticsearch.host}")
    private String host;

    @Value("${elasticsearch.searchparam}")
    private String searchparam;

    @Override
    public ResponseModel explain(String sql) {
        SqlElasticRequestBuilder builder = null;
        try {
            builder = Util.sqlToEsQuery(sql);
        } catch (Exception e) {
            return ResponseModel.getBizError("sql解析失败!\n" + e.getMessage());
        }
        return ResponseModel.of(builder.explain());
    }

    @Override
    public ResponseModel search(Sql sqlParam) {
        String sql = sqlParam.getSql();
        String username = sqlParam.getUsername();
        int roleLevel = sqlParam.getRoleLevel();
        Long start = System.currentTimeMillis();
        SqlElasticRequestBuilder builder = null;
        try {
            builder = Util.sqlToEsQuery(sql);
        } catch (Exception e) {
            return ResponseModel.getBizError("sql解析失败!\n" + e.getMessage());
        }
        String request = builder.explain();
        if (StringUtil.isNotBlank(request)) {
            SearchRequest searchRequest = ((SearchRequest) builder.request());
            String[] indexs = searchRequest.indices();
            String uri = host + "/" + getIndexsWithRole(roleLevel, indexs) + "/_search?" + searchparam;
            String response = null;
            response = SqlUtil.post(uri, Constants.JSON_HEADER, request, Constants.CHARSET_UTF8);
            if (response == null) {
                return ResponseModel.getBizError();
            }
            logger.debug(request + "\n" + response);
            Long took = System.currentTimeMillis() - start;
            log(sql, request, username, took);
            return ResponseModel.of(JSONObject.parseObject(response));
        } else {
            return ResponseModel.getBizError("sql解析失败!\n其他原因！");
        }
    }

    @Override
    public ResponseModel defaultSearch(SqlParam sqlParam) {
        String bateDatas = StringUtil.join(sqlParam.getDatas(), "','");
        //aggs
        List<String> defaultAggsList = jdbcTemplate.queryForList(String.format(fieldAggsSql, "('" + bateDatas + "')"), String.class);
        String defaultAggs = StringUtil.join(defaultAggsList, ",");
        //fields
        List<String> defaultFields = Lists.newArrayList();
        if (sqlParam.isDetail()) {
            defaultFields = jdbcTemplate.queryForList(String.format(fieldDetailSql, "('" + bateDatas + "')"), String.class);
        } else {
            defaultFields = jdbcTemplate.queryForList(String.format(fieldSql, "('" + bateDatas + "')"), String.class);
        }
        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        if (StringUtil.isNotBlank(sqlParam.getHighlight())) {
            sql.append(sqlParam.getHighlight());
        }
        //聚类分页特殊处理
        if (StringUtil.isNotBlank(defaultAggs)) {
            sql.append(" /*! DOCS_WITH_AGGREGATION(").append(sqlParam.getPage() * sqlParam.getPageSize()).append(",").append(sqlParam.getPageSize()).append(") */");
        }
        sql.append(StringUtil.join(defaultFields, ","))
                .append(" from ['").append(bateDatas.toLowerCase()).append("']");
        if (StringUtil.isNotBlank(sqlParam.getConditions())) {
            sql.append(" where ").append(sqlParam.getConditions());
        }
        if (StringUtil.isNotBlank(defaultAggs)) {
            sql.append(" group by ").append(defaultAggs);
        }
        if (StringUtil.isNotBlank(sqlParam.getOrders())) {
            sql.append(" order by ").append(sqlParam.getOrders());
        }
        //聚类分页特殊处理
        if (StringUtil.isBlank(defaultAggs)) {
            sql.append(" limit ").append(sqlParam.getPage() * sqlParam.getPageSize()).append(",").append(sqlParam.getPageSize());
        }
        return search(new Sql(sql.toString(), sqlParam.getUsername(), sqlParam.getRoleLevel()));
    }

    private void log(String sql, String explain, String username, Long took) {
        logger.debug("耗时:" + took);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                jdbcTemplate.update(logSql, "" + UUID.randomUUID().toString().substring(0, 20), explain, sql, took, username);
            }
        });
    }

    private String getIndexsWithRole(int roleLevel, String[] indexs) {
        StringBuilder index = new StringBuilder();
        index.append("'").append(SqlUtil.indexsJoin(indexs, "','")).append("'");
        List<String> lists = jdbcTemplate.queryForList("select t.datacode from t_dsmanager_data t where t.datacode in (" + index + ") and t.datarolelevel <= ?", String.class, roleLevel);
        if (lists.size() != index.length()) {
            return StringUtil.join(lists, ",");
        }
        return index.toString();
    }
}
