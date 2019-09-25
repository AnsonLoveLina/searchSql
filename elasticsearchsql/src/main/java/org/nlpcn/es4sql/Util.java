package org.nlpcn.es4sql;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.threadpool.ThreadPool;
import org.nlpcn.es4sql.domain.KVValue;
import org.nlpcn.es4sql.exception.SqlParseException;
import org.nlpcn.es4sql.query.SqlElasticRequestBuilder;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Util {


    // statics
    public static final int ES_MAJOR_VERSION = 2;
    public static final int ES_MINOR_VERSION = 1;
    public static final String ELASTICSEARCH_NAME = "Elasticsearch";
    public static final String ELASTICSEARCH_VERSION = "2.1";
    public static final String CATALOG_SEPARATOR = ".";
    public static final int DRIVER_MAJOR_VERSION = 0;
    public static final int DRIVER_MINOR_VERSION = 5;

    // connection defaults
    public static final String PREFIX = "jdbc:elasticsearch:";
    public static final String URI_PREFIX = "elasticsearch://";
    public static final int PORT = 9300;

    // defaults
    private static final int FETCH_SIZE = 10000; // 10K is current max for ES
    private static final int SCROLL_TIMEOUT_SEC = 10;
    private static final int QUERY_TIMEOUT_MS = 10000;
    private static final int DEFAULT_ROW_LENGTH = 250; // used during initialization of rows when querying all columns (Select *)
    private static final String QUERY_CACHE = "query_cache";
    private static final String RESULT_NESTED_LATERAL = "true";
    private static final int FRAGMENT_SIZE = 100;
    private static final int FRAGMENT_NUMBER = 1;
    private static final int PRECISION_THRESHOLD = 3000;
    private static final boolean CLIENT_TRANSPORT_IGNORE = true; // 10K is current max for ES

    // property keys
    public static final String PROP_FETCH_SIZE = "fetch.size";
    public static final String PROP_SCROLL_TIMEOUT_SEC = "scroll.timeout.sec";
    public static final String PROP_QUERY_TIMEOUT_MS = "query.timeout.ms";
    public static final String PROP_DEFAULT_ROW_LENGTH = "default.row.length";
    public static final String PROP_QUERY_CACHE_TABLE = "query.cache.table";
    public static final String PROP_RESULT_NESTED_LATERAL = "result.nested.lateral";
    public static final String PROP_TABLE_COLUMN_MAP = "table.column.info.map";
    public static final String PROP_FRAGMENT_SIZE = "fragment.size";
    public static final String PROP_FRAGMENT_NUMBER = "fragment.number";
    public static final String PROP_RESULTS_SPLIT = "results.split";
    public static final String PROP_PRECISION_THRESHOLD = "precision.threshold";
    public static final String PROP_CLIENT_TRANSPORT_IGNORE = "client.transport.ignore_cluster_name";


    public static String getLoggingInfo(){
        StackTraceElement element = Thread.currentThread().getStackTrace()[2];
        return element.getClassName()+"."+element.getMethodName()+" ["+element.getLineNumber()+"]";
    }

    public static List<Object> clone(List<Object> list){
        List<Object> copy = new ArrayList<Object>(list.size());
        for(Object o : list) copy.add(o);
        return copy;
    }

    public static Properties defaultProps(){
        Properties defaults = new Properties();
        defaults.put(PROP_CLIENT_TRANSPORT_IGNORE, CLIENT_TRANSPORT_IGNORE);
        return defaults;
    }

    /**
     * Retrieves the integer property with given name from the properties
     * @param props
     * @param name
     * @param def
     * @return
     */
    public static int getIntProp(Properties props, String name, int def){
        if(!props.containsKey(name)) return def;
        try {
            return Integer.parseInt(props.getProperty(name));
        } catch (Exception e) {
            return def;
        }

    }

    /**
     * Retrieves the integer property with given name from the properties
     * @param props
     * @param name
     * @param def
     * @return
     */
    public static boolean getBooleanProp(Properties props, String name, boolean def){
        if(!props.containsKey(name)) return def;
        if(props.get(name).toString().length() < 3) return true;
        try {
            return Boolean.parseBoolean( props.getProperty(name) );
        } catch (Exception e) {
            return def;
        }

    }

    public static Object getObjectProperty(Properties props, String name) {
        return props.get(name);
    }

    public static void sleep(int millis) {
        try{
            Thread.sleep(millis);
        }catch(Exception e){}
    }


    public static SqlElasticRequestBuilder sqlToEsQuery(String sql) throws Exception {
        Map actions = new HashMap();
        Settings settings = Settings.builder().build();

        ThreadPool threadPool = new ThreadPool(settings);
        Client client = new NodeClient(settings, threadPool);
        SearchDao searchDao = new org.nlpcn.es4sql.SearchDao(client);
        try {
            return searchDao.explain(sql).explain();
        } catch (Exception e) {
            throw e;
        }
    }

    public static String joiner(List<KVValue> lists, String oper) {

        if (lists.size() == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder(lists.get(0).toString());
        for (int i = 1; i < lists.size(); i++) {
            sb.append(oper);
            sb.append(lists.get(i).toString());
        }

        return sb.toString();
    }

    public static List<Map<String, Object>> sortByMap(List<Map<String, Object>> lists) {

        return lists;
    }

    public static Object removeTableAilasFromField(Object expr, String tableAlias) {

        if (expr instanceof SQLIdentifierExpr || expr instanceof SQLPropertyExpr || expr instanceof SQLVariantRefExpr) {
            String name = expr.toString().replace("`", "");
            if (tableAlias != null) {
                String aliasPrefix = tableAlias + ".";
                if (name.startsWith(aliasPrefix)) {
                    String newFieldName = name.replaceFirst(aliasPrefix, "");
                    return new SQLIdentifierExpr(newFieldName);
                }
            }
        }
        return expr;
    }


    public static Object expr2Object(SQLExpr expr) {
        return expr2Object(expr, "");
    }

    public static Object expr2Object(SQLExpr expr, String charWithQuote) {
        Object value = null;
        if (expr instanceof SQLNumericLiteralExpr) {
            value = ((SQLNumericLiteralExpr) expr).getNumber();
        } else if (expr instanceof SQLCharExpr) {
            value = charWithQuote + ((SQLCharExpr) expr).getText() + charWithQuote;
        } else if (expr instanceof SQLIdentifierExpr) {
            value = expr.toString();
        } else if (expr instanceof SQLPropertyExpr) {
            value = expr.toString();
        } else if (expr instanceof SQLVariantRefExpr) {
            value = expr.toString();
        } else if (expr instanceof SQLAllColumnExpr) {
            value = "*";
        } else if (expr instanceof SQLValuableExpr) {
            value = ((SQLValuableExpr) expr).getValue();
        } else if (expr instanceof SQLBooleanExpr) {
            value = ((SQLBooleanExpr) expr).getValue();
        } else {
            //throw new SqlParseException("can not support this type " + expr.getClass());
        }
        return value;
    }

    public static Object getScriptValue(SQLExpr expr) throws SqlParseException {
        if (expr instanceof SQLIdentifierExpr || expr instanceof SQLPropertyExpr || expr instanceof SQLVariantRefExpr) {
            return "doc['" + expr.toString() + "'].value";
        } else if (expr instanceof SQLValuableExpr) {
            return ((SQLValuableExpr) expr).getValue();
        }
        throw new SqlParseException("could not parse sqlBinaryOpExpr need to be identifier/valuable got" + expr.getClass().toString() + " with value:" + expr.toString());
    }

    public static Object getScriptValueWithQuote(SQLExpr expr, String quote) throws SqlParseException {
        if (expr instanceof SQLIdentifierExpr || expr instanceof SQLPropertyExpr || expr instanceof SQLVariantRefExpr) {
            return "doc['" + expr.toString() + "'].value";
        }  else if (expr instanceof SQLCharExpr) {
            return quote + ((SQLCharExpr) expr).getValue() + quote;
        } else if (expr instanceof SQLIntegerExpr) {
            return ((SQLIntegerExpr) expr).getValue();
        } else if (expr instanceof SQLNumericLiteralExpr) {
            return ((SQLNumericLiteralExpr) expr).getNumber();
        } else if (expr instanceof SQLNullExpr) {
            return ((SQLNullExpr) expr).toString().toLowerCase();
        } else if (expr instanceof  SQLBinaryOpExpr) {
            //zhongshu-comment 该分支由忠树添加
            String left = "doc['" + ((SQLBinaryOpExpr) expr).getLeft().toString() + "'].value";
            String operator = ((SQLBinaryOpExpr) expr).getOperator().getName();
            String right = "doc['" + ((SQLBinaryOpExpr) expr).getRight().toString() + "'].value";
            return left + operator + right;
        }
        throw new SqlParseException("could not parse sqlBinaryOpExpr need to be identifier/valuable got " + expr.getClass().toString() + " with value:" + expr.toString());
    }

    public static boolean isFromJoinOrUnionTable(SQLExpr expr) {
        SQLObject temp = expr;
        AtomicInteger counter = new AtomicInteger(10);
        while (temp != null &&
                !(expr instanceof SQLSelectQueryBlock) &&
                !(expr instanceof SQLJoinTableSource) && !(expr instanceof SQLUnionQuery) && counter.get() > 0) {
            counter.decrementAndGet();
            temp = temp.getParent();
            if (temp instanceof SQLSelectQueryBlock) {
                SQLTableSource from = ((SQLSelectQueryBlock) temp).getFrom();
                if (from instanceof SQLJoinTableSource || from instanceof SQLUnionQuery) {
                    return true;
                }
            }
            if (temp instanceof SQLJoinTableSource || temp instanceof SQLUnionQuery) {
                return true;
            }
        }
        return false;
    }

    public static double[] String2DoubleArr(String paramer) {
        String[] split = paramer.split(",");
        double[] ds = new double[split.length];
        for (int i = 0; i < ds.length; i++) {
            ds[i] = Double.parseDouble(split[i].trim());
        }
        return ds;
    }

    public static double[] KV2DoubleArr(List<KVValue> params) {
        double[] ds = new double[params.size()];
        int i = 0;
        for (KVValue v : params) {
            ds[i] = ((Number) v.value).doubleValue();
            i++;
        }
        return ds;
    }


    public static String extendedToString(SQLExpr sqlExpr) {
        if (sqlExpr instanceof SQLTextLiteralExpr) {
            return ((SQLTextLiteralExpr) sqlExpr).getText();
        }
        return sqlExpr.toString();
    }

    public static String[] concatStringsArrays(String[] a1, String[] a2) {
        String[] strings = new String[a1.length + a2.length];
        for (int i = 0; i < a1.length; i++) {
            strings[i] = a1[i];
        }
        for (int i = 0; i < a2.length; i++) {
            strings[a1.length + i] = a2[i];
        }
        return strings;
    }

    public static Object searchPathInMap(Map<String, Object> fieldsMap, String[] path) {
        Map<String,Object> currentObject = fieldsMap;
        for(int i=0;i<path.length-1 ;i++){
            Object valueFromCurrentMap = currentObject.get(path[i]);
            if(valueFromCurrentMap == null) return null;
            if(!Map.class.isAssignableFrom(valueFromCurrentMap.getClass())) return null;
            currentObject = (Map<String, Object>) valueFromCurrentMap;
        }
        return currentObject.get(path[path.length-1]);
    }

    public static Object deepSearchInMap(Map<String,Object> fieldsMap , String field){
        if(field.contains(".")){
            String[] split = field.split("\\.");
            return searchPathInMap(fieldsMap,split);
        }
        return fieldsMap.get(field);
    }

    public static boolean clearEmptyPaths(Map<String, Object> map) {
        if(map.size() == 0){
            return true;
        }
        Set<String> keysToDelete = new HashSet<>();
        for (Map.Entry<String,Object> entry : map.entrySet()){
            Object value = entry.getValue();
            if(Map.class.isAssignableFrom(value.getClass())){
                if(clearEmptyPaths((Map<String, Object>) value)){
                    keysToDelete.add(entry.getKey());
                }
            }
        }
        if(keysToDelete.size() != 0){
            if(map.size() == keysToDelete.size()){
                map.clear();
                return true;
            }
            for(String key : keysToDelete){
                map.remove(key);
                return false;
            }
        }
        return false;
    }

}
