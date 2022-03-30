package com.stnts.tc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;


/**
 * @author liang.zhang
 */
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
//@EnableSwagger2
@ComponentScan(value = {"com.stnts.tc.*", "com.stnts.bi.sql.*", "com.stnts.bi.authorization"})
@EnableDiscoveryClient
public class TrafficCenterApplication {

	public static void main(String[] args) {
		
		System.setProperty("hadoop.home.dir", "F:\\hadoop-common-2.2.0-bin-master\\hadoop-common-2.2.0-bin-master");
		SpringApplication.run(TrafficCenterApplication.class, args);
	}
}
