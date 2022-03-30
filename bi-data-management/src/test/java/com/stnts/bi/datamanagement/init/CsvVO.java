package com.stnts.bi.datamanagement.init;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/4/16
 */
@Data
@AllArgsConstructor
public class CsvVO {
    private String departmentName;
    private String departmentCode;
    private String username;
    private String userid;
    private String classify;
    private String companyName;
    private String channelName;
    private String subChannelName;
    private String pidAlias;
    private String productName;
    private String productCode;
    private String ppName;
    private String ext;
    private String chargeRule;
    private String channelRate;
    private String channelShare;
    private String price;
}
