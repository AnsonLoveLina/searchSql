package com.ngw.domain;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by zy-xx on 2019/8/22.
 */
public class Sql {
    @ApiModelProperty("sql语句")
    private String sql;
    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("用户权限级别")
    private int roleLevel;

    public Sql() {
    }

    public Sql(String sql, String username, int roleLevel) {
        this.sql = sql;
        this.username = username;
        this.roleLevel = roleLevel;
    }

    public int getRoleLevel() {
        return roleLevel;
    }

    public void setRoleLevel(int roleLevel) {
        this.roleLevel = roleLevel;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
