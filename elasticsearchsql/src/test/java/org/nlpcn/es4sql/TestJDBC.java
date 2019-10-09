package org.nlpcn.es4sql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zy-xx on 2019/9/18.
 */
public class TestJDBC {

    private static final String my_index_relation = "my_index_relation";

    private static final String my_index = "my_index";

    private static String query1 = "SELECT * from  " + my_index + " limit 0,10";

    private static String query2 = "SELECT parent,fieldA from  " + my_index_relation + " limit 0,10";

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

    private static String user = "elastic";
    private static String password = "xx198742";
    private static String param = "";
    private static java.util.Properties info = new java.util.Properties();

    static{
        param = "xpack.security.user="+user + ":" + password+"&xpack.security.transport.ssl.enabled=true&xpack.security.transport.ssl.verification_mode=certificate&xpack.security.transport.ssl.keystore.path=/Users/zy-xx/Documents/学习/elasticSearch/6/elasticsearch/elastic-certificates.p12&xpack.security.transport.ssl.truststore.path=/Users/zy-xx/Documents/学习/elasticSearch/6/elasticsearch/elastic-certificates.p12";
        info.put("xpack.security.user", user + ":" + password);
        info.put("xpack.security.transport.ssl.enabled", "true");
        info.put("xpack.security.transport.ssl.verification_mode", "certificate");
        info.put("xpack.security.transport.ssl.keystore.path", "/Users/zy-xx/Documents/学习/elasticSearch/6/elasticsearch/elastic-certificates.p12");
        info.put("xpack.security.transport.ssl.truststore.path", "/Users/zy-xx/Documents/学习/elasticSearch/6/elasticsearch/elastic-certificates.p12");
    }

    @org.junit.Test
    public void test() throws Exception {

//        System.out.println("sql" + insertSql5 + ":\n----------\n" + Util.sqlToEsQuery(query1));
        testInsertStatement();
        testInsertPrepareStatement();
        testQueryStatement();
        testQueryPrepareStatement();
    }

    @org.junit.Test
    public void testInsertPrepareStatement() throws Exception {
        Class.forName("org.elasticsearch.jdbc.ElasticSearchDriver");
        Connection connection = DriverManager.getConnection("jdbc:elasticsearch://localhost:9300/");
        PreparedStatement ps = connection.prepareStatement(insertSql6);
        int result = ps.executeUpdate();
        System.out.println("result = " + result);
        connection.rollback();
        Statement statement = connection.createStatement();
        int result1 = statement.executeUpdate(insertSql6);
        System.out.println("result1 = " + result1);
        connection.commit();
        ps.close();
        connection.close();
    }

    @org.junit.Test
    public void testInsertStatement() throws Exception {
        Class.forName("org.elasticsearch.jdbc.ElasticSearchDriver");
        Connection connection = DriverManager.getConnection("jdbc:elasticsearch://localhost:9300");
        connection.setAutoCommit(false);
        Statement statement = connection.createStatement();
        int result = statement.executeUpdate(insertSql6);
        System.out.println("result = " + result);
        connection.commit();
        statement.close();
        connection.close();
    }

    @org.junit.Test
    public void testQueryStatement() throws Exception {
        Class.forName("org.elasticsearch.jdbc.ElasticSearchDriver");
        Connection connection = DriverManager.getConnection("jdbc:elasticsearch://localhost:9300?"+param);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query1);
        while (resultSet.next()) {
            System.out.println(resultSet.getString("key"));
        }
        statement.close();
        connection.close();
    }

    @org.junit.Test
    public void testQueryPrepareStatement() throws Exception {
        Class.forName("org.elasticsearch.jdbc.ElasticSearchDriver");
        Connection connection = DriverManager.getConnection("jdbc:elasticsearch://localhost:9300/");
        PreparedStatement ps = connection.prepareStatement(query2);
        ResultSet resultSet = ps.executeQuery();
        List<String> result = new ArrayList<String>();
        while (resultSet.next()) {
            System.out.println(resultSet.getObject("parent"));
            System.out.println(resultSet.getString("fieldA"));
        }
        ps.close();
        connection.close();
    }
}
