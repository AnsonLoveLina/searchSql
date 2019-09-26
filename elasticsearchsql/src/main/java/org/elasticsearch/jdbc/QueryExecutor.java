package org.elasticsearch.jdbc;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.plugin.nlpcn.QueryActionElasticExecutor;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.nlpcn.es4sql.SearchDao;
import org.nlpcn.es4sql.exception.SqlParseException;
import org.nlpcn.es4sql.query.Action;
import org.nlpcn.es4sql.query.QueryAction;

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
    private static QueryExecutor queryExecutor;

    public static QueryExecutor createQueryExecutor(String url, Properties info) throws SQLException {
        if (queryExecutor == null) {
            queryExecutor = new QueryExecutor(url, info);
        }
        return queryExecutor;
    }

    public QueryExecutor(String url, Properties info) throws SQLException {
        Object[] result = ESJDBCUtil.parseURL(url, info);
        this.uriList = (List<URI>) result[0];
        this.info = (Properties) result[1];
        buildClient();
    }

    public Object startQuery(String query) throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        SearchDao searchDao = new SearchDao(client);

        Action queryAction = searchDao.explain(query);
        Object execution = QueryActionElasticExecutor.executeAnyAction(searchDao.getClient(), queryAction);
        return execution;
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
