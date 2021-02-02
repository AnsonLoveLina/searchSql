package com.ngw.service.api;

import com.ngw.domain.*;

import java.util.List;
import java.util.Map;

/**
 * Created by zy-xx on 2019/8/22.
 */
public interface SqlService {
    public ResponseModel explain(String sql);
    public ResponseModel search(Sql sql,RequestTrans requestTrans);
    public ResponseModel defaultSearch(SqlParam sqlParam,RequestTrans requestTrans);

    ResponseModel aggsSearch(SqlParam sqlSearchParam);

    int batchTaskFinish(String taskid);

    public List<List<String>> getFieldsByData(String datacode);

    public Map<String,Object> getDataByCode(String datacode);
}
