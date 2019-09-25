package org.elasticsearch.jdbc;

import org.elasticsearch.client.Client;
import org.nlpcn.es4sql.Util;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * Created by allwefantasy on 8/30/16.
 */
public class ElasticSearchConnection implements Connection {

    private final QueryExecutor queryExecutor;
    private boolean autoCommit = false;
    private boolean readOnly = true;

    private final Client client;

    // 关闭标识
    private boolean closeStatus = true;
    private int level = ElasticSearchConnection.TRANSACTION_NONE;

    public ElasticSearchConnection(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
        this.client = queryExecutor.getClient();
    }

    public Client getClient() {
        return client;
    }

    @Override
    public Statement createStatement() throws SQLException {
        if (this.client == null) {
            throw new SQLException("Unable to connect on specified urls " + queryExecutor.getUriList());
        }
        return new ElasticSearchStatement(this);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        if (this.client == null) {
            throw new SQLException("Unable to connect on specified urls " + queryExecutor.getUriList());
        }
        return new ElasticSearchPreparedStatement(this, sql);
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

    @Override
    public void commit() throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void rollback() throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void close() throws SQLException {
        closeStatus = true;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return closeStatus;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return null;
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
        return null;
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
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        if (this.client == null) {
            throw new SQLException("Unable to connect on specified urls " + queryExecutor.getUriList());
        }
        return new ElasticSearchStatement(this);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        if (this.client == null) {
            throw new SQLException("Unable to connect on specified urls " + queryExecutor.getUriList());
        }
        return new ElasticSearchPreparedStatement(this, sql);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return null;
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
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
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
        if (this.client == null) {
            throw new SQLException("Unable to connect on specified urls " + queryExecutor.getUriList());
        }
        if (isClosed()) throw new SQLException("Connection closed");
        ElasticSearchStatement st = new ElasticSearchStatement(this);
        return st;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return null;
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return null;
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
        return null;
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
