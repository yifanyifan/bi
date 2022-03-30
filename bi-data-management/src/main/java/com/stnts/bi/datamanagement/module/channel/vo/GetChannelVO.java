package com.stnts.bi.datamanagement.module.channel.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.stnts.bi.datamanagement.common.JacksonSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 渠道合作
 *
 * @author 刘天元
 * @since 2021-02-03
 */
@Data

@ApiModel(value = "渠道列表")
public class GetChannelVO {

    @ApiModelProperty("CCID")
    private String ccid;

    @ApiModelProperty("渠道ID")
    private Long channelId;

    @ApiModelProperty("渠道名称")
    private String channelName;

    @ApiModelProperty("部门名称")
    private String departmentName;

    @ApiModelProperty("部门code")
    private String departmentCode;

    @ApiModelProperty("一级分类")
    private String firstLevelBusiness;

    @ApiModelProperty("二级分类")
    private String secondLevelBusiness;

    @ApiModelProperty("三级分类")
    private String thirdLevelBusiness;

    @ApiModelProperty("计费规则")
    private String chargeRule;

    @ApiModelProperty("渠道费率")
    private Integer channelRate;

    @ApiModelProperty("渠道分成")
    private Integer channelShare;

    @ApiModelProperty("渠道阶梯分成")
    @JsonSerialize(using = JacksonSerializer.StringOrObjectJsonSerialize.class)
    private String channelShareStep;

    @ApiModelProperty("单价")
    private BigDecimal price;

    @ApiModelProperty("负责人id")
    private Long userid;

    @ApiModelProperty("负责人名称")
    private String username;

    @ApiModelProperty("子渠道")
    private List<SubChannel> subChannelList;

    @Data
    public static class SubChannel {
        @ApiModelProperty(value = "子渠道ID", example = "1357283635853475841")
        private String subChannelId;

        @ApiModelProperty(value = "子渠道名称", example = "test")
        private String subChannelName;

        @ApiModelProperty(value = "pid数", example = "10")
        private Integer pidCount;
    }

}
