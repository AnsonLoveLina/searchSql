package com.alibaba.druid.pool;

import java.util.Properties;

/**
 * Created by zy-xx on 2019/9/18.
 */
public class ElasticSearchDruidDataSourceBuilder {

    public static ElasticSearchDruidDataSourceBuilder create() {
        return new ElasticSearchDruidDataSourceBuilder();
    }

    /**
     * For build multiple DruidDataSource, detail see document.
     */
    public ElasticSearchDruidDataSource build(Properties properties) throws Exception {
        return (ElasticSearchDruidDataSource) ElasticSearchDruidDataSourceFactory.createDataSource(properties);
    }
}
