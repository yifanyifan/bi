package com.stnts.bi.gateway.utils;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2020/12/2
 */
public class RespUtil {

    public static  Mono<Void> returnMessage(ServerWebExchange exchange, String resultStr) {
        ServerHttpResponse response = exchange.getResponse();
        HttpStatus httpStatus = HttpStatus.OK;
        byte[] bits = resultStr.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        response.setStatusCode(httpStatus == null ? HttpStatus.OK : httpStatus);
        //指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }
}
