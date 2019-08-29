package com.ngw.exception;

import com.ngw.domain.ResponseCode;

/**
 * 业务异常类
 */
public class BaseBizException extends RuntimeException {

    /**
     * 业务异常错误码，见com.ngw.util.domain.ResponseCode
     */
    private int code;

    /**
     * 错误信息
     */
    private String message;

    public BaseBizException() {
        super();
    }

    private BaseBizException(String message) {
        super(message);
        this.code = ResponseCode.BIZ_ERROR.getCode();
        this.message = message;
    }

    private BaseBizException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public static BaseBizException of(String message) {
        return new BaseBizException(message);
    }

    public static BaseBizException of(ResponseCode codeEnum) {
        return of(codeEnum.getCode(), codeEnum.getMsg());
    }

    public static BaseBizException of(int code, String message) {
        return new BaseBizException(code, message);
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
