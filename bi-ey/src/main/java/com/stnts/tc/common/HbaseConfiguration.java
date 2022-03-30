package com.stnts.tc.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * @author liang.zhang
 * @date 2019年11月18日
 * @desc TODO
 */
@Configuration
@ConfigurationProperties("hbase")
@Data
public class HbaseConfiguration {
	
	private String quorum;
	
	private String parent;
	
	private String clientPort;
	
	private String rootDir;
	
	private String tableName;  //指标主表
	
	private String columnFamily;  //主表columnFamily
	
	private String barInfoTable;  //网吧表
	private String barIndexTable;  //网吧索引表[索引表的列簇和列统一为"info"]
	private String pluginIndexTable;  //插件索引表
	
}
