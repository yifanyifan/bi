package com.stnts.bi.schedule.deduct.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class PidDayVO implements Serializable {
    @ApiModelProperty("日期")
    private String dateNow;

    @ApiModelProperty("PID")
    private String pid;

    @ApiModelProperty("CCID")
    private String ccid;

    @ApiModelProperty("产品code")
    private String productCode;

    @ApiModelProperty("产品名称")
    private String productName;

    @ApiModelProperty("渠道ID")
    private String channelId;

    @ApiModelProperty("渠道名称")
    private String channelName;

    @ApiModelProperty("子渠道ID")
    private String subChannelId;

    @ApiModelProperty("子渠道名称")
    private String subChannelName;

    @ApiModelProperty("计费规则")
    private String chargeRule;

    @ApiModelProperty("启动PC")
    private String pc;

    @ApiModelProperty("UV")
    private String uv;

    @ApiModelProperty("原始注册数")
    private String regCount;

    @ApiModelProperty("原始订单金额(元)")
    private String payFee;

    @ApiModelProperty("原始订单量")
    private String payOrderCount;

    @ApiModelProperty("设置开始日期")
    private String createdAt;
}
