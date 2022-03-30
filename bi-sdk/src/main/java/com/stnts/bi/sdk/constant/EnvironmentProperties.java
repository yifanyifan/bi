package com.stnts.bi.sdk.constant;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author tianyuan
 */
@Component
@Data
public class EnvironmentProperties {

    @Value("${sdk.setting.wutong.interface.address}")
    private String wutongInterfaceAddress;

    @Value("${sdk.setting.wutong.interface.secret}")
    private String wutongInterfaceSecret;

    @Value("${sdk.setting.appIdForYoutop}")
    private String appIdForYoutop;

    @Value("${sdk.setting.appSecretForYoutop}")
    private String appSecretForYoutop;

}
