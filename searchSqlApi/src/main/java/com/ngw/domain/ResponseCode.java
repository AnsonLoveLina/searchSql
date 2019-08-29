package com.ngw.domain;

/**
 * 响应状态码枚举定义
 *  @since  lijianhua, 2018/11/24
 */
public enum ResponseCode {

    SUCCESS(0,"成功"),
    SYS_ERROR(500,"系统内部异常"),
    NO_ACCESS(403,"无权限"),
    NO_LOGIN(401,"未登录，或登录无效"),
    BIZ_ERROR(400,"业务异常");


    private int code;
    private String msg;

    ResponseCode(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
