package com.ngw.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ngw.domain.*;
import com.ngw.service.api.BatchService;
import com.ngw.service.api.RequestTrans;
import com.ngw.service.api.SqlService;
import com.ngw.socket.ISocketEmitCallBack;
import com.ngw.socket.SocketIOClientUtil;
import com.ngw.util.Constant;
import com.ngw.util.CustomerType;
import com.ngw.worker.BigExcelWriteWorker;
import jodd.io.FileUtil;
import jodd.io.ZipUtil;
import jodd.util.StringUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
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

    @Value("${batch.rootPath}")
    private String batchRootPath;

    @Value("${batch.size}")
    private Integer batchSize;

    @Value("${batch.sheetTimeout}")
    private Long sheetTimeout;

    public ResponseModel batchExecute(BatchParam batchParam) {
        String taskId = batchParam.getTaskId();
        List<BatchData> lists = Lists.newArrayList();
        for (String data : batchParam.getDatas()) {
            BatchData batchData = JSON.parseObject(JSON.toJSONString(sqlService.getDataByCode(data)), BatchData.class);
            lists.add(batchData);
        }
        batchParam.setBatchDatas(lists);
        for (String text : batchParam.getTexts()) {
            try {
                batchTextExecute(text, batchParam);
            } catch (IOException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        ZipOutputStream zipOutputStream = null;
        try {
            zipOutputStream = new ZipOutputStream(new FileOutputStream(batchRootPath + File.separator + taskId + ".zip"));
            ZipUtil.addToZip(zipOutputStream, new File(batchRootPath + File.separator + taskId), null, null, true);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipOutputStream != null) {
                try {
                    zipOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        SocketIOClientUtil.getInstance().send(batchParam.getTaskId() + Constant.BATCH_SEARCH_ENDPOINT_EVENT, batchParam.getUsername(), CustomerType.USER, JSON.toJSONString(ResponseModel.getSuccess()), new ISocketEmitCallBack() {
            @Override
            public void call(Map<String, String> response) {
                if (!SUCCESS_FLAG.equals(response.get("flag"))) {
                    logger.error(String.format("%s error!\n%s", Constant.BATCH_SEARCH_ENDPOINT_EVENT, response.toString()));
                }
            }
        });
        sqlService.batchTaskFinish(batchParam.getTaskId());
        return ResponseModel.getSuccess();
    }

    private void batchTextExecute(String text, BatchParam batchParam) throws IOException, InterruptedException, ExecutionException {
        String taskId = batchParam.getTaskId();
        List<BatchData> datas = batchParam.getBatchDatas();
        int count = datas.size();
        FileUtil.mkdir(new File(batchRootPath + File.separator + taskId));
        for (BatchData data : datas) {
            String excelPath = batchRootPath + File.separator + taskId + File.separator + text + ".xlsx";
            BigExcelWriteWorker bigExcelWriteWorker = new BigExcelWriteWorker();
            batchTextDataExecute(text, data, batchParam, bigExcelWriteWorker);
            bigExcelWriteWorker.commit(excelPath);
        }
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

    private void scrollAll(String taskId, SqlParam sqlParam, BatchData batchData, ScrollCallBack scrollCallBack) {
        String afterValue = "";
        for (int i = 0; ; i++) {
            ResponseModel responseModel = afterSearch(sqlParam, afterValue);
            JSONObject response = (JSONObject) responseModel.getData();
            JSONArray hits = (JSONArray) ((JSONObject) response.get("hits")).get("hits");
            scrollCallBack.callBack(responseModel.getCode(), hits, responseModel);
            JSONObject record = null;
            if (hits.size() != 0) {
                record = (JSONObject) hits.get(hits.size() - 1);
            } else {
                break;
            }
            afterValue = (String) record.get("_id");
            if (hits.size() < batchSize) {
                break;
            }
            responseModel = null;
        }
    }

    public interface ScrollCallBack {
        void callBack(int responseCode, JSONArray hits, ResponseModel responseModel);
    }

    private void batchTextDataExecute(String text, BatchData data, BatchParam batchParam, BigExcelWriteWorker bigExcelWriteWorker) {
        SqlParam sqlParam = new SqlParam();
        sqlParam.setDatas(Lists.newArrayList(data.getDataCode()));
        sqlParam.setText(text);
        sqlParam.setUsername(batchParam.getUsername());
        sqlParam.setRoleLevel(batchParam.getRoleLevel());
        sqlParam.setPageSize(batchSize);
        sqlParam.setOrders("_id");
        List<List<String>> title = sqlService.getFieldsByData(sqlParam.getDatas().get(0));
        List<String> titleCodes = title.get(0);
        List<String> titleName = title.get(1);
        scrollAll(batchParam.getTaskId(), sqlParam, data, (responseCode, hits, responseModel) -> {
            if (ResponseCode.SUCCESS.getCode() == responseCode) {
                List<List<String>> datas = Lists.newArrayList();

                for (int i = 0; i < hits.size(); i++) {
                    JSONObject record = (JSONObject) hits.get(i);
                    JSONObject source = (JSONObject) record.get("_source");
                    String[] recordExcel = new String[titleCodes.size()];
                    for (String titleCode : source.keySet()) {
                        int index = titleCodes.indexOf(titleCode);
                        if (index > 0) {
                            recordExcel[index] = "" + source.get(titleCode);
                        } else {
                            //父子表的情况
                            JSONArray sourceChilds = (JSONArray) source.get(titleCode);
                            for (Object object : sourceChilds) {
                                JSONObject sourceChild = (JSONObject) object;
                                for (String titleChildCode : sourceChild.keySet()) {
                                    int indexChild = titleCodes.indexOf(titleCode + "." + titleChildCode);
                                    if (indexChild > 0) {
                                        recordExcel[indexChild] = "" + sourceChild.get(titleChildCode);
                                    }
                                }
                            }
                        }
                    }
                    datas.add(Lists.newArrayList(recordExcel));
                }
                if (datas.size() != 0) {
                    bigExcelWriteWorker.writeSheet(data.getDataName(), titleName, datas);
                    datas.clear();

                    JSONObject response = new JSONObject();
                    response.put("result", responseModel);
                    response.put("text", sqlParam.getText());
                    response.put("data", data);
                    SocketIOClientUtil.getInstance().send(batchParam.getTaskId(), sqlParam.getUsername(), CustomerType.USER, response, new ISocketEmitCallBack() {
                        @Override
                        public void call(Map<String, String> response) {
                            if (!SUCCESS_FLAG.equals(response.get("flag"))) {
                                logger.error(String.format("%s error!\n%s", batchParam.getTaskId(), response.toString()));
                            }
                        }
                    });
                }
            }
        });
    }
}
