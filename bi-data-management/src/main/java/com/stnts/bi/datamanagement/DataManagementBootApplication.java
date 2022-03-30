package com.stnts.bi.datamanagement;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.stnts.signature.annotation.SignedScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author liutianyuan
 */
@SpringBootApplication
@ComponentScan({"com.stnts.bi.datamanagement", "com.stnts.bi.common"})
//, "com.stnts.bi.monitor"
//@ComponentScan({"com.stnts.bi.datamanagement"})
@MapperScan({"com.stnts.bi.datamanagement.module.*.mapper"})
@EnableDiscoveryClient
@EnableScheduling
@EnableSwagger2
@EnableKnife4j
@SignedScan
@EnableConfigurationProperties
@EnableFeignClients
public class DataManagementBootApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataManagementBootApplication.class, args);
    }
}