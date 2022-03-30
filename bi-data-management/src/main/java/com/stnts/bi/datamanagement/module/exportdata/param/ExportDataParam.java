package com.stnts.bi.datamanagement.module.exportdata.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "数据导入参数")
public class ExportDataParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("PID")
    private String pid;

    @ApiModelProperty("计费名别名")
    private String pidAlias;

    @ApiModelProperty("推广部门名称")
    private String promoteDepartmentName;

    @ApiModelProperty("推广部门CODE（非Excel参数）")
    private String promoteDepartmentCode;

    @ApiModelProperty(value = "内结渠道ID（非Excel参数）")
    private Long channelIdSettlement;

    @ApiModelProperty(value = "内结渠道名称（非Excel参数）")
    private String channelNameSettlement;

    @ApiModelProperty("内结CCID")
    private String ccidSettlement;

    @ApiModelProperty("PID负责人工号")
    private String pidUsername;

    @ApiModelProperty("数据来源")
    private String dataSource;

    /*************************** 业务分类 *******************************/
    @ApiModelProperty("业务分类ID")
    private Integer businessDictId;

    @ApiModelProperty("一级分类")
    private String firstLevelBusiness;

    @ApiModelProperty("二级分类")
    private String secondLevelBusiness;

    @ApiModelProperty("三级分类")
    private String thirdLevelBusiness;

    /*************************** 渠道信息 *******************************/
    @ApiModelProperty("公司名称")
    private String companyId;

    /*@ApiModelProperty("公司名称（非Excel参数）")
    private String companyName;*/

    @ApiModelProperty("渠道名称")
    private String channelName;

    @ApiModelProperty("渠道归属部门名称")
    private String channelDepartmentName;

    @ApiModelProperty("渠道归属部门CODE（非Excel参数）")
    private String channelDepartmentCode;

    @ApiModelProperty("是否自营（1自营、2非自营）")
    private String channelType;

    @ApiModelProperty("保密类型（1共享、2私有）")
    private String secretType;

    @ApiModelProperty("是否内结（1是、2否）")
    private String settlementType;

    @ApiModelProperty("子渠道名称")
    private String subChannelName;

    @ApiModelProperty("推广位名称")
    private String ppName;

    @ApiModelProperty("推广位标识（1渠道推广位、2子渠道推广位)")
    private String ppFlag;

    @ApiModelProperty(value = "插件ID")
    private String plugId;

    @ApiModelProperty(value = "插件名称")
    private String plugName;

    @ApiModelProperty("Product负责人名称")
    private String productUsername;

    /*************************** 产品信息 *******************************/
    @ApiModelProperty("产品名称")
    private String productName;

    @ApiModelProperty("产品code")
    private String productCode;

    @ApiModelProperty(value = "应用名称")
    private String applicationName;

    /*************************** 推广媒介 *******************************/
    @ApiModelProperty("推广媒介（多个则用 | 分隔）")
    private String mediumName;

    /*************************** 渠道计费信息 *******************************/
    @ApiModelProperty("计费方式")
    private String chargeRule;

    @ApiModelProperty("结算指标【1.收入2.利润3.注册4.激活】")
    private String channelShareType;

    @ApiModelProperty("渠道分成（固定分成/阶梯分成）")
    private String channelShareFlag;

    @ApiModelProperty("分成比例（固定分成/阶梯分成）")
    private String channelShare;

    @ApiModelProperty("渠道阶梯分成")
    private String channelShareStep;

    @ApiModelProperty("单价")
    private String price;

    @ApiModelProperty("渠道费率")
    private String channelRate;

    @ApiModelProperty("CCID负责人名称")
    private String ccidUsername;

    @ApiModelProperty("是否网吧")
    private String isWB;

    @ApiModelProperty("是否移动端")
    private String isYDD;
}
