package com.stnts.bi.datamanagement.module.channel.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/3/30
 */
@Data
@AllArgsConstructor
@ApiModel("PID接口VO_ZA")
public class ChannelPromotionZaVO {
    @ApiModelProperty("PID")
    private String pid;
    @ApiModelProperty("CCID")
    private String ccid;
    @ApiModelProperty("渠道ID")
    private String channelId;
    @ApiModelProperty("子渠道ID")
    private String subChannelId;
    @ApiModelProperty("推广位ID")
    private String promoteId;
    @ApiModelProperty("产品ID")
    private String productId;
    @ApiModelProperty("PID创建时间")
    private String pidCreateTime;
}
