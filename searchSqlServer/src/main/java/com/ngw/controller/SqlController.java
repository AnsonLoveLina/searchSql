package com.ngw.controller;

import com.ngw.domain.ResponseModel;
import com.ngw.domain.Sql;
import com.ngw.domain.SqlParam;
import com.ngw.domain.SqlSearchParam;
import com.ngw.service.api.SqlService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Created by zy-xx on 2019/8/22.
 */
@RestController
@RequestMapping("/sql")
public class SqlController {

    @Resource
    private SqlService sqlService;

    @ResponseBody
    @ApiOperation(value="SQL变成ES请求", notes="SQL变成ES请求")
    @PostMapping("/explain")
    public ResponseModel explain(@RequestBody Sql sql){
        return sqlService.explain(sql.getSql());
    }

    @ResponseBody
    @ApiOperation(value="SQL查询", notes="SQL查询")
    @PostMapping("/search")
    public ResponseModel search(@RequestBody Sql sql){
        return sqlService.search(sql);
    }

    @ResponseBody
    @ApiOperation(value="默认查询", notes="默认查询")
    @PostMapping("/defaultSearch")
    public ResponseModel defaultSearch(@RequestBody SqlParam sqlParam){
        return sqlService.defaultSearch(sqlParam);
    }

    @ResponseBody
    @ApiOperation(value="默认聚类查询", notes="默认聚类查询")
    @PostMapping("/aggsSearch")
    public ResponseModel aggsSearch(@RequestBody SqlSearchParam sqlSearchParam){
        return sqlService.aggsSearch(sqlSearchParam);
    }
}
