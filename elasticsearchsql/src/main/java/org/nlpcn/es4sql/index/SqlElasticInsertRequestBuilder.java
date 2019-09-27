package org.nlpcn.es4sql.index;

import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionRequestBuilder;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.nlpcn.es4sql.query.SqlElasticRequestBuilder;

/**
 * Created by zy-xx on 2019/9/26.
 */
public class SqlElasticInsertRequestBuilder implements SqlElasticRequestBuilder {
    private IndexRequestBuilder indexRequestBuilder;

    public SqlElasticInsertRequestBuilder(IndexRequestBuilder indexRequestBuilder) {
        this.indexRequestBuilder = indexRequestBuilder;
    }

    @Override
    public ActionRequest request() {
        return indexRequestBuilder.request();
    }

    @Override
    public String explain() {
        try {
            if (request() != null) {
                return indexRequestBuilder.request().source().toString();
            }
            return indexRequestBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ActionResponse get() {
        return indexRequestBuilder.get();
    }

    @Override
    public ActionRequestBuilder getBuilder() {
        return indexRequestBuilder;
    }

    @Override
    public String toString() {
        try {
            if (request() != null) {
                return indexRequestBuilder.request().source().toString();
            }
            return indexRequestBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}