package com.stnts.bi.dashboard.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2020/10/22
 */
//@Configuration
//@ConfigurationProperties(prefix = "dashboard.global")
@Getter
@Setter
public class DashBoardConf {

    private Map<String, BigDecimal> kpiMap;
}
