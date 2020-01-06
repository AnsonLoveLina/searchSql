package com.ngw.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ngw.domain.BatchParam;
import com.ngw.domain.ResponseCode;
import com.ngw.domain.ResponseModel;
import com.ngw.domain.SqlParam;
import com.ngw.service.api.BatchService;
import com.ngw.service.api.RequestTrans;
import com.ngw.service.api.SqlService;
import com.ngw.socket.ISocketEmitCallBack;
import com.ngw.socket.SocketIOClientUtil;
import com.ngw.util.Constant;
import com.ngw.util.POIUtil;
import com.ngw.worker.BatchWorker;
import jodd.io.ZipUtil;
import jodd.util.StringUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipOutputStream;

import static com.ngw.util.Constant.SUCCESS_FLAG;

/**
 * Created by zy-xx on 2019/12/25.
 */
@Service("batchServiceImpl")
public class BatchServiceImpl implements BatchService {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(BatchServiceImpl.class);

    @Resource
    private SqlService sqlService;

    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Value("${batch.rootPath}")
    private String batchRootPath;

    @Value("${batch.size}")
    private Integer batchSize;

    public void batchExecute(BatchParam batchParam) {
        String taskId = batchParam.getTaskId();
        for (String text : batchParam.getTexts()) {
            try {
                batchTextExecute(text, batchParam);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(batchRootPath + File.separator + taskId + ".zip"));
            ZipUtil.addToZip(zipOutputStream, new File(batchRootPath + File.separator + taskId), null, null, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void batchTextExecute(String text, BatchParam batchParam) throws IOException, InterruptedException {
        String taskId = batchParam.getTaskId();
        List<String> datas = batchParam.getDatas();
        int count = datas.size();
        CountDownLatch countDownLatch = new CountDownLatch(count);
        FileOutputStream fileOut = new FileOutputStream(batchRootPath + File.separator + taskId + File.separator + text);
        Workbook workbook = new XSSFWorkbook();
        for (String data : datas) {
            Sheet sheet = workbook.createSheet(data);
            execute(text, data, batchParam, countDownLatch, sheet);

        }
        countDownLatch.await();
        workbook.write(fileOut);
    }

    private ResponseModel afterSearch(SqlParam sqlParam, String afterValue) {
        ResponseModel responseModel = sqlService.defaultSearch(sqlParam, new RequestTrans() {
            @Override
            public String trans(String request) {
                JSONObject requestJSONObject = JSONObject.parseObject(request);
                requestJSONObject.put("from", 0);
                if (StringUtil.isNotBlank(afterValue)) {
                    JSONArray after = new JSONArray();
                    after.add(afterValue);
                    requestJSONObject.put("search_after", after);
                }
                return JSONObject.toJSONString(requestJSONObject);
            }
        });
        return responseModel;
    }

    private void scrollAll(SqlParam sqlParam, ScrollCallBack scrollCallBack) {
        for (String afterValue = ""; ; ) {
            ResponseModel responseModel = afterSearch(sqlParam, afterValue);
            JSONObject response = (JSONObject) responseModel.getData();
            JSONArray hits = (JSONArray) ((JSONObject) response.get("hits")).get("hits");
            scrollCallBack.callBack(responseModel.getCode(), hits, responseModel);
            JSONObject record = (JSONObject) hits.get(hits.size());
            afterValue = (String) record.get("_id");
            if (hits.size() < batchSize) {
                break;
            }
        }
    }

    interface ScrollCallBack {
        void callBack(int responseCode, JSONArray hits, ResponseModel responseModel);
    }

    private void execute(String text, String data, BatchParam batchParam, CountDownLatch countDownLatch, Sheet sheet) {
        SqlParam sqlParam = new SqlParam();
        sqlParam.setDatas(Lists.newArrayList(data));
        sqlParam.setText(text);
        sqlParam.setUsername(batchParam.getUsername());
        sqlParam.setRoleLevel(batchParam.getRoleLevel());
        sqlParam.setPageSize(batchSize);
        List<String> title = sqlService.getFieldsByData(sqlParam.getDatas().get(0));
        scrollAll(sqlParam, (responseCode, hits, responseModel) -> {
            if (ResponseCode.SUCCESS.getCode() == responseCode) {
                List<List<String>> datas = Lists.newArrayList();

                for (int i = 0; i < hits.size(); i++) {
                    JSONObject record = (JSONObject) hits.get(i);
                    List<String> recordExcel = Lists.newArrayList();
                    for (String titleCode : record.keySet()) {
                        //toDo 假如是一对多的情况，则需要调整
                        int index = title.indexOf(titleCode);
                        if (index > 0) {
                            recordExcel.add(index, "" + record.get(titleCode));
                        }
                    }
                    datas.add(recordExcel);
                }
                executorService.execute(new BatchWorker(sheet, title, datas, countDownLatch));
                SocketIOClientUtil.getInstance().send(Constant.BATCH_SEARCH_RESULT_EVENT, batchParam.getUsername(), JSON.toJSONString(responseModel), new ISocketEmitCallBack() {
                    @Override
                    public void call(Map<String, String> response) {
                        if (!SUCCESS_FLAG.equals(response.get("flag"))) {
                            logger.error(String.format("%s error!\n%s", Constant.BATCH_SEARCH_RESULT_EVENT, response.toString()));
                        }
                    }
                });
            }
        });
    }
}
