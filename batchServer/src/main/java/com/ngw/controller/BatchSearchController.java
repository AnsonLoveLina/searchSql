package com.ngw.controller;

import com.ngw.domain.BatchParam;
import com.ngw.domain.SqlParam;
import com.ngw.service.BatchServiceImpl;
import com.ngw.service.api.BatchService;
import com.ngw.service.api.SqlService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by zy-xx on 2019/12/26.
 */
@RestController
@RequestMapping("/batch")
public class BatchSearchController implements BatchService {

    @Resource(name = "batchServiceImpl")
    private BatchService batchService;

    @Override
    @ResponseBody
    @ApiOperation(value = "批量比对", notes = "批量比对")
    @PostMapping("/batchExecute")
    public void batchExecute(BatchParam batchParam) {
        batchService.batchExecute(batchParam);
    }
}
