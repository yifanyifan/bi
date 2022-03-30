package com.stnts.bi.datamanagement.module.channel.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 渠道合作
 *
 * @author 刘天元
 * @since 2021-02-03
 */
@Data
@ApiModel(value = "供应商列表")
public class GetAgentVO implements Serializable {

    @ApiModelProperty("供应商id")
    private Long agentId;

    @ApiModelProperty("供应商名称")
    private String agentName;

    @ApiModelProperty("渠道信息")
    private List<Channel> channelList;

    @ApiModelProperty(value = "前端渠道名称显示。", example = "龙管家、去上网")
    private String channelDisplay;

    @ApiModelProperty(value = "子渠道数量", example = "8")
    private Integer subChannelCount;

    @ApiModelProperty("部门名称")
    private String departmentName;

    @ApiModelProperty("部门code")
    private String departmentCode;

    @Data
    public static class Channel {
        @ApiModelProperty("渠道ID")
        private Long channelId;

        @ApiModelProperty("渠道名称")
        private String channelName;
    }
}
