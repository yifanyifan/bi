package com.stnts.bi.datamanagement.module.channel.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: liang.zhang
 * @description: 产品、应用VO
 * @date: 2021/2/26
 */
@Data
@ApiModel("应用类")
public class AppVO {
    @ApiModelProperty("产品ID")
    private String productCode;

    @ApiModelProperty("产品名")
    private String productName;

    @ApiModelProperty("应用ID")
    private String applicationId;

    @ApiModelProperty("应用名")
    private String applicationName;
}
