package com.ngw.domain;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by zy-xx on 2019/12/26.
 */
public class BatchParam {
    @ApiModelProperty("任务ID")
    private String taskId;
    @ApiModelProperty("数据表用,分隔")
    private List<String> datas;
    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("用户权限级别")
    private int roleLevel;
    @ApiModelProperty("文本")
    private List<String> texts;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public List<String> getDatas() {
        return datas;
    }

    public void setDatas(List<String> datas) {
        this.datas = datas;
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

    public List<String> getTexts() {
        return texts;
    }

    public void setTexts(List<String> texts) {
        this.texts = texts;
    }
}
