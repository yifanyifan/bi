package com.stnts.bi.dashboard.config;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.stnts.bi.common.ResultEntity;
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

}
