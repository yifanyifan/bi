package com.stnts.tc.common;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.stnts.tc.vo.Option;

import lombok.Data;

/**
 * @author liang.zhang
 * @date 2020年1月9日
 * @desc TODO
 * key配置
 */
@Configuration
@ConfigurationProperties(prefix="key")
@Data
public class KeyConfiguration {

	/** 易游-全局-插件-数据概况 */
	private List<Option> eyGlobalPluginProfile;
	
	/** 易游-网吧-插件-数据概况 */
	private List<Option> eyBarPluginProfile;
	
	/** 易游-网吧-基础-指标 */
	private List<Option> eyBarBasicKpi;
}
