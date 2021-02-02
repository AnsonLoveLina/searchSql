package com.ngw.domain;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by zy-xx on 2019/10/8.
 */
public class SqlSearchParam {

    @ApiModelProperty("数据表用,分隔")
    private List<String> datas;
    @ApiModelProperty("文本")
    private String text;
    @ApiModelProperty("查询语句")
    private String conditions;
    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("用户权限级别")
    private int roleLevel;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
}
