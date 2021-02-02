package com.ngw.controller;

import com.ngw.domain.BatchParam;
import com.ngw.domain.ResponseModel;
import com.ngw.domain.SqlParam;
import com.ngw.service.BatchServiceImpl;
import com.ngw.service.api.BatchService;
import com.ngw.service.api.SqlService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zy-xx on 2019/12/26.
 */
@RestController
@RequestMapping("/batch")
public class BatchSearchController implements BatchService {

    @Resource(name = "batchServiceImpl")
    private BatchService batchService;

    @Value("${batch.rootPath}")
    private String batchRootPath;

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 5, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());

    @Override
    @ResponseBody
    @ApiOperation(value = "批量比对", notes = "批量比对")
    @PostMapping("/batchExecute")
    public ResponseModel batchExecute(@RequestBody BatchParam batchParam) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                batchService.batchExecute(batchParam);
            }
        });
        return ResponseModel.getSuccess();
    }

    @ResponseBody
    @ApiOperation(value = "批量比对任务结果下载", notes = "批量比对任务结果下载")
    @RequestMapping("/batchExecuteResultDownload")
    public ResponseEntity<FileSystemResource> batchExecuteResultDownload(@RequestParam String taskId) {
        File batchExcelFile = new File(batchRootPath + File.separator + taskId + ".zip");
        return export(batchExcelFile);
    }

    private ResponseEntity<FileSystemResource> export(File file) {
        if (file == null) {
            return null;
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Disposition", "attachment;filename=" + file.getName());
        return ResponseEntity.ok().headers(httpHeaders).contentLength(file.length()).contentType(MediaType.parseMediaType("application/octet-stream")).body(new FileSystemResource(file));
    }
}
