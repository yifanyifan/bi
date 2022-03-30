package com.stnts.bi.datamanagement.module.channel.vo;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 子渠道
 *
 * @author 刘天元
 * @since 2021-02-04
 */
@Data

@ApiModel(value = "子渠道")
public class ChannelChildVO {

    @ApiModelProperty("CCID")
    private String ccid;

    @ApiModelProperty("一级分类")
    @JsonIgnore
    private String firstLevelBusiness;

    @ApiModelProperty("二级分类")
    @JsonIgnore
    private String secondLevelBusiness;

    @ApiModelProperty("三级分类")
    @JsonIgnore
    private String thirdLevelBusiness;

    @ApiModelProperty("供应商id")
    private Long agentId;

    @ApiModelProperty("供应商名称")
    private String agentName;

    @ApiModelProperty("渠道ID")
    private Long channelId;

    @ApiModelProperty("渠道名称")
    private String channelName;

    @ApiModelProperty("子渠道ID")
    private String subChannelId;

    @ApiModelProperty("子渠道名称")
    private String subChannelName;

    @ApiModelProperty("部门Code")
    private String departmentCode;

    @ApiModelProperty("部门名称")
    private String departmentName;

    @ApiModelProperty("客户ID")
    private String customerId;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "更新时间开始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTimeStart;

    @ApiModelProperty(value = "更新时间结束")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTimeEnd;

}
