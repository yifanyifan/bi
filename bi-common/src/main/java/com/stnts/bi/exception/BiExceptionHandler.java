package com.stnts.bi.exception;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.stnts.bi.common.ResultEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/1/20
 */
@ControllerAdvice
@Slf4j
public class BiExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    @ResponseStatus(value= HttpStatus.OK)
    public ResultEntity exceptionHandler(HttpServletRequest req, Exception e) {
        log.error("Global ExceptionHandler", e);
        String errorMesssage = "";
        if(e instanceof MethodArgumentNotValidException) {
            BindingResult bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
            if (CollectionUtil.isNotEmpty(bindingResult.getFieldErrors())) {
                for (FieldError fieldError : bindingResult.getFieldErrors()) {
                    errorMesssage += fieldError.getDefaultMessage() + ",";
                }
                errorMesssage = "参数校验异常:" + errorMesssage.substring(0, errorMesssage.length() - 1);
            } else {
                errorMesssage = "参数校验异常";
            }
        }else if(e instanceof DuplicateKeyException || e instanceof SQLIntegrityConstraintViolationException){
            errorMesssage = "存在重复数据,请检查输入名称等";
        }else if(e instanceof DataIntegrityViolationException){
            errorMesssage = "请输入合法长度的名称";
        }else{
            Throwable rootCause = ExceptionUtil.getRootCause(e);
            errorMesssage = ExceptionUtil.getMessage(rootCause);
        }
        return ResultEntity.exception(errorMesssage);
    }

    @ExceptionHandler(value = BiException.class)
    @ResponseBody
    @ResponseStatus(value= HttpStatus.OK)
    public ResultEntity biExceptionHandler(HttpServletRequest req, BiException e) {
        log.error("Global biExceptionHandler", e);
        String message = e.getMsg();
        return ResultEntity.exception(message);
    }
}
