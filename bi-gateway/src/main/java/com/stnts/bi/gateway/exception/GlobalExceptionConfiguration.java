//package com.stnts.bi.gateway.exception;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.stnts.bi.gateway.common.ResultEntity;
//import com.stnts.bi.gateway.conf.GatewayConfiguration;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
//import org.springframework.core.io.buffer.DataBufferFactory;
//import org.springframework.http.MediaType;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ResponseStatusException;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
///**
// * @author: liang.zhang
// * @description:
// */
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class GlobalExceptionConfiguration implements ErrorWebExceptionHandler {
//
//    private final GatewayConfiguration gatewayConfig;
//
//    private final ObjectMapper objectMapper;
//
//    @Override
//    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
//        ServerHttpResponse response = exchange.getResponse();
//
//        if (response.isCommitted()) {
//            return Mono.error(ex);
//        }
//
//        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
//        if (ex instanceof ResponseStatusException) {
//            response.setStatusCode(((ResponseStatusException) ex).getStatus());
//        }
//
//        return response
//                .writeWith(Mono.fromSupplier(() -> {
//                    DataBufferFactory bufferFactory = response.bufferFactory();
//                    try {
//                        if(gatewayConfig.isShowStack()){
//                            ex.printStackTrace();
//                        }
//                        return bufferFactory.wrap(objectMapper.writeValueAsBytes(ResultEntity.exception(ex.getMessage())));
//                    } catch (JsonProcessingException e) {
//                        log.warn("Error writing response", ex);
//                        return bufferFactory.wrap(new byte[0]);
//                    }
//                }));
//    }
//}