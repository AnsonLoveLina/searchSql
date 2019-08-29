package com.ngw.domain;

/**
 *
 * 接口响应模型对象
 * @since  lijianhua, 2018/11/24
 */
public class ResponseModel<T> {
    /**
     * 响应状态码，见com.ngw.util.domain.ResponseCode
     */
//    @ApiModelProperty("响应状态码")
    private int code;

    /**
     * 错误信息
     */
//    @ApiModelProperty("错误信息")
    private String msg;

    /**
     * 响应数据
     */
//    @ApiModelProperty("响应数据")
    private T data;

    public ResponseModel(){
    }

    public ResponseModel(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public ResponseModel(T data){
        this.data = data;
        this.code = ResponseCode.SUCCESS.getCode();
        this.msg = ResponseCode.SUCCESS.getMsg();
    }

    public ResponseModel(int code, String msg, T data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static ResponseModel getResponseModel(ResponseCode codeEnum){
        return new ResponseModel(codeEnum.getCode(),codeEnum.getMsg());
    }

    public static ResponseModel getSuccess(){
        return getResponseModel(ResponseCode.SUCCESS);
    }

    public static ResponseModel getSysError(){
        return getResponseModel(ResponseCode.SYS_ERROR);
    }

    public static ResponseModel getBizError(){
        return getResponseModel(ResponseCode.BIZ_ERROR);
    }

    public static <T> ResponseModel<T> getBizError(String message){
        return new ResponseModel(ResponseCode.BIZ_ERROR.getCode(), message);
    }

    public static <T> ResponseModel of(ResponseCode code, T data){
        return new ResponseModel<>(code.getCode(), code.getMsg(), data);
    }

    public static <T> ResponseModel<T> of(T data){
        return new ResponseModel<>(data);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
