package com.stnts.bi.monitor;

import cn.hutool.core.util.StrUtil;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.utils.JacksonUtil;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author liutianyuan
 * @date 2021-06-16 15:19
 */

@ControllerAdvice
public class CustomResponseBodyAdviceAdapter implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        LogBO logBO = ThreadLocalLog.get();
        if (logBO != null) {
            response.getHeaders().add("Request-Identify", logBO.getRequestId().toString());
            String result = JacksonUtil.toJSON(body);
            logBO.setResult(StrUtil.sub(result, 0, 10000));
            if (body instanceof ResultEntity) {
                ResultEntity resultVO = (ResultEntity) body;
                if (ResultEntity.ResultEntityEnum.SUCCESS.getCode().equals(resultVO.getCode()) || ResultEntity.ResultEntityEnum.NOT_VALID_PARAM.getCode().equals(resultVO.getCode()) || ResultEntity.ResultEntityEnum.FAILURE.getCode().equals(resultVO.getCode())) {
                    logBO.setSuccess(1);
                } else {
                    logBO.setSuccess(0);
                    logBO.setMessage(resultVO.getStatus());
                }
            }
        }
        return body;
    }
}
