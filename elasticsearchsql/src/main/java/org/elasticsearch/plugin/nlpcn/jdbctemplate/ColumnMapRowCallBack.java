package org.elasticsearch.plugin.nlpcn.jdbctemplate;

import com.google.common.collect.Lists;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by zy-xx on 2019/9/23.
 */
public class ColumnMapRowCallBack<T> implements PreparedStatementCallback<T> {
    private RowMapper<Map<String, Object>> rowMapper;

    public ColumnMapRowCallBack(RowMapper<Map<String, Object>> rowMapper) {
        this.rowMapper = rowMapper;
    }

    @Override
    public T doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
        List<Map<String, Object>> lists = Lists.newArrayList();
        ResultSet resultSet = ps.executeQuery();
        return null;
    }
}
