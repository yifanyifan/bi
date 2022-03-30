package com.stnts.bi.schedule.constant;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author tianyuan
 */
@Component
@Data
public class EnvironmentProperties {

    @Value("${schedule.setting.wutong.interface.address}")
    private String wutongInterfaceAddress;

    @Value("${schedule.setting.wutong.interface.secret}")
    private String wutongInterfaceSecret;


    /**
     * 订单系统调用接口使用
     */
    @Value("${deduct.setting.appId}")
    private String appId;
    @Value("${deduct.setting.appSecret}")
    private String appSecret;

    /**
     * 特殊秘钥【查询条件必填】
     */
    @Value("${deduct.setting.appId2}")
    private String appId2;
    @Value("${deduct.setting.appSecret2}")
    private String appSecret2;
}
