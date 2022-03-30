package com.stnts.bi.gameop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2020/9/22
 */
@SpringBootApplication(scanBasePackages = {"com.stnts.bi"})
@EnableDiscoveryClient
public class GameopApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameopApplication.class, args);
    }
}
