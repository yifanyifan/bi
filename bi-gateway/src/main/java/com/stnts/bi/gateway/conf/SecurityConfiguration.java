//package com.stnts.bi.gateway.conf;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
//
///**
// * @author: liang.zhang
// * @description:
// * @date: 2021/11/3
// */
//@Configuration
//@EnableWebFluxSecurity
//public  class SecurityConfiguration extends WebSecurityConfigurerAdapter {
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().ignoringRequestMatchers().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
//    }
//}
