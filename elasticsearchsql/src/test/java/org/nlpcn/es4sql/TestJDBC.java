package org.nlpcn.es4sql;

import com.alibaba.druid.pool.DruidDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.alibaba.druid.pool.DruidDataSourceFactory.PROP_CONNECTIONPROPERTIES;

/**
 * Created by zy-xx on 2019/9/18.
 */
public class TestJDBC {

    private static final String my_index_relation = "my_index_relation";

    private static final String my_index = "my_index";

    String insertSql1 = "insert into " + my_index + " values(true,'1990-01-01 12:11:11',11.1,'41.12,-71.34',1,'是的',123213,'中华人民共和国')";

    String insertSql2 = "insert into " + my_index + "(fieldDate,fieldKeyword,fieldText) values('1990-01-01 12:11:11','是的','中华人民共和国')";

    String insertSql3 = "insert into " + my_index + "(fieldDate,fieldText) values('1990-01-01 12:11:11','中华人民共和国')";

    String insertSql4 = "insert into " + my_index + "(fieldDate,fieldText) values('1990-01-01 12','中华人民共和国')";

    String insertSql5 = "insert into " + my_index + "(fieldBoolean,fieldDate,fieldDouble,fieldGeoPoin,fieldInteger,fieldKeyword,fieldLong,fieldText) values(true,'1990-01-01 12:11:11',11.1,'41.12,-71.34',1,'是的',123213,'中华人民共和国')";

    String updateSql1 = "update " + my_index + " set fieldDate='1990-01-01 12',fieldText='中华人民共和国'";

    String updateSql2 = "update " + my_index + " set fieldDate='1990-01-01 12',fieldText='中华人民共和国' where fieldKeyword='是的'";

    String updateSql3 = "update " + my_index + " set fieldDate='1990-01-01 12',fieldText='中华人民共和国' where q=query('中华')";

    String updateSql4 = "update " + my_index + " set fieldBoolean=false,fieldDate='1990-01-01 12',fieldDouble=1.11,fieldGeoPoin='41.12,-71.34',fieldInteger=2,fieldKeyword='是的',fieldLong=2320909,fieldText='中华人民共和国'";

    @org.junit.Test
    public void testInsert() throws Exception {
        Class.forName("org.elasticsearch.jdbc.ElasticSearchDriver");
        Connection connection = DriverManager.getConnection("jdbc:elasticsearch://localhost:9300/");
        Statement statement = connection.createStatement();
        int result = statement.executeUpdate(insertSql2);
        System.out.println("result = " + result);
        statement.close();
        connection.close();
    }

    @org.junit.Test
    public void testStatement() throws Exception {
        Class.forName("org.elasticsearch.jdbc.ElasticSearchDriver");
        Connection connection = DriverManager.getConnection("jdbc:elasticsearch://localhost:9300/");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT parent,fieldA from  " + my_index_relation + " limit 0,10");
        while (resultSet.next()) {
            System.out.println(resultSet.getObject("parent"));
            System.out.println(resultSet.getString("fieldA"));
        }
        statement.close();
        connection.close();
    }

    @org.junit.Test
    public void testPrepareStatement() throws Exception {
        Class.forName("org.elasticsearch.jdbc.ElasticSearchDriver");
        Connection connection = DriverManager.getConnection("jdbc:elasticsearch://localhost:9300/");
        PreparedStatement ps = connection.prepareStatement("SELECT parent,fieldA from  " + my_index_relation + " limit 0,10");
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
