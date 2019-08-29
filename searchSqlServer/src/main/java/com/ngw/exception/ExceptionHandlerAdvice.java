package com.ngw.exception;

import com.ngw.domain.ResponseCode;
import com.ngw.domain.ResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

import java.util.List;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseModel handler(Exception e, HandlerMethod handlerMethod) {
        ResponseModel responseModel = ResponseModel.getSysError();

        if (e instanceof BaseBizException) {
            BaseBizException baseBizException = (BaseBizException) e;
            responseModel.setCode(baseBizException.getCode());
            responseModel.setMsg(baseBizException.getMessage());
        } else if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException validException = (MethodArgumentNotValidException) e;
            List<ObjectError> errors = validException.getBindingResult().getAllErrors();
            StringBuffer errorBuf = new StringBuffer();
            errors.stream().forEach(x -> errorBuf.append(x.getDefaultMessage()).append(","));
            responseModel.setCode(ResponseCode.BIZ_ERROR.getCode());
            responseModel.setMsg(errorBuf.substring(0, errorBuf.length() - 1));
        } else {
            responseModel.setMsg("系统异常，稍后再试");
        }
        logger.error("程序出现异常", e);
        return responseModel;
    }
}
