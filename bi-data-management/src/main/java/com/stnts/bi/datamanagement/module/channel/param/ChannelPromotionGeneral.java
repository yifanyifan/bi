package com.stnts.bi.datamanagement.module.channel.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 渠道推广
 *
 * @author 刘天元
 * @since 2021-02-04
 */
@Data
@ApiModel(value = "ChannelPromotionGeneral对象")
public class ChannelPromotionGeneral implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("部门名称")
    private String departmentName;

    @ApiModelProperty("部门code")
    private String departmentCode;

    @ApiModelProperty("业务分类ID")
    private Integer businessDictId;

    @ApiModelProperty("一级分类")
    private String firstLevelBusiness;

    @ApiModelProperty("二级分类")
    private String secondLevelBusiness;

    @ApiModelProperty("三级分类")
    private String thirdLevelBusiness;

    @ApiModelProperty("渠道Id")
    private Long channelId;

    @ApiModelProperty("渠道名称")
    private String channelName;

    @ApiModelProperty("供应商id")
    private Long companyId;

    @ApiModelProperty("供应商名称")
    private String companyName;

    @ApiModelProperty("是否自营（自营1，非自营2）")
    private String channelType;

    @ApiModelProperty("保密类型secretType只能传1(共享)、2(私有)")
    private Integer secretType;

    @ApiModelProperty("CCID")
    private String ccid;

    @ApiModelProperty("是否内结（1：是，2：否）")
    private String settlementType;

    @ApiModelProperty("计费方式")
    private String chargeRule;

    @ApiModelProperty("结算指标【1.收入2.利润3.注册4.激活】")
    private String channelShareType;

    @ApiModelProperty("单价")
    private BigDecimal price;

    @ApiModelProperty("渠道分成")
    private BigDecimal channelShare;

    @ApiModelProperty("渠道阶梯分成")
    private String channelShareStep;

    @ApiModelProperty("渠道费率")
    private BigDecimal channelRate;

    @ApiModelProperty("子渠道ID")
    private String subChannelId;

    @ApiModelProperty("子渠道名称")
    private String subChannelName;

    @ApiModelProperty("推广位ID")
    private Long ppId;

    @ApiModelProperty("推广位标识：1:渠道推广位 2:子渠道推广位")
    private Integer ppFlag;

    @ApiModelProperty("推广位名称")
    private String ppName;

    @ApiModelProperty("拓展字段")
    private String extra;

    @ApiModelProperty("产品Id")
    private String productId;

    @ApiModelProperty("产品Code")
    private String productCode;

    @ApiModelProperty("产品名称")
    private String productName;

    @ApiModelProperty("应用表Id")
    private Long applicationId;

    @ApiModelProperty("应用表名称")
    private String applicationName;

    @ApiModelProperty("推广媒介id,多个以,分割")
    private String mediumId;

    @ApiModelProperty("推广媒介名称,多个以,分割")
    private String mediumName;

    @ApiModelProperty("PID别名")
    private String pidAlias;

    @ApiModelProperty("生成pid数量")
    private Integer pidNum;

    @ApiModelProperty("负责人工号")
    private Long userid;

    @ApiModelProperty("负责人名称")
    private String username;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "考核期限开始时间")
    private Date checkStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "考核期限结束时间")
    private Date checkEndDate;

    @ApiModelProperty(value = "内结渠道ID")
    private Long channelIdSettlement;

    @ApiModelProperty(value = "内结渠道名称")
    private String channelNameSettlement;

    @ApiModelProperty("内结CCID")
    private String ccidSettlement;

    @ApiModelProperty("数据来源")
    private String dataSource;

    private List<Map<String, String>> cps;


}
