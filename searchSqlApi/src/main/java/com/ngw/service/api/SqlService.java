package com.ngw.service.api;

import com.ngw.domain.ResponseModel;
import com.ngw.domain.Sql;
import com.ngw.domain.SqlParam;

/**
 * Created by zy-xx on 2019/8/22.
 */
public interface SqlService {
    public ResponseModel explain(String sql);
    public ResponseModel search(Sql sql);
    public ResponseModel defaultSearch(SqlParam sqlParam);
}
