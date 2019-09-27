package org.elasticsearch.jdbc;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.nlpcn.es4sql.Util;
import org.nlpcn.es4sql.index.IndexAction;
import org.nlpcn.es4sql.index.InsertAction;

import java.sql.*;
import java.util.*;
import java.util.concurrent.Executor;

/**
 * Created by allwefantasy on 8/30/16.
 * 同一个connection必须线程安全
 */
public class ElasticSearchConnection implements Connection {

    private BulkRequestBuilder bulkRequest;

    private final QueryExecutor queryExecutor;
    private boolean autoCommit = false;
    private boolean readOnly = true;

    private final Client client;
    private final Set<ElasticSearchStatement> statements = new HashSet<>();

    // 关闭标识
    private boolean closeStatus = false;
    private int level = ElasticSearchConnection.TRANSACTION_NONE;

    public ElasticSearchConnection(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
        this.client = queryExecutor.getClient();
        this.bulkRequest = this.client.prepareBulk();
    }

    public QueryExecutor getQueryExecutor() {
        return queryExecutor;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public Statement createStatement() throws SQLException {
        if (this.client == null) {
            throw new SQLException("Unable to connect on specified urls " + queryExecutor.getUriList());
        }
        ElasticSearchStatement st = new ElasticSearchStatement(this);
        statements.add(st);
        return st;
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        if (this.client == null) {
            throw new SQLException("Unable to connect on specified urls " + queryExecutor.getUriList());
        }
        ElasticSearchPreparedStatement st = new ElasticSearchPreparedStatement(this,sql);
        statements.add(st);
        return st;
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return sql;
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        this.autoCommit = autoCommit;
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return autoCommit;
    }

    protected void add(IndexAction dmlAction) throws Exception {
        queryExecutor.add(dmlAction, bulkRequest);
    }

    @Override
    public void commit() throws SQLException {
        try {
            for (ElasticSearchStatement st : this.statements) {
                for (IndexAction dmlAction : st.getDMLActions()) {
                    add(dmlAction);
                }
                st.getDMLActions().clear();
            }
            cleartatements();
            queryExecutor.commit(bulkRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rollback() throws SQLException {
        bulkRequest = this.client.prepareBulk();
        cleartatements();
    }

    @Override
    public void close() throws SQLException {
        if (isClosed()) return;
        closeStatus = true;
        for (ElasticSearchStatement st : this.statements) st.close();
        cleartatements();
        client.close();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return closeStatus;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        String host = ((TransportClient) client).transportAddresses().get(0).address().getHostName();
        int port = ((TransportClient) client).transportAddresses().get(0).address().getPort();
        return new ESDatabaseMetaData(host, port, client, this.getClientInfo(), this);
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        this.readOnly = readOnly;
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return readOnly;
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public String getCatalog() throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        this.level = level;
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return level;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return createStatement();
    }

    protected void cleartatements() {
        statements.clear();
    }

    protected void removeStatements(ElasticSearchStatement st) {
        statements.remove(st);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public int getHoldability() throws SQLException {
        return 6;
//        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return this.prepareStatement(sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return this.prepareStatement(sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return this.prepareStatement(sql);
    }

    @Override
    public Clob createClob() throws SQLException {
        return null;
    }

    @Override
    public Blob createBlob() throws SQLException {
        return null;
    }

    @Override
    public NClob createNClob() throws SQLException {
        return null;
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return null;
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return false;
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {

    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {

    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return null;
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return null;
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return null;
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return null;
    }

    @Override
    public void setSchema(String schema) throws SQLException {

    }

    @Override
    public String getSchema() throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void abort(Executor executor) throws SQLException {

    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {

    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return 0;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }
}
