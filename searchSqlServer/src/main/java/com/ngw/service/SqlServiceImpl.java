package com.ngw.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ngw.common.Constants;
import com.ngw.domain.*;
import com.ngw.service.api.RequestTrans;
import com.ngw.service.api.SqlService;
//import com.ngw.util.RestClientUtil;
import com.ngw.util.SqlUtil;
import jodd.util.StringUtil;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.nlpcn.es4sql.Util;
import org.nlpcn.es4sql.query.SqlElasticRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
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

    @Value("${elasticsearch.textCondition}")
    private String defaultCondition;

    @Autowired
    @Qualifier("oracleJdbcTemplate")
    private JdbcTemplate jdbcTemplate;
    private LobHandler lobHandler = new DefaultLobHandler();  // reusable object

    private int defaultPageSize = 10;

    private String batchTaskUpdateSql = "update T_DSMANAGER_BATCHTASK set taskstatus='02' where id=?";

    private String dataByCodeSql = "select * from t_dsmanager_data d where d.datacode = ?";

    private String fieldNamesByDataSql = "select nvl(fieldname,esfieldcode) from v_t_dsmanager_field_es f where deleteflag='0' and (exists (select 1 from t_dsmanager_data d where d.id = f.dataid and nvl(d.dataisrelation,'0')='0' and d.datacode = ?) or (relationparentfieldid is not null and exists (select 1 from t_dsmanager_data d where d.id = f.dataid and d.datacode = ? and nvl(d.dataisrelation,'0')='1')))";

    private String fieldCodesByDataSql = "select esfieldcode from v_t_dsmanager_field_es f where deleteflag='0' and (exists (select 1 from t_dsmanager_data d where d.id = f.dataid and nvl(d.dataisrelation,'0')='0' and d.datacode = ?) or (relationparentfieldid is not null and exists (select 1 from t_dsmanager_data d where d.id = f.dataid and d.datacode = ? and nvl(d.dataisrelation,'0')='1')))";

    private String fieldFVHSql = "select f.esfieldcode from v_t_dsmanager_field_es f where f.deleteflag='0' and f.fieldisfvh='1' and (exists (select 1 from t_dsmanager_data d where d.id = f.dataid and nvl(d.dataisrelation,'0')='0' and d.datacode in %s) or (relationparentfieldid is not null and exists (select 1 from t_dsmanager_data d where d.id = f.dataid and d.datacode in %s and nvl(d.dataisrelation,'0')='1'))) order by f.fieldshowordernum";

//    private String fieldParentAggsSql = "select '(' || nvl(f.fieldaggsformula,f.esfieldcode) || ')' from v_t_dsmanager_field_es f where f.deleteflag='0' and f.fieldisaggs='1' and f.PARENTFIELDCODE = %s and (exists (select 1 from t_dsmanager_data d where d.id = f.dataid and nvl(d.dataisrelation,'0')='0' and d.datacode in %s) or (relationparentfieldid is not null and exists (select 1 from t_dsmanager_data d where d.id = f.dataid and d.datacode in %s and nvl(d.dataisrelation,'0')='1'))) order by f.fieldshowordernum";

//    private String fieldAggsSql = "select '(' || nvl(f.fieldaggsformula,f.esfieldcode) || ')' from v_t_dsmanager_field_es f where f.deleteflag='0' and f.fieldisaggs='1' and f.PARENTFIELDCODE is null and (exists (select 1 from t_dsmanager_data d where d.id = f.dataid and nvl(d.dataisrelation,'0')='0' and d.datacode in %s) or (relationparentfieldid is not null and exists (select 1 from t_dsmanager_data d where d.id = f.dataid and d.datacode in %s and nvl(d.dataisrelation,'0')='1'))) order by f.fieldshowordernum";

    private String fieldAggsSql = "select '(' || nvl(f.fieldaggsformula,f.esfieldcode) || ')' from v_t_dsmanager_field_es f where f.deleteflag='0' and f.fieldisaggs='1' and (exists (select 1 from t_dsmanager_data d where d.id = f.dataid and nvl(d.dataisrelation,'0')='0' and d.datacode in %s) or (relationparentfieldid is not null and exists (select 1 from t_dsmanager_data d where d.id = f.dataid and d.datacode in %s and nvl(d.dataisrelation,'0')='1'))) order by f.fieldshowordernum";

    private String fieldDetailSql = "select esfieldcode from v_t_dsmanager_field_es f where deleteflag='0' and fielddetailishiden='0' and (exists (select 1 from t_dsmanager_data d where d.id = f.dataid and nvl(d.dataisrelation,'0')='0' and d.datacode in %s) or (relationparentfieldid is not null and exists (select 1 from t_dsmanager_data d where d.id = f.dataid and d.datacode in %s and nvl(d.dataisrelation,'0')='1'))) order by fieldshowordernum";

    private String fieldSql = "select esfieldcode from v_t_dsmanager_field_es f where deleteflag='0' and fieldishiden='0' and (exists (select 1 from t_dsmanager_data d where d.id = f.dataid and nvl(d.dataisrelation,'0')='0' and d.datacode in %s) or (relationparentfieldid is not null and exists (select 1 from t_dsmanager_data d where d.id = f.dataid and d.datacode in %s and nvl(d.dataisrelation,'0')='1'))) order by fieldshowordernum";

    private String indexRoleSql = "select t.datacode from t_dsmanager_data t where t.datacode in %s and t.datarolelevel <= ?";


    private String logSql = "insert into t_dssearch_searchlog(ID,TOOK,CREATOR,CREATETIME,DELETEFLAG,SQL,EXPLAIN) values (?,?,?,sysdate,'0',?,?)";

    @Value("${elasticsearch.host}")
    private String host;
    @Value("${elasticsearch.username:#{null}}")
    private String username;
    @Value("${elasticsearch.password:#{null}}")
    private String password;

    @Value("${elasticsearch.searchparam}")
    private String searchparam;

    @Override
    public ResponseModel explain(String sql) {
        SqlElasticRequestBuilder builder;
        try {
            builder = Util.sqlToEsQuery(sql);
        } catch (Exception e) {
            return ResponseModel.getBizError("sql解析失败!\n" + e.getMessage());
        }
        return ResponseModel.of(builder.explain());
    }

    @Override
    public ResponseModel search(Sql sqlParam, RequestTrans requestTrans) {
        String sql = sqlParam.getSql();
        String username = sqlParam.getUsername();
        int roleLevel = sqlParam.getRoleLevel();
        long start = System.currentTimeMillis();

//        List<Map<String, Object>> lists = esJdbcTemplate.queryForList(sql, 1);
//        long took = System.currentTimeMillis() - start;
//        log(sql, null, username, took);
//        return ResponseModel.of(lists);
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(com.ngw.SqlUtil.getRestClientBuilder(this.username, this.password, Lists.newArrayList(URI.create(this.host))));
        try {
            String response = com.ngw.SqlUtil.requestSql(sql, restHighLevelClient);

            long took = System.currentTimeMillis() - start;
            log(sql, null, username, took);
            return ResponseModel.of(JSONObject.parseObject(response));
        } finally {
            try {
                restHighLevelClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        SqlElasticRequestBuilder builder;
//        try {
//            builder = Util.sqlToEsQuery(sql);
//        } catch (Exception e) {
//            return ResponseModel.getBizError("sql解析失败!\n" + e.getMessage());
//        }
//        String request = builder.explain();
//        if (requestTrans != null) {
//            request = requestTrans.trans(request);
//        }
//        if (StringUtil.isNotBlank(request)) {
////            RestClient restClient = RestClientUtil.getRestClient();
////            Response rsp = null;
//            SearchRequest searchRequest = ((SearchRequest) builder.request());
//            String[] indexs = searchRequest.indices();
//            String uri = host + "/" + getIndexsWithRole(roleLevel, indexs) + "/_search?" + searchparam;
//            String response;
//            response = SqlUtil.post(uri, Constants.JSON_HEADER, request, Constants.CHARSET_UTF8, this.username, this.password);
//
////            Map<String, String> params = Collections.singletonMap("pretty", "true");
////            Map<String, String> params = JSON.parseObject(request, Map.class);
////            try {
////                String endpoint = "/" + getIndexsWithRole(roleLevel, indexs) + "/" + RestClientUtil.type + "/_search";
////                System.out.println("endpoint = " + endpoint);
////                rsp = restClient.performRequest("GET", endpoint, params);
////                if (HttpStatus.SC_OK == rsp.getStatusLine().getStatusCode()) {
////                    logger.info("QueryData successful.");
////                } else {
////                    logger.error("QueryData failed.");
////                }
////            } catch (Exception e) {
////                logger.error("QueryData failed, exception occurred.", e);
////            }
//            if (response == null) {
//                return ResponseModel.getBizError();
//            }
//            logger.debug(request + "\n" + response);
//            long took = System.currentTimeMillis() - start;
//            log(sql, request, username, took);
//            return ResponseModel.of(JSONObject.parseObject(response));
//        }
//        return ResponseModel.getBizError("sql解析失败!\n其他原因！");
    }

    @Override
    public ResponseModel defaultSearch(SqlParam sqlParam, RequestTrans requestTrans) {
        if (sqlParam.getPageSize() == 0) {
            sqlParam.setPageSize(defaultPageSize);
        }
        String bateDatas = StringUtil.join(sqlParam.getDatas().toArray(), "','");
        String bateDatasEsSql = StringUtil.join(sqlParam.getDatas().toArray(), ",");
        //fvh高亮
        List<String> defaultFVHList = Lists.newArrayList();
        if (sqlParam.isHighlight()) {
            defaultFVHList = jdbcTemplate.queryForList(String.format(fieldFVHSql, "('" + bateDatas + "')", "('" + bateDatas + "')"), String.class);
        }
        String defaultAggs = null;
        if (sqlParam.isAggs()) {
            //aggs
            List<String> defaultAggsList = jdbcTemplate.queryForList(String.format(fieldAggsSql, "('" + bateDatas + "')", "('" + bateDatas + "')"), String.class);
            defaultAggs = StringUtil.join(defaultAggsList.toArray(), ",");
        }
        //fields
        List<String> defaultFields;
        if (sqlParam.isDetail()) {
            defaultFields = jdbcTemplate.queryForList(String.format(fieldDetailSql, "('" + bateDatas + "')", "('" + bateDatas + "')"), String.class);
        } else {
            defaultFields = jdbcTemplate.queryForList(String.format(fieldSql, "('" + bateDatas + "')", "('" + bateDatas + "')"), String.class);
        }
        if (defaultFields.size() == 0) {
            return ResponseModel.getBizError("针对表" + bateDatas + "，无字段可查！");
        }
        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        if (sqlParam.getPageSize() != -1) {
            if (defaultFVHList.size() > 0) {
                for (String fields : defaultFVHList) {
                    sql.append("/*! HIGHLIGHT(").append(fields).append(",pre_tags:['<em>'],post_tags:['</em>'])*/");
                }
            }
            //聚类分页特殊处理
            if (StringUtil.isNotBlank(defaultAggs)) {
                sql.append(" /*! DOCS_WITH_AGGREGATION(").append(sqlParam.getPage() * sqlParam.getPageSize()).append(",").append(sqlParam.getPageSize()).append(") */");
            }
        }
        sql.append("`").append(StringUtil.join(defaultFields.toArray(), "`,`")).append("`")
                .append(" from ['").append(bateDatasEsSql.toLowerCase()).append("']");
        if (StringUtil.isNotBlank(sqlParam.getText())) {
            sql.append(" where ").append(defaultCondition.replace("{text}", sqlParam.getText()));
        } else if (StringUtil.isNotBlank(sqlParam.getConditions())) {
            sql.append(" where ").append(sqlParam.getConditions());
        }
        if (sqlParam.getPageSize() != -1) {
            if (StringUtil.isNotBlank(defaultAggs)) {
                sql.append(" group by ").append(defaultAggs);
            }
            if (StringUtil.isNotBlank(sqlParam.getOrders())) {
                sql.append(" order by ").append(sqlParam.getOrders());
            } else {
                sql.append(" order by _score desc");
            }
        }

        if (StringUtil.isBlank(defaultAggs) || sqlParam.getPageSize() == -1) {
            sql.append(" limit ").append(sqlParam.getPage() * sqlParam.getPageSize()).append(",").append(sqlParam.getPageSize());
        }
        return search(new Sql(sql.toString(), sqlParam.getUsername(), sqlParam.getRoleLevel()), requestTrans);
    }

    @Override
    public ResponseModel aggsSearch(SqlParam sqlSearchParam) {
        String bateDatas = StringUtil.join(sqlSearchParam.getDatas().toArray(), "','");
        String bateDatasEsSql = StringUtil.join(sqlSearchParam.getDatas().toArray(), ",");
        String defaultAggs;
        //aggs
        List<String> defaultAggsList = jdbcTemplate.queryForList(String.format(fieldAggsSql, "('" + bateDatas + "')", "('" + bateDatas + "')"), String.class);
        String parentAggsCondition = "";
//        if (StringUtil.isNotBlank(sqlSearchParam.getParentFieldName())) {
//            parentAggsCondition = sqlSearchParam.getParentFieldName() + "='" + sqlSearchParam.getParentFieldValue() + "' and ";
//            defaultAggsList = jdbcTemplate.queryForList(String.format(fieldParentAggsSql, "'" + sqlSearchParam.getParentFieldName() + "'", "('" + bateDatas + "')", "('" + bateDatas + "')"), String.class);
//        } else {
//            defaultAggsList = jdbcTemplate.queryForList(String.format(fieldAggsSql, "('" + bateDatas + "')", "('" + bateDatas + "')"), String.class);
//        }
        defaultAggs = StringUtil.join(defaultAggsList.toArray(), ",");
        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        sql.append(" * ")
                .append(" from ['").append(bateDatasEsSql.toLowerCase()).append("']");
        /*if (StringUtil.isNotBlank(sqlSearchParam.getText())) {
            sql.append(" where ").append(defaultCondition.replace("{text}", sqlSearchParam.getText()));
        } else*/
        if (StringUtil.isNotBlank(sqlSearchParam.getConditions())) {
            sql.append(" where ").append(parentAggsCondition).append(sqlSearchParam.getConditions());
        }
        if (StringUtil.isNotBlank(defaultAggs)) {
            sql.append(" group by ").append(defaultAggs);
        } else {
            return new ResponseModel(ResponseCode.SUCCESS.getCode(), "针对表" + bateDatas + "，无字段可聚类！");
        }
        return search(new Sql(sql.toString(), sqlSearchParam.getUsername(), sqlSearchParam.getRoleLevel()), null);
    }

    @Override
    public int batchTaskFinish(String taskid) {
        return jdbcTemplate.update(batchTaskUpdateSql, taskid);
    }

    @Override
    public List<List<String>> getFieldsByData(String datacode) {
        List<String> fileCodes = jdbcTemplate.queryForList(fieldCodesByDataSql, String.class, datacode, datacode);
        List<String> fileNames = jdbcTemplate.queryForList(fieldNamesByDataSql, String.class, datacode, datacode);
        return Lists.newArrayList(fileCodes, fileNames);
    }

    @Override
    public Map<String, Object> getDataByCode(String datacode) {
        return jdbcTemplate.queryForMap(dataByCodeSql, datacode);
    }

    private void log(String sql, String explain, String username, long took) {
        logger.debug("耗时:" + took);
        executorService.execute(() -> jdbcTemplate.execute(logSql, new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
            protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException {
                ps.setString(1, "" + UUID.randomUUID().toString().substring(0, 20));
                ps.setString(2, "" + took);
                ps.setString(3, username);
                lobCreator.setClobAsString(ps, 4, sql);
                lobCreator.setClobAsString(ps, 5, explain);
            }
        }));
    }

    private String getIndexsWithRole(int roleLevel, String[] indexs) {
        StringBuilder index = new StringBuilder();
        index.append("('").append(SqlUtil.indexsJoin(indexs, "','")).append("')");
        List<String> lists = jdbcTemplate.queryForList(String.format(indexRoleSql, index), String.class, roleLevel);
        if (lists.size() != index.length()) {
            return StringUtil.join(lists.toArray(), ",");
        }
        return index.toString();
    }
}
