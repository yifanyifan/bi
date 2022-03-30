//package com.stnts.bi.gateway.conf;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
//
///**
// * @author: liang.zhang
// * @description:
// * @date: 2021/6/24
// */
//@Configuration
//@EnableWebFluxSecurity
//public class GatewaySecurityConfig {
//    @Bean
//    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity serverHttpSecurity)
//    {
//        serverHttpSecurity.csrf().csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse());
////                .and().authorizeExchange().pathMatchers("/**").permitAll()
////                .anyExchange()
////                .authenticated();
//        return serverHttpSecurity.build();
//    }
//}
