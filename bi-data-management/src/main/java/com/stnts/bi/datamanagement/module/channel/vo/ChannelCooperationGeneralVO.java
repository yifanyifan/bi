package com.stnts.bi.datamanagement.module.channel.vo;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 渠道合作
 *
 * @author 刘天元
 * @since 2021-02-03
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "CCID通用VO对象")
public class ChannelCooperationGeneralVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("部门code")
    private String departmentCode;

    @ApiModelProperty("部门名称")
    private String departmentName;

    @ApiModelProperty("业务分类ID")
    private Integer businessDictId;

    @ApiModelProperty("一级分类")
    private String firstLevelBusiness;

    @ApiModelProperty("二级分类")
    private String secondLevelBusiness;

    @ApiModelProperty("三级分类")
    private String thirdLevelBusiness;

    @ApiModelProperty("供应商id")
    private Long agentId;

    @ApiModelProperty("供应商名称")
    private String agentName;

    @ApiModelProperty("渠道ID")
    private Long channelId;

    @ApiModelProperty("渠道名称")
    private String channelName;

    @ApiModelProperty("CCID")
    private String ccid;

    @ApiModelProperty("结算指标【1.收入2.利润3.注册4.激活】")
    private String channelShareType;

    @ApiModelProperty("计费方式")
    private String chargeRule;

    @ApiModelProperty("渠道费率")
    private BigDecimal channelRate;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("渠道分成")
    private BigDecimal channelShare;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("渠道阶梯分成")
    private String channelShareStep;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("单价")
    private BigDecimal price;

    @ApiModelProperty("负责人id")
    private Long userid;

    @ApiModelProperty(value = "创建时间", hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
