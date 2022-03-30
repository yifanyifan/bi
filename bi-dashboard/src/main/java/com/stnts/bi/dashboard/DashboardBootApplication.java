package com.stnts.bi.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author liutianyuan
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan({"com.stnts.bi.dashboard","com.stnts.bi.sql","com.stnts.bi.authorization"})
@EnableDiscoveryClient
public class DashboardBootApplication {
    public static void main(String[] args) {
        SpringApplication.run(DashboardBootApplication.class, args);
    }
}