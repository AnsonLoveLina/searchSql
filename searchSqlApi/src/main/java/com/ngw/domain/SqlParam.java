package com.ngw.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by zy-xx on 2019/8/29.
 */
public class SqlParam extends SqlSearchParam {
    @ApiModelProperty("排序语句")
    private String orders;
    @ApiModelProperty("第几页")
    private int page;
    @ApiModelProperty("一页多少数据")
    private int pageSize;
    @ApiModelProperty("是否需要聚类")
    private boolean aggs = true;
    @ApiModelProperty("是否是详细查询")
    private boolean detail;

    public boolean isDetail() {
        return detail;
    }

    public void setDetail(boolean detail) {
        this.detail = detail;
    }

    public boolean isAggs() {
        return aggs;
    }

    public void setAggs(boolean aggs) {
        this.aggs = aggs;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrders() {
        return orders;
    }

    public void setOrders(String orders) {
        this.orders = orders;
    }
}
