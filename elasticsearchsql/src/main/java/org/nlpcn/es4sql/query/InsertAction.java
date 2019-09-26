package org.nlpcn.es4sql.query;

import jodd.util.StringUtil;
import org.elasticsearch.action.index.IndexAction;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.nlpcn.es4sql.domain.Insert;
import org.nlpcn.es4sql.domain.Query;
import org.nlpcn.es4sql.exception.SqlParseException;

/**
 * Created by zy-xx on 2019/9/26.
 */
public class InsertAction implements Action {
    private Client client;
    private Insert insert;
    private IndexRequestBuilder request;

    public InsertAction(Client client, Insert insert) {
        this.client = client;
        this.insert = insert;
    }

    @Override
    public SqlElasticRequestBuilder explain() throws SqlParseException {
        this.request = client.prepareIndex(insert.getIndex(), insert.getType(), insert.getId());

        setValues();

        SqlElasticInsertRequestBuilder sqlElasticInsertRequestBuilder = new SqlElasticInsertRequestBuilder(request);
        return sqlElasticInsertRequestBuilder;
    }

    private void setValues() {
        this.request.setSource(insert.getValues());
    }
}
