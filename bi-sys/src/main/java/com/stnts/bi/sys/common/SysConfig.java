package com.stnts.bi.sys.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix="sys.global")
@Data
public class SysConfig {

	private Integer pageSize;
	private String ehomeApi;
	private String ehomeDepartmentApi;
	private String ehomeUserApi;
	private String ehomeAppId;
	private String ehomeKey;
	private String biIndex;
	private boolean checkSign;
	
	//产品线接口
	private String productApiHost;
	private String productApiKey;

	//bi提供给olap的key
	private String olapKey;
	private String defaultName;
	private String olapAppId;
	private String olapApiUrl;
	private String keyFromOlap;
	private List<Integer> biOpenMenu;
}
