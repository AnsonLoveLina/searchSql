package com.ngw.worker;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ngw.domain.ResponseCode;
import com.ngw.domain.ResponseModel;
import com.ngw.domain.SqlParam;
import com.ngw.service.api.SqlService;
import com.ngw.util.POIUtil;
import jodd.util.StringUtil;
import org.apache.poi.ss.usermodel.Sheet;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by zy-xx on 2019/12/25.
 */
public class BatchWorker implements Runnable {
    private Sheet sheet;

    private List<String> titles;

    private List<List<String>> datas;

    private CountDownLatch countDownLatch;

    public BatchWorker(Sheet sheet, List<String> titles, List<List<String>> datas, CountDownLatch countDownLatch) {
        this.sheet = sheet;
        this.titles = titles;
        this.datas = datas;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        try {
            POIUtil.createExcelFile(sheet, titles, datas);
        } finally {
            countDownLatch.countDown();
        }
    }
}
