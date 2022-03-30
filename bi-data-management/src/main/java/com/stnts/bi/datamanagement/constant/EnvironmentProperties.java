package com.stnts.bi.datamanagement.constant;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author tianyuan
 */
@Component
@Data
public class EnvironmentProperties {
    /**
     * 订单系统调用接口使用
     */
    @Value("${data-management.setting.appId}")
    private String appId;
    @Value("${data-management.setting.appSecret}")
    private String appSecret;

    /**
     * 特殊秘钥【查询条件必填】
     */
    @Value("${data-management.setting.appId2}")
    private String appId2;
    @Value("${data-management.setting.appSecret2}")
    private String appSecret2;

    @Value("${data-management.setting.uams.interface.address}")
    private String uamsAddress;

    @Value("${data-management.setting.uams.interface.secret}")
    private String uamsSecret;

    @Value("${data-management.setting.uams.interface.appId}")
    private String uamsAppId;

    @Value("${data-management.setting.add-cooperator-url}")
    private String addCooperatorUrl;

    @Value("${data-management.setting.youtop-api-host}")
    private String youtopApiHost;
}
