package com.stnts.bi.sys;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.stnts.bi.exception.BiExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author liang.zhang
 */
@EnableTransactionManagement
@EnableSwagger2
@EnableKnife4j
@SpringBootApplication(scanBasePackages = {"com.stnts.bi"})
@EnableDiscoveryClient
@EnableScheduling
@EnableFeignClients(basePackages = "com.stnts.bi.sys.feign")
public class MainApplication {

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}
}
