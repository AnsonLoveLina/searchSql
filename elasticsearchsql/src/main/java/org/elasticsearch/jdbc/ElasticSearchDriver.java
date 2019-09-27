package org.elasticsearch.jdbc;

import org.nlpcn.es4sql.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Properties;

/**
 * Basic {@link Driver} implementation used to get {@link ElasticSearchConnection}.
 *
 * @author cversloot
 */
public class ElasticSearchDriver implements Driver {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchDriver.class.getName());

    /**
     * Register this driver with the driver manager
     */
    static {
        try {
            DriverManager.registerDriver(new ElasticSearchDriver());
        } catch (SQLException sqle) {
            logger.error("Unable to register Driver", sqle);
        }
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        QueryExecutor queryExecutor = new QueryExecutor(url,info);
        return new ElasticSearchConnection(queryExecutor);
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return ESJDBCUtil.acceptsURL(url);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return ESJDBCUtil.getPropertyInfo(url,info);
    }

    @Override
    public int getMajorVersion() {
        return Util.ES_MAJOR_VERSION;
    }

    @Override
    public int getMinorVersion() {
        return Util.ES_MINOR_VERSION;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException(Util.getLoggingInfo());
    }

}
