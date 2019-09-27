package org.elasticsearch.jdbc;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.plugin.nlpcn.QueryActionElasticExecutor;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.nlpcn.es4sql.SearchDao;
import org.nlpcn.es4sql.exception.SqlParseException;
import org.nlpcn.es4sql.Action;
import org.nlpcn.es4sql.index.IndexAction;
import org.nlpcn.es4sql.index.InsertAction;
import org.nlpcn.es4sql.jdbc.ObjectResult;
import org.nlpcn.es4sql.jdbc.ObjectResultsExtractor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.List;
import java.util.Properties;

public class QueryExecutor {

    private Client client;
    private final List<URI> uriList;
    private final Properties info;

    public QueryExecutor(String url, Properties info) throws SQLException {
        Object[] result = ESJDBCUtil.parseURL(url, info);
        this.uriList = (List<URI>) result[0];
        this.info = (Properties) result[1];
        buildClient();
    }

    public ObjectResult getObjectResult(boolean flat, String query, boolean includeScore, boolean includeType, boolean includeId) throws Exception {
        SearchDao searchDao = new SearchDao(this.client);

        //String rewriteSQL = searchDao.explain(getSql()).explain().explain();

        Action queryAction = searchDao.explain(query);
        Object execution = QueryActionElasticExecutor.executeAnyAction(searchDao.getClient(), queryAction);
        return new ObjectResultsExtractor(includeScore, includeType, includeId, false, queryAction).extractResults(execution, flat);
    }

    public Action getAction(String query) throws Exception {
        SearchDao searchDao = new SearchDao(this.client);

        //String rewriteSQL = searchDao.explain(getSql()).explain().explain();

        Action action = searchDao.explain(query);
        return action;
    }

    public void add(IndexAction action, BulkRequestBuilder bulkRequestBuilder) throws Exception {
        bulkRequestBuilder.add((IndexRequestBuilder) action.explain().getBuilder());
    }

    public void commit(BulkRequestBuilder bulkRequestBuilder) throws Exception {
        bulkRequestBuilder.execute().actionGet();
    }

    private void buildClient() throws SQLException {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    Settings.Builder builder = Settings.builder();
                    info.forEach((k, v) -> builder.put(k.toString(), v.toString()));

                    TransportAddress[] addresses = new TransportAddress[uriList.size()];
                    try {
                        for (int i = 0; i < addresses.length; ++i) {
                            addresses[i] = new TransportAddress(InetAddress.getByName(uriList.get(i).getHost()), uriList.get(i).getPort());
                        }
                    } catch (UnknownHostException e) {
                        throw new SQLException(e);
                    }

                    client = new PreBuiltXPackTransportClient(builder.build()).addTransportAddresses(addresses);
                }
            }
        }
    }

    public List<URI> getUriList() {
        return uriList;
    }

    public Client getClient() {
        return client;
    }

    public void close() {

        // close elasticsearch client
        if (this.client != null) {
            this.client.close();
            this.client = null;
        }
    }

}