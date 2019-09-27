package com.ngw.config;

/**
 * Created by zy-xx on 2019/9/18.
 */

import com.alibaba.druid.pool.ElasticSearchDruidDataSourceBuilder;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author
 * @date 2018/10/10
 */
@Configuration
public class DataSourceConfig {

    @Bean(name = "oracleDataSource")
    @Qualifier("oracleDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    public DataSource oracleDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "esDataSource")
    @Qualifier("esDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.es")
    public DataSource esDataSource() throws Exception {
        return new ElasticSearchDruidDataSourceWrapper();
    }

    @Bean(name = "oracleJdbcTemplate")
    public JdbcTemplate oracleJdbcTemplate(
            @Qualifier("oracleDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "esJdbcTemplate")
    public JdbcTemplate esJdbcTemplate(
            @Qualifier("esDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
