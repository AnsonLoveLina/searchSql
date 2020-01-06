package com.ngw.service.api;

import com.ngw.domain.ResponseModel;
import com.ngw.domain.Sql;
import com.ngw.domain.SqlParam;
import com.ngw.domain.SqlSearchParam;

import java.util.List;
import java.util.Map;

/**
 * Created by zy-xx on 2019/8/22.
 */
public interface SqlService {
    public ResponseModel explain(String sql);
    public ResponseModel search(Sql sql,RequestTrans requestTrans);
    public ResponseModel defaultSearch(SqlParam sqlParam,RequestTrans requestTrans);
    public ResponseModel aggsSearch(SqlSearchParam sqlSearchParam);
    public List<String> getFieldsByData(String data);
}
