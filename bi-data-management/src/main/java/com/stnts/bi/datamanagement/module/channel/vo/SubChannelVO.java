package com.stnts.bi.datamanagement.module.channel.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/3/30
 */
@Data
@ApiModel("子渠道搜索条件类")
public class SubChannelVO {

    @ApiModelProperty("子渠道ID")
    private String subChannelId;
    @ApiModelProperty("子渠道名称")
    private String subChannelName;
}
