package com.stnts.bi.sdk.config;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sdk.exception.ValidationException;
import com.stnts.bi.sql.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * @author liutianyuan
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    @ResponseStatus(value= HttpStatus.OK)
    public ResultEntity exceptionHandler(HttpServletRequest req, Exception e) {
        log.error("Global ExceptionHandler", e);
        Throwable rootCause = ExceptionUtil.getRootCause(e);
        String message = ExceptionUtil.getMessage(rootCause);
        return   ResultEntity.exception(message);
    }

    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    @ResponseStatus(value= HttpStatus.OK)
    public ResultEntity businessExceptionHandler(HttpServletRequest req, BusinessException e) {
        log.error("BusinessException ExceptionHandler", e);
        return ResultEntity.exception(e.getInfo());
    }

    @ExceptionHandler(value = ValidationException.class)
    @ResponseBody
    @ResponseStatus(value= HttpStatus.OK)
    public ResultEntity validationExceptionHandler(HttpServletRequest req, ValidationException e) {
        log.info(e.getInfo());
        return ResultEntity.param(e.getInfo());
    }
}
