package com.alibaba.druid.pool;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
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
import java.util.Properties;

public class QueryExecutor {

    private Client client;
    private final URI uri;
    private final Properties info;


    public QueryExecutor(URI uri, Properties info) {
        this.uri = uri;
        this.info = info;
        buildClient();
    }

    public Object startQuery(String query) throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        SearchDao searchDao = new org.nlpcn.es4sql.SearchDao(client);

        Action queryAction = searchDao.explain(query);
        Object execution = QueryActionElasticExecutor.executeAnyAction(searchDao.getClient(), queryAction);
        return execution;
    }

    private void buildClient() {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    Settings.Builder builder = Settings.builder();
                    info.forEach((k, v) -> builder.put(k.toString(), v.toString()));

                    TransportAddress[] addresses = new TransportAddress[1];
                    try {
                        addresses[0] = new TransportAddress(InetAddress.getByName(uri.getHost()), uri.getPort());
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }

                    client = new PreBuiltXPackTransportClient(builder.build()).addTransportAddresses(addresses);
                }
            }
        }
    }


    public void close() {

    }

}
