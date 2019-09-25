package org.elasticsearch.jdbc;

import org.nlpcn.es4sql.Util;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by zy-xx on 2019/9/25.
 */
public class ESJDBCUtil {

    /**
     * Returns the class associated with a java.sql.Types id
     * @param type
     * @return
     */
    public static Class<?> getClassForTypeId(int type){
        switch(type){
            case Types.ARRAY : return Array.class;
            case Types.BIGINT : return Long.class;
            case Types.TINYINT : return Byte.class;
            case Types.BINARY : return Byte[].class;
            case Types.BIT : return Boolean.class;
            case Types.BOOLEAN : return Boolean.class;
            case Types.CHAR : return Character.class;
            case Types.DATE : return java.util.Date.class;
            case Types.DOUBLE : return Double.class;
            case Types.FLOAT : return Float.class;
            case Types.INTEGER : return Integer.class;
            case Types.NUMERIC : return BigDecimal.class;
            case Types.SMALLINT : return Short.class;
            case Types.LONGVARCHAR : return String.class;
            case Types.REAL : return Float.class;
            case Types.VARCHAR : return String.class;
            case Types.TIME : return Time.class;
            case Types.TIMESTAMP : return Timestamp.class;
            case Types.LONGVARBINARY : return Byte[].class;
            case Types.VARBINARY : return Byte[].class;
            default : return Object.class;
        }
    }

    public static int getTypeIdForObject(Object c) {
        if (c instanceof Long)
            return Types.BIGINT;
        if (c instanceof Boolean)
            return Types.BOOLEAN;
        if (c instanceof Character)
            return Types.CHAR;
        if (c instanceof Timestamp)
            return Types.TIMESTAMP;
        if (c instanceof Date)
            return Types.DATE;
        if (c instanceof java.util.Date)
            return Types.DATE;
        if (c instanceof Double)
            return Types.DOUBLE;
        if (c instanceof Integer)
            return Types.INTEGER;
        if (c instanceof BigDecimal)
            return Types.NUMERIC;
        if (c instanceof Short)
            return Types.SMALLINT;
        if (c instanceof Float)
            return Types.FLOAT;
        if (c instanceof String)
            return Types.VARCHAR;
        if (c instanceof Time)
            return Types.TIME;
        if (c instanceof Byte)
            return Types.TINYINT;
        if (c instanceof Byte[])
            return Types.VARBINARY;
        if(c instanceof Object[])
            return Types.JAVA_OBJECT;
        if(c instanceof Object)
            return Types.JAVA_OBJECT;
        if (c instanceof Array)
            return Types.ARRAY;
        else
            return Types.OTHER;
    }

    /**
     * Parses the url and returns information required to create a connection. Properties
     * in the url are added to the provided properties and returned in the object array
     *
     * @param url
     * @param info
     * @return {String:host, int:port, String:index, Properties:info}
     * @throws SQLException
     */
    public static Object[] parseURL(String url, Properties info) throws SQLException {
        if (!acceptsURL(url)) throw new SQLException("Invalid url");
        List<URI> uris = new ArrayList<>();
        String conUrls = url.substring(21);
        String[] conUrlArray = conUrls.split(",");
        try {
            for (String conUrl : conUrlArray) {
                URI uri = new URI(Util.URI_PREFIX + conUrl);
                uris.add(uri);
                Properties props = Util.defaultProps();
                if (info != null) {
                    props.putAll(info);
                }
                info = props;
                if (uri.getQuery() != null)
                    for (String keyValue : uri.getQuery().split("&")) {
                        String[] parts = keyValue.split("=");
                        if (parts.length > 1) info.setProperty(parts[0].trim(), parts[1].trim());
                        else info.setProperty(parts[0], "");
                    }
            }
        } catch (URISyntaxException e) {
            throw new SQLException("Unable to parse URL. Pleas use '" + Util.PREFIX + "//host:port/schema?{0,1}(param=value&)*'", e);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new SQLException("No shema (index) specified. Pleas use '" + Util.PREFIX + "//host:port/schema?{0,1}(param=value&)*'");
        } catch (Exception e) {
            throw new SQLException("Unable to connect to database due to: " + e.getClass().getName(), e);
        }
        return new Object[]{uris, info};
    }

    public static boolean acceptsURL(String url) throws SQLException {
        if (!url.startsWith(Util.PREFIX)) return false;
        try {
            String conUrls = url.substring(21);
            String[] conUrlArray = conUrls.split(",");
            for (String conUrl : conUrlArray) {
                URI uri = new URI(Util.URI_PREFIX + conUrl);
                if (uri.getHost() == null) throw new SQLException("Invalid URL, no host specified");
                if (uri.getPath() == null) throw new SQLException("Invalid URL, no index specified");
                if (uri.getPath().split("/").length > 2)
                    throw new SQLException("Invalid URL, " + uri.getPath() + " is not a valid index");
            }
        } catch (URISyntaxException e) {
            throw new SQLException("Unable to parse URL", e);
        }
        return true;
    }

    public static DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        Properties props = (Properties) parseURL(url, info)[3];
        DriverPropertyInfo[] result = new DriverPropertyInfo[props.size()];
        int index = 0;
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            result[index] = new DriverPropertyInfo((String) entry.getKey(), entry.getValue().toString());
            index++;
        }
        return result;
    }
}
