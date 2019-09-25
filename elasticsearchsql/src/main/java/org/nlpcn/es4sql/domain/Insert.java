package org.nlpcn.es4sql.domain;

import jodd.util.StringUtil;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zy-xx on 2019/9/25.
 */
public class Insert {

    private Map<String, Object> values = new HashMap<>();

    public Map<String, Object> getValues() {
        return values;
    }

    public void setValues(Map<String, Object> values) throws SQLException {
        if (values == null) {
            throw new SQLException("params can't be null");
        }
        this.values = values;
    }

    public void addValues(String columnName, Object updateValue) throws SQLException {
        if (StringUtil.isNotBlank(columnName) && updateValue != null) {
            values.put(columnName, updateValue);
        } else {
            throw new SQLException("columnName or value can't be null");
        }
    }
}
