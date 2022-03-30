package com.stnts.bi.datamanagement.module.channel.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 渠道推广迁移历史表
 * </p>
 *
 * @author yifan
 * @since 2021-06-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("dm_channel_promotion_history")
@ApiModel(value = "ChannelPromotionHistory对象", description = "渠道推广迁移历史表")
public class ChannelPromotionHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "PID")
    private String pid;

    @ApiModelProperty(value = "PID别名")
    private String pidAlias;

    @ApiModelProperty(value = "CCID")
    private String ccid;

    @ApiModelProperty(value = "渠道ID")
    private Long channelId;

    @ApiModelProperty(value = "渠道名称")
    private String channelName;

    @ApiModelProperty(value = "子渠道id")
    private String subChannelId;

    @ApiModelProperty(value = "子渠道名称")
    private String subChannelName;

    @ApiModelProperty(value = "推广位ID")
    private Long ppId;

    @ApiModelProperty("产品表ID【自增ID】")
    private Long productId;

    @ApiModelProperty(value = "产品code")
    private String productCode;

    @ApiModelProperty(value = "应用id")
    private String applicationId;

    @ApiModelProperty(value = "推广媒介id")
    private String mediumId;

    @ApiModelProperty(value = "拓展字段")
    private String extra;

    @ApiModelProperty(value = "部门code")
    private String departmentCode;

    @ApiModelProperty(value = "部门名称")
    private String departmentName;

    @ApiModelProperty(value = "一级分类")
    private String firstLevelBusiness;

    @ApiModelProperty(value = "二级分类")
    private String secondLevelBusiness;

    @ApiModelProperty(value = "三级分类")
    private String thirdLevelBusiness;

    @ApiModelProperty(value = "计费方式")
    private String chargeRule;

    @ApiModelProperty(value = "结算指标 1.收入2.利润3.注册4.激活")
    private String channelShareType;

    @ApiModelProperty(value = "渠道分成")
    private BigDecimal channelShare;

    @ApiModelProperty(value = "渠道阶梯分成")
    private String channelShareStep;

    @ApiModelProperty(value = "单价")
    private BigDecimal price;

    @ApiModelProperty(value = "渠道费率")
    private BigDecimal channelRate = BigDecimal.ZERO;

    @ApiModelProperty(value = "迁移人id")
    private Long operid;

    @ApiModelProperty(value = "迁移人名称")
    private String opername;

    @ApiModelProperty(value = "负责人id")
    private Long userid;

    @ApiModelProperty(value = "负责人")
    private String username;

    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "数据有效期开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkStartDate;

    @ApiModelProperty(value = "数据有效期结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkEndDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "迁移人名称")
    private String opernameName;

    public String getOpername() {
        return opername;
    }

    public void setOpername(String opername) {
        this.opername = opername;
        this.opernameName = StringUtils.isNotBlank(this.opername) && this.opername.contains("(") ? this.opername.substring(0, this.opername.indexOf("(")) : this.opername;
    }
}
