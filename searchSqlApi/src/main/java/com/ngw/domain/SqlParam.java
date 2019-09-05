package com.ngw.domain;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by zy-xx on 2019/8/29.
 */
public class SqlParam {
    @ApiModelProperty("数据表用,分隔")
    private List<String> datas;
    @ApiModelProperty("高亮")
    private String highlight;
    @ApiModelProperty("查询语句")
    private String conditions;
    @ApiModelProperty("排序语句")
    private String orders;
    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("用户权限级别")
    private int roleLevel;
    @ApiModelProperty("第几页")
    private int page;
    @ApiModelProperty("一页多少数据")
    private int pageSize;
    @ApiModelProperty("是否是详细查询")
    private boolean detail;

    public String getHighlight() {
        return highlight;
    }

    public void setHighlight(String highlight) {
        this.highlight = highlight;
    }

    public boolean isDetail() {
        return detail;
    }

    public void setDetail(boolean detail) {
        this.detail = detail;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRoleLevel() {
        return roleLevel;
    }

    public void setRoleLevel(int roleLevel) {
        this.roleLevel = roleLevel;
    }

    public List<String> getDatas() {
        return datas;
    }

    public void setDatas(List<String> datas) {
        this.datas = datas;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public String getOrders() {
        return orders;
    }

    public void setOrders(String orders) {
        this.orders = orders;
    }
}
