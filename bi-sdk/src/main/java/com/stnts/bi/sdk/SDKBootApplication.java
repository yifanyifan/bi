package com.stnts.bi.sdk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author liutianyuan
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan({"com.stnts.bi.sdk", "com.stnts.bi.sql", "com.stnts.bi.monitor", "com.stnts.bi.authorization"})
@EnableDiscoveryClient
@EnableSwagger2
public class SDKBootApplication {
    public static void main(String[] args) {
        SpringApplication.run(SDKBootApplication.class, args);
    }
}