package com.stnts.tc.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.stnts.tc.kpi.KpiEy;

import lombok.Data;

/**
 * @author liang.zhang
 * @date 2019年11月20日
 * @desc TODO
 * 页面展示指标配置
 */
@Configuration
@ConfigurationProperties(prefix="kpi")
@Data
public class KpiConfiguration {
	
	/** 易游KPI配置  */
	private KpiEy ey;
}
