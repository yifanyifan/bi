package com.stnts.bi.datamanagement.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/5/5
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "dm")
public class DataManagementConfig {

    /** 数据管理数据管理员对应角色ID */
    private Integer roleAdminId;
    /** 易乐玩开放平台媒介ID */
    private String ylwMediumId;
    /** 易乐玩开放平台默认产品Code */
    private String ylwProductCode;
    /**
     * 易乐玩开放平台默认产品Id
     */
    private Long ylwProductId;
}
