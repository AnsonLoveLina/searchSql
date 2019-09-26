package org.elasticsearch.jdbc;

import org.elasticsearch.plugin.nlpcn.QueryActionElasticExecutor;
import org.elasticsearch.plugin.nlpcn.executors.CsvExtractorException;
import org.nlpcn.es4sql.SearchDao;
import org.nlpcn.es4sql.Util;
import org.nlpcn.es4sql.exception.SqlParseException;
import org.nlpcn.es4sql.jdbc.ObjectResult;
import org.nlpcn.es4sql.jdbc.ObjectResultsExtractor;
import org.nlpcn.es4sql.query.Action;
import org.nlpcn.es4sql.query.QueryAction;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
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

    public ElasticSearchStatement(ElasticSearchConnection conn) {
        this.connection = conn;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        List<String> headers = new ArrayList<>();
        List<List<Object>> lines = new ArrayList<>();
        try {
            ObjectResult extractor = getObjectResult(true, sql, false, false, true);
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
        List<String> headers = new ArrayList<>();
        List<List<Object>> lines = new ArrayList<>();
        try {
            ObjectResult extractor = getObjectResult(true, sql, false, false, true);
            headers = extractor.getHeaders();
            lines = extractor.getLines();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.result = new ElasticSearchResultSet(this, headers, lines);

        return lines.size();
    }

    @Override
    public void close() throws SQLException {

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
        List<String> headers = new ArrayList<>();
        List<List<Object>> lines = new ArrayList<>();
        try {
            ObjectResult extractor = getObjectResult(true, sql, false, false, true);
            headers = extractor.getHeaders();
            lines = extractor.getLines();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.result = new ElasticSearchResultSet(this, headers, lines);
        return this.result != null;
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

    }

    @Override
    public void clearBatch() throws SQLException {

    }

    @Override
    public int[] executeBatch() throws SQLException {
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

    private ObjectResult getObjectResult(boolean flat, String query, boolean includeScore, boolean includeType, boolean includeId) throws SqlParseException, SQLFeatureNotSupportedException, Exception, CsvExtractorException {
        SearchDao searchDao = new SearchDao(connection.getClient());

        //String rewriteSQL = searchDao.explain(getSql()).explain().explain();

        Action queryAction = searchDao.explain(query);
        Object execution = QueryActionElasticExecutor.executeAnyAction(searchDao.getClient(), queryAction);
        return new ObjectResultsExtractor(includeScore, includeType, includeId, false, queryAction).extractResults(execution, flat);
    }
}
