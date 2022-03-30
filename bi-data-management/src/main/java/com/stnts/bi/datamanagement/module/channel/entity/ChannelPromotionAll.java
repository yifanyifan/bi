package com.stnts.bi.datamanagement.module.channel.entity;

import com.baomidou.mybatisplus.annotation.*;
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
 * <p>
 * 渠道推广宽表
 * </p>
 *
 * @author yifan
 * @since 2021-07-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("dm_channel_promotion_all")
@ApiModel(value = "ChannelPromotionAll对象", description = "渠道推广宽表")
public class ChannelPromotionAll implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "公司ID")
    private String companyId;

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "部门code_推广部门")
    private String departmentCode;

    @ApiModelProperty(value = "部门名称_推广部门")
    private String departmentName;

    @ApiModelProperty(value = "部门code_渠道归属部门")
    private String departmentCodeAttr;

    @ApiModelProperty(value = "部门名称_渠道归属部门")
    private String departmentNameAttr;

    @ApiModelProperty("业务分类ID")
    private Integer businessDictId;

    @ApiModelProperty(value = "一级分类")
    private String firstLevelBusiness;

    @ApiModelProperty(value = "二级分类")
    private String secondLevelBusiness;

    @ApiModelProperty(value = "三级分类")
    private String thirdLevelBusiness;

    @ApiModelProperty(value = "产品code")
    private String productCode;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "应用id")
    private Long applicationId;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "应用名称")
    private String applicationName;

    @ApiModelProperty("公司主体id")
    private String cooperationMainId;

    @ApiModelProperty("公司主体名称")
    private String cooperationMainName;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "CP厂商ID")
    private Long vendorId;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "CP厂商名称")
    private String vendorName;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "CP厂商有效期开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date vendorCheckStartDate;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "CP厂商有效期结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date vendorCheckEndDate;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("推广部门CODE集合")
    private String saleDepartmentCode;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("推广部门名称集合")
    private String saleDepartmentName;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("游戏类别")
    private String productFlag;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("游戏屏幕，单选，游戏类别为H5时，必填")
    private String productScreen;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("游戏类型，多个则用,号分隔")
    private String productClass;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("游戏题材，多个则用,号分隔")
    private String productTheme;

    @ApiModelProperty("是否内结（1：是，2：否）")
    private String settlementType;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "内结渠道ID")
    private Long channelIdSettlement;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "内结渠道名称")
    private String channelNameSettlement;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "内结CCID")
    private String ccidSettlement;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "推广媒介id")
    private String mediumId;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "推广媒介名称")
    private String mediumName;

    @ApiModelProperty(value = "渠道ID")
    private Long channelId;

    @ApiModelProperty(value = "渠道名称")
    private String channelName;

    @ApiModelProperty("客户ID")
    private String customerId;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("CRM类型, 1：渠道版CRM，2: 通用版CRM")
    private String crmType;

    @ApiModelProperty(value = "保密类型1:共享2:私有")
    private String secretType;

    @ApiModelProperty(value = "是否自营（自营1，非自营2）")
    private String channelType;

    @ApiModelProperty(value = "子渠道id")
    private String subChannelId;

    @ApiModelProperty(value = "子渠道名称")
    private String subChannelName;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "推广位ID")
    private Long ppId;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "推广位名称")
    private String ppName;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "推广位类型1:渠道推广位 2:子渠道推广位")
    private String ppFlag;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "插件ID")
    private String plugId;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "插件名称")
    private String plugName;

    @ApiModelProperty(value = "CCID")
    private String ccid;

    @ApiModelProperty(value = "计费规则")
    private String chargeRule;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "结算指标 1.收入2.利润3.注册4.激活")
    private String channelShareType;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "渠道分成")
    private BigDecimal channelShare;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "渠道阶梯分成")
    private String channelShareStep;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "单价")
    private BigDecimal price;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "渠道费率")
    private BigDecimal channelRate = BigDecimal.ZERO;

    @ApiModelProperty(value = "PID")
    private String pid;

    @ApiModelProperty(value = "PID别名")
    private String pidAlias;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "拓展字段")
    private String extra;

    @ApiModelProperty(value = "数据有效期开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkStartDate;

    @ApiModelProperty(value = "数据有效期结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkEndDate;

    @ApiModelProperty(value = "1：最新数据(只有一条)，2：旧数据(迁移产生)")
    private String flag;

    @ApiModelProperty(value = "负责人id")
    private Long userid;

    @ApiModelProperty(value = "负责人名称")
    private String username;

    @ApiModelProperty("数据来源")
    private String dataSource;

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


}
