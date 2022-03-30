package com.stnts.bi.plugin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.stnts.bi"})
@EnableDiscoveryClient
public class PluginApplication {

	public static void main(String[] args) {
		
		SpringApplication.run(PluginApplication.class, args);
	}
}
