package com.ngw.worker;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ngw.domain.ResponseCode;
import com.ngw.domain.ResponseModel;
import com.ngw.domain.SqlParam;
import com.ngw.service.BatchServiceImpl;
import com.ngw.service.api.SqlService;
import com.ngw.util.POIUtil;
import jodd.util.StringUtil;
import org.apache.poi.ss.usermodel.Sheet;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * Created by zy-xx on 2019/12/25.
 */
public class BatchWorker implements Callable<Boolean> {
    private SqlParam sqlParam;

    private BatchServiceImpl.ScrollCallBack scrollCallBack;

    public BatchWorker(SqlParam sqlParam, BatchServiceImpl.ScrollCallBack scrollCallBack) {
        this.sqlParam = sqlParam;
        this.scrollCallBack = scrollCallBack;
    }

    @Override
    public Boolean call() {
        return false;
    }
}
