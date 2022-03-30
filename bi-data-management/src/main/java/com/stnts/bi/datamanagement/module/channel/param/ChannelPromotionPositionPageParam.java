package com.stnts.bi.datamanagement.module.channel.param;

import com.stnts.bi.entity.common.BasePageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 *  @author liang.zhang
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "推广位分页参数")
public class ChannelPromotionPositionPageParam extends BasePageParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("渠道ID")
    private Long channelId;

    @ApiModelProperty("子渠道ID")
    private String subChannelId;

    @ApiModelProperty("推广位状态1：启用，0：停用")
    private Integer ppStatus;

    @ApiModelProperty("为了前端展示需要,当传pid时把对应推广位信息加到列表中")
    private String pid;

    @ApiModelProperty("推广位标识：1:渠道推广位 2:子渠道推广位")
    private Integer ppFlag;

    @ApiModelProperty("推广位Id")
    private String ppId;

    @ApiModelProperty("推广位名称")
    private String ppName;

    @ApiModelProperty(value = "插件ID")
    private String plugId;

    @ApiModelProperty(value = "插件名称")
    private String plugName;
}
