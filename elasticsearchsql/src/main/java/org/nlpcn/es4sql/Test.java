package org.nlpcn.es4sql;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ElasticSearchDruidDataSourceFactory;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.threadpool.ThreadPool;
import org.nlpcn.es4sql.query.SqlElasticRequestBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * Created by allwefantasy on 8/18/16.
 */
public class Test {

    private static final String my_index_relation = "my_index_relation";

    private static final String my_index = "my_index";

    private static String query2 = "SELECT /*! HIGHLIGHT(*,pre_tags:['<em>'],post_tags:['</em>'])*/* from ['v_znb_05_kzzyga009_new'] where ll=matchphrase('网吧')";

    private static String query1 = "SELECT parent,fieldA from  " + my_index_relation + " limit 0,10";

    private static String query3 = "SELECT /*! HIGHLIGHT(*,pre_tags:['<em>'],post_tags:['</em>'])*/* from  \" + my_index + \" where key='value' group by key.keyword";

    private static String insertSql1 = "insert into " + my_index + " values(true,'1990-01-01 12:11:11',11.1,'41.12,-71.34',1,'是的',123213,'中华人民共和国')";

    private static String insertSql2 = "insert into " + my_index + "(fieldDate,fieldKeyword,fieldText) values('1990-01-01 12:11:11','是的','中华人民共和国')";

    private static String insertSql3 = "insert into " + my_index + "(fieldDate,fieldText) values('1990-01-01','中华人民共和国')";

    private static String insertSql4 = "insert into " + my_index + "(fieldDate,fieldText) values('1990-01-01 12','中华人民共和国')";

    private static String insertSql5 = "insert into " + my_index + "(_id,fieldBoolean,fieldDate,fieldDouble,fieldGeoPoin,fieldInteger,fieldKeyword,fieldLong,fieldText) values('dsfd3334234234',true,'1990-01-01',11.1,'41.12,-71.34',1,'是的',123213,'中华人民共和国')";

    private static String insertSql6 = "insert into " + my_index + "(fieldBoolean,fieldDate,fieldDouble,fieldGeoPoin,fieldInteger,fieldKeyword,fieldLong,fieldText) values(true,'1990-01-01',11.1,'41.12,-71.34',1,'是的',123213,'中华人民共和国')";

    private static String updateSql1 = "update " + my_index + " set fieldDate='1990-01-01 12',fieldText='中华人民共和国'";

    private static String updateSql2 = "update " + my_index + " set fieldDate='1990-01-01 12',fieldText='中华人民共和国' where fieldKeyword='是的'";

    private static String updateSql3 = "update " + my_index + " set fieldDate='1990-01-01 12',fieldText='中华人民共和国' where q=query('中华')";

    private static String updateSql4 = "update " + my_index + " set fieldBoolean=false,fieldDate='1990-01-01 12',fieldDouble=1.11,fieldGeoPoin='41.12,-71.34',fieldInteger=2,fieldKeyword='是的',fieldLong=2320909,fieldText='中华人民共和国'";


    public static String sqlToEsQuery(String sql) throws Exception {
        Map actions = new HashMap();
        Settings settings = Settings.builder().build();

        ThreadPool threadPool = new ThreadPool(settings);
        Client client = new NodeClient(settings, threadPool);
        SearchDao searchDao = new org.nlpcn.es4sql.SearchDao(client);
        try {
            SearchRequest searchRequest = (SearchRequest)searchDao.explain(sql).explain().request();
            return searchDao.explain(sql).explain().explain();
        } catch (Exception e) {
            throw e;
        }
    }

    public static void main(String[] args) throws Exception {
        String sql = "SELECT u as u2,count(distinct(mid)) as count FROM panda_quality where ty='buffer' and day='20160816' and tm>1471312800.00 and tm<1471313100.00 and domain='http://pl10.live.panda.tv' group by u  order by count desc limit 5000";
//        sql = "SELECT sum(num) as num2,newtype as nt  from  twitter2 group by nt  order by num2 ";
//        System.out.println("sql" + sql + ":\n" + sqlToEsQuery(sql));
//
//        sql = "SELECT sum(num_d) as num2,split(newtype,',') as nt  from  twitter2 group by nt  order by num2 ";
//
//        System.out.println("sql" + sql + ":\n" + sqlToEsQuery(sql));
//
//        sql = "SELECT newtype as nt  from  twitter2  ";
//
//        System.out.println("sql" + sql + ":\n----------\n" + sqlToEsQuery(sql));
//
//        sql = "SELECT sum(num_d) as num2,floor(num) as nt  from  twitter2 group by floor(num),newtype  order by num2 ";
//
//        System.out.println("sql" + sql + ":\n----------\n" + sqlToEsQuery(sql));
//
//        sql = "SELECT split('newtype','b')[1] as nt,sum(num_d) as num2   from  twitter2 group by nt ";
//
//        System.out.println("sql" + sql + ":\n----------\n" + sqlToEsQuery(sql));
//
//        sql = "SELECT concat_ws('dd','newtype','num_d') as num2   from  twitter2";
//        System.out.println("sql" + sql + ":\n----------\n" + sqlToEsQuery(sql));
//
//        sql = "SELECT split(split('newtype','c')[0],'b')[0] as num2   from  twitter2";
//        System.out.println("sql" + sql + ":\n----------\n" + sqlToEsQuery(sql));
//
//        sql = "SELECT floor(split(substring('newtype',0,3),'c')[0]) as num2   from  twitter2";
//        System.out.println("sql" + sql + ":\n----------\n" + sqlToEsQuery(sql));
//
//        sql = "SELECT split(substring('newtype',0,3),'c')[0] as nt,num_d   from  twitter2 group by nt";
//        System.out.println("sql" + sql + ":\n----------\n" + sqlToEsQuery(sql));
//
//        sql = "SELECT floor(num_d) as nt from  twitter2 ";
//        System.out.println("sql" + sql + ":\n----------\n" + sqlToEsQuery(sql));
//
//        sql = "SELECT trim(newtype) as nt from  twitter2 ";
//        System.out.println("sql" + sql + ":\n----------\n" + sqlToEsQuery(sql));
//
//        sql = "SELECT trim(concat_ws('dd',newtype,num_d)) as nt from  twitter2 ";
//        System.out.println("sql" + sql + ":\n----------\n" + sqlToEsQuery(sql));
//
//
//        sql = "SELECT split(trim(concat_ws('dd',newtype,num_d)),'dd')[0] as nt from  twitter2 ";
//        System.out.println("sql" + sql + ":\n----------\n" + sqlToEsQuery(sql));

//        sql = "SELECT floor(" +
//                "floor(substring(newtype,0,14)/100)/5)*5 as key," +
//                "count(distinct(num)) cvalue FROM twitter2 " +
//                "group by key ";
        SqlElasticRequestBuilder query = Util.sqlToEsQuery(query2);
        System.out.println("query = " + query.explain());

    }
}
