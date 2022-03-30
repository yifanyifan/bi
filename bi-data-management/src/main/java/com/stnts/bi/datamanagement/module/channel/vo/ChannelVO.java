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
@ApiModel("渠道搜索类")
@AllArgsConstructor
public class ChannelVO {

    @ApiModelProperty("渠道ID")
    private Long channelId;
    @ApiModelProperty("渠道名称")
    private String channelName;
}
