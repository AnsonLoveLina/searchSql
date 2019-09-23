package org.nlpcn.es4sql;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ElasticSearchDruidDataSourceFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.alibaba.druid.pool.DruidDataSourceFactory.PROP_CONNECTIONPROPERTIES;

/**
 * Created by zy-xx on 2019/9/18.
 */
public class TestJDBC {

    @org.junit.Test
    public void test() {
        try {
            TestJDBC.testJDBC();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String my_index_dynamic = "my_index_dynamic";

    public static void testJDBC() throws Exception {
        Properties properties = new Properties();
        properties.put("url", "jdbc:elasticsearch://127.0.0.1:9300/");
        properties.put(PROP_CONNECTIONPROPERTIES, "client.transport.ignore_cluster_name=true");
        DruidDataSource dds = (DruidDataSource) ElasticSearchDruidDataSourceFactory.createDataSource(properties);
        Connection connection = dds.getConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT parent,fieldA from  " + my_index_dynamic + " limit 1,10");
        ResultSet resultSet = ps.executeQuery();
        List<String> result = new ArrayList<String>();
        while (resultSet.next()) {
            System.out.println(resultSet.getObject("parent"));
            System.out.println(resultSet.getString("fieldA"));
        }
        ps.close();
        connection.close();
        dds.close();
    }

}
