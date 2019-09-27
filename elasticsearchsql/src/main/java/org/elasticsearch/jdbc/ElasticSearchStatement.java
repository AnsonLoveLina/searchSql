package org.elasticsearch.jdbc;

import org.nlpcn.es4sql.Util;
import org.nlpcn.es4sql.index.IndexAction;
import org.nlpcn.es4sql.jdbc.ObjectResult;
import org.nlpcn.es4sql.Action;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zy-xx on 2019/9/19.
 */
public class ElasticSearchStatement implements Statement {


    private ElasticSearchConnection connection;
    private int maxRowsRS = Integer.MAX_VALUE;

    protected int queryTimeoutSec = 10;
    protected boolean poolable = true;
    protected boolean closeOnCompletion = false;
    protected ResultSet result;
    private List<IndexAction> DMLActions = new ArrayList<>();

    public ElasticSearchStatement(ElasticSearchConnection conn) {
        this.connection = conn;
    }

    public List<IndexAction> getDMLActions() {
        return DMLActions;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        List<String> headers = new ArrayList<>();
        List<List<Object>> lines = new ArrayList<>();
        try {
            ObjectResult extractor = connection.getQueryExecutor().getObjectResult(true, sql, false, false, true);
            headers = extractor.getHeaders();
            lines = extractor.getLines();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.result = new ElasticSearchResultSet(this, headers, lines);

        return this.result;
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        if (connection.getAutoCommit()) {
            return executeQuery(sql).getRow();
        }
        Action action = null;
        try {
            action = connection.getQueryExecutor().getAction(sql);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        if (!(action instanceof IndexAction)) {
            this.connection.removeStatements(this);
            return executeQuery(sql).getRow();
        }
        IndexAction indexAction = (IndexAction) action;
        DMLActions.add(indexAction);
        return indexAction.getCount();
    }

    @Override
    public void close() throws SQLException {
        DMLActions.clear();
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return 0;
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {

    }

    @Override
    public int getMaxRows() throws SQLException {
        return this.maxRowsRS;
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        this.maxRowsRS = max;
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {

    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return queryTimeoutSec;
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        this.queryTimeoutSec = seconds;
    }

    @Override
    public void cancel() throws SQLException {

    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public void setCursorName(String name) throws SQLException {

    }

    @Override
    public boolean execute(String sql) throws SQLException {
        Action action = null;
        try {
            action = connection.getQueryExecutor().getAction(sql);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if (!(action instanceof IndexAction)) {
            this.connection.removeStatements(this);
            return executeQuery(sql) != null;
        } else {
            executeUpdate(sql);
            return true;
        }
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return this.result;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return -1;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return false;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public int getFetchDirection() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {

    }

    @Override
    public int getFetchSize() throws SQLException {
        return Util.getIntProp(getConnection().getClientInfo(), Util.PROP_FETCH_SIZE, 10000);
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public int getResultSetType() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        try {
            for (IndexAction dmlAction : DMLActions) {
                this.connection.add(dmlAction);
            }
        } catch (Exception e) {
            throw new SQLException(e.getCause());
        }
    }

    @Override
    public void clearBatch() throws SQLException {
        this.connection.rollback();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        this.connection.cleartatements();
        this.connection.commit();
        return new int[0];
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.connection;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return false;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return this.executeUpdate(sql);
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return this.executeUpdate(sql);
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return this.executeUpdate(sql);
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return this.execute(sql);
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return this.execute(sql);
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return 0;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return connection.isClosed();
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        this.poolable = poolable;
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return poolable;
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        this.closeOnCompletion = true;

    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return closeOnCompletion;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
}
