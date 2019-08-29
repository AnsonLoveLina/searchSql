package com.ngw.controller;

import com.ngw.domain.ResponseModel;
import com.ngw.domain.Sql;
import com.ngw.domain.SqlParam;
import com.ngw.service.api.SqlService;
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
    @PostMapping("/explain")
    public ResponseModel explain(@RequestBody Sql sql){
        return sqlService.explain(sql.getSql());
    }

    @ResponseBody
    @PostMapping("/search")
    public ResponseModel search(@RequestBody Sql sql){
        return sqlService.search(sql);
    }

    @ResponseBody
    @PostMapping("/defaultSearch")
    public ResponseModel defaultSearch(@RequestBody SqlParam sqlParam){
        return sqlService.defaultSearch(sqlParam);
    }
}
