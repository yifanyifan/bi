package com.stnts.bi.datamanagement.module.channel.vo;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.stnts.bi.datamanagement.common.JacksonSerializer;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelPromotion;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 渠道推广
 *
 * @author 刘天元
 * @since 2021-02-04
 */
@Data
@ApiModel(value = "ChannelPromotionVO对象")
public class ChannelPromotionVO implements Serializable {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("PID")
    private String pid;

    @ApiModelProperty("PID别名")
    private String pidAlias;

    @ApiModelProperty("CCID")
    private String ccid;

    @ApiModelProperty("公司id")
    private Long agentId;

    @ApiModelProperty("公司名称")
    private String agentName;

    @ApiModelProperty("渠道ID")
    private Long channelId;

    @ApiModelProperty("渠道名称")
    private String channelName;

    @ApiModelProperty("渠道_部门名称")
    private String channelDepartmentName;

    @ApiModelProperty("渠道_保密类型")
    private String secretTypeStr;

    @ApiModelProperty("子渠道id")
    private String subChannelId;

    @ApiModelProperty("子渠道名称")
    private String subChannelName;

    @ApiModelProperty("产品id")
    private Long productId;

    @ApiModelProperty("产品Code")
    private String productCode;

    @ApiModelProperty("产品名称")
    private String productName;

    @ApiModelProperty("产品部门编码")
    private String productDepartmentCode;

    @ApiModelProperty("产品部门名称")
    private String productDepartmentName;

    @ApiModelProperty("应用id")
    private String applicationId;

    @ApiModelProperty("应用")
    private String applicationName;

    @ApiModelProperty("产品名称+应用名称")
    private String productNameAndApplicationName;

    @ApiModelProperty("推广媒介id,多选以,形式分割")
    private String mediumId;

    @ApiModelProperty("推广媒介名称,多选以,形式分割")
    private String mediumName;

    @ApiModelProperty("拓展字段")
    @JsonSerialize(using = JacksonSerializer.StringOrObjectJsonSerialize.class)
    private String extra;

    @ApiModelProperty("负责人id")
    private Long userid;

    @ApiModelProperty("负责人")
    private String username;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty("部门Code")
    private String departmentCode;

    @ApiModelProperty("部门名称")
    private String departmentName;

    @ApiModelProperty("推广位ID")
    private Long ppId;

    @ApiModelProperty("推广位名称")
    private String promotionPositionName;

    @ApiModelProperty("一级分类")
    private String firstLevelBusiness;

    @ApiModelProperty("二级分类")
    private String secondLevelBusiness;

    @ApiModelProperty("三级分类")
    private String thirdLevelBusiness;

    @ApiModelProperty(value = "数据有效期开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkStartDate;

    @ApiModelProperty(value = "数据有效期结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkEndDate;

    @ApiModelProperty(value = "数据有效期开始时间~数据有效期结束时间")
    private String checkDateStr;

    @ApiModelProperty("负责人名称")
    private String usernameName;

    @ApiModelProperty("PID属性集合")
    private List<ChannelPromotion> channelPromotionList;

    @ApiModelProperty("PID别名-不包含数字")
    private String pidAliasStr;

    @ApiModelProperty("历史CCID数量")
    private String ccidHistoryNum;

    // 内结信息 Start
    @ApiModelProperty(value = "内结渠道ID")
    private Long channelIdSettlement;

    @ApiModelProperty(value = "内结渠道名称")
    private String channelNameSettlement;

    @ApiModelProperty("内结公司id")
    private Long agentIdSettlement;

    @ApiModelProperty("内结公司名称")
    private String agentNameSettlement;

    @ApiModelProperty("内结CCID")
    private String ccidSettlement;

    @ApiModelProperty("计费方式")
    private String chargeRuleSettlement;

    @ApiModelProperty("结算指标")
    private String channelShareTypeSettlement;

    @ApiModelProperty("结算指标")
    private String channelShareTypeStrSettlement;

    @ApiModelProperty("渠道费率")
    private BigDecimal channelRateSettlement = BigDecimal.ZERO;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("渠道分成")
    private BigDecimal channelShareSettlement;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("渠道阶梯分成")
    private String channelShareStepSettlement;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("单价")
    private BigDecimal priceSettlement;
    // 内结信息 End

    public void setUsername(String username) {
        this.username = username;
        this.usernameName = StringUtils.isNotBlank(this.username) && this.username.contains("(") ? this.username.substring(0, this.username.indexOf("(")) : this.username;
    }

    public String getUsername() {
        return username;
    }
}
