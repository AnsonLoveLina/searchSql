package com.ngw.service.api;

import com.ngw.domain.BatchParam;
import com.ngw.domain.SqlParam;

import java.util.List;
import java.util.Map;

/**
 * Created by zy-xx on 2019/12/26.
 */
public interface BatchService {
    void batchExecute(BatchParam batchParam);
}
