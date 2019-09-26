package org.nlpcn.es4sql.domain;

import jodd.util.StringUtil;
import org.elasticsearch.action.DocWriteResponse;
import org.nlpcn.es4sql.Util;
import org.nlpcn.es4sql.exception.SqlParseException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zy-xx on 2019/9/25.
 */
public class Insert {

    private String index;

    private String type;

    private String id = null;

    private Map<String, Object> values = new HashMap<>();

    public Map<String, Object> getValues() {
        return values;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public void setValues(Map<String, Object> values) throws SqlParseException {
        if (values == null) {
            throw new SqlParseException("params can't be null");
        }
        this.values = values;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void addValues(String columnName, Object updateValue) throws SqlParseException {
        if (StringUtil.isNotBlank(columnName) && updateValue != null) {
            values.put(columnName, updateValue);
            if (Util._ID.equals(columnName)) {
                setId(updateValue.toString());
            }
        } else {
            throw new SqlParseException("columnName or value can't be null");
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void getValues(String columnName) throws SqlParseException {
        if (StringUtil.isNotBlank(columnName)) {
            values.get(columnName);
        } else {
            throw new SqlParseException("columnName or value can't be null");
        }
    }
}
