package org.nlpcn.es4sql.index;

import com.sun.tools.javac.util.Assert;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.nlpcn.es4sql.domain.Insert;
import org.nlpcn.es4sql.exception.SqlParseException;
import org.nlpcn.es4sql.Action;
import org.nlpcn.es4sql.query.SqlElasticRequestBuilder;

/**
 * Created by zy-xx on 2019/9/26.
 * update by query和delete by query不算在index中，所以只有insert对于ES而言是insertOrUpdate
 */
public class InsertAction implements IndexAction, Action {
    private Client client;
    private Insert insert;
    private IndexRequestBuilder request;

    public InsertAction(Client client, Insert insert) {
        Assert.checkNonNull(client);
        Assert.checkNonNull(insert);
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

    public Insert getInsert() {
        return insert;
    }

    private void setValues() {
        this.request.setSource(insert.getValues());
    }

    @Override
    public int getCount() {
        return insert.getValues() == null ? 0 : 1;
    }
}
