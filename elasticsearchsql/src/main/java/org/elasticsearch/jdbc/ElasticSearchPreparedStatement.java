package org.elasticsearch.jdbc;

import org.elasticsearch.plugin.nlpcn.QueryActionElasticExecutor;
import org.elasticsearch.plugin.nlpcn.executors.CsvExtractorException;
import org.nlpcn.es4sql.SearchDao;
import org.nlpcn.es4sql.Util;
import org.nlpcn.es4sql.exception.SqlParseException;
import org.nlpcn.es4sql.jdbc.ObjectResult;
import org.nlpcn.es4sql.jdbc.ObjectResultsExtractor;
import org.nlpcn.es4sql.query.QueryAction;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ElasticSearchPreparedStatement extends ElasticSearchStatement implements PreparedStatement {

    private Object[] sqlAndParams;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private ElasticSearchConnection connection;

    public ElasticSearchPreparedStatement(ElasticSearchConnection conn, String sql) {
        super(conn);
        this.connection = conn;
        sql = sql.trim();

        String[] parts = (sql + ";").split("\\?");
        this.sqlAndParams = new Object[parts.length * 2 - 1];
        for (int i = 0; i < parts.length; i++) {
            this.sqlAndParams[i * 2] = parts[i];
        }
    }

    /**
     * Builds the final sql statement
     * @return
     * @throws SQLException
     */
    private String buildSql() throws SQLException{
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<sqlAndParams.length; i++)try {
            if(sqlAndParams[i] instanceof Date){
                sb.append("'"+dateFormat.format((Date)sqlAndParams[i])+"' ");
            }else{
                sb.append(sqlAndParams[i]+" ");
            }
        }catch(Exception e){
            throw new SQLException("Unable to create SQL statement, ["+i+"] = "+sqlAndParams[i]+" : "+e.getMessage(), e);
        }
        return sb.substring(0, sb.length()-2);
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        if (super.execute(this.buildSql())) return getResultSet();
        else return null;
    }

    @Override
    public int executeUpdate() throws SQLException {
        return super.executeUpdate(this.buildSql());
	    /*		throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());*/
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        this.sqlAndParams[(parameterIndex*2) - 1] = null;
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        this.sqlAndParams[(parameterIndex*2) - 1] = Boolean.valueOf(x);

    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        this.sqlAndParams[(parameterIndex*2) - 1] = Byte.valueOf(x);

    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        this.sqlAndParams[(parameterIndex*2) - 1] = Short.valueOf(x);
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        this.sqlAndParams[(parameterIndex*2) - 1] = Integer.valueOf(x);
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        this.sqlAndParams[(parameterIndex*2) - 1] = Long.valueOf(x);
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        this.sqlAndParams[(parameterIndex*2) - 1] = Float.valueOf(x);
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        this.sqlAndParams[(parameterIndex*2) - 1] = Double.valueOf(x);
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        this.sqlAndParams[(parameterIndex*2) - 1] = x;

    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        // TODO: escape
        this.sqlAndParams[(parameterIndex*2) - 1] = "'"+x+"'";
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        this.sqlAndParams[(parameterIndex*2) - 1] = x;
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        this.sqlAndParams[(parameterIndex*2) - 1] = x;
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        this.sqlAndParams[(parameterIndex*2) - 1] = Long.valueOf(x.getTime());
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        this.sqlAndParams[(parameterIndex*2) - 1] = Long.valueOf(x.getTime());

    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void clearParameters() throws SQLException {
        for(int i=1; i<sqlAndParams.length; i+=2) sqlAndParams[i] = null;
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        if(x instanceof String) this.sqlAndParams[(parameterIndex*2) - 1] = "'"+x+"'";
        else this.sqlAndParams[(parameterIndex*2) - 1] = x;
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        if(x instanceof String ) this.sqlAndParams[(parameterIndex*2) - 1] = "'"+x+"'";
        else this.sqlAndParams[(parameterIndex*2) - 1] = x;
    }

    @Override
    public boolean execute() throws SQLException {
        return super.execute(this.buildSql());
    }

    @Override
    public void addBatch() throws SQLException {
        super.addBatch(this.buildSql());
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        this.sqlAndParams[(parameterIndex*2) - 1] = x;
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return result.getMetaData();
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        this.sqlAndParams[(parameterIndex*2) - 1] = null;
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        this.sqlAndParams[(parameterIndex*2) - 1] = x;

    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return new ESParameterMetaData(sqlAndParams);
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        this.sqlAndParams[(parameterIndex*2) - 1] = value;
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }


    @Override
    public void addBatch(String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

    private ObjectResult getObjectResult(boolean flat, String query, boolean includeScore, boolean includeType, boolean includeId) throws SqlParseException, SQLFeatureNotSupportedException, Exception, CsvExtractorException {
        SearchDao searchDao = new SearchDao(connection.getClient());

        //String rewriteSQL = searchDao.explain(getSql()).explain().explain();

        QueryAction queryAction = searchDao.explain(query);
        Object execution = QueryActionElasticExecutor.executeAnyAction(searchDao.getClient(), queryAction);
        return new ObjectResultsExtractor(includeScore, includeType, includeId, false, queryAction).extractResults(execution, flat);
    }
}
