package com.stnts.bi.datamanagement.module.cooperation.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 合作伙伴汇总表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CRM客户转公司API参数")
public class CooperationAddApiParam implements Serializable {
    private static final long serialVersionUID = 1L;

    private String appId;

    private Long timestamp;

    private String sign;

    @ApiModelProperty("客户ID")
    private String customerId;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("公司ID")
    private String companyId;

    @ApiModelProperty(value = "公司全称（营业执照全称）")
    private String companyName;

    @ApiModelProperty(value = "合作方类型。1=上游客户；2=下游供应商")
    private Integer cooperationType;

    @ApiModelProperty(value = "税务登记号")
    private String companyTaxkey;

    @ApiModelProperty(value = "隐私保护.0=不保护（全公司开放，默认）；1=部门保护（部门可见）；2=私有保护（仅自己可见）")
    private Integer isProtection;

    @ApiModelProperty(value = "BI账号类型(是否测试使用)。0=不是（默认），1=是")
    private Integer isTest;

    @ApiModelProperty(value = "是否属于盛天体系，1是，2否")
    private Integer inSystem;

    @ApiModelProperty(value = "合同性质。1销售合同，2采购合同")
    private Integer natureContract;

    @ApiModelProperty(value = "公司法人代表（营业执照）")
    private String companyLegal;

    @ApiModelProperty(value = "公司资质（上传的一些资质证明）")
    private String companyQualification;

    @ApiModelProperty(value = "开户行名称")
    private String bankName;

    @ApiModelProperty(value = "开户行号")
    private String bankNumber;

    @ApiModelProperty(value = "银行卡号")
    private String cardNumber;

    @ApiModelProperty(value = "CRM类型, 1：渠道版CRM，2: 通用版CRM")
    private String crmType;

    @ApiModelProperty("渠道ID")
    private String channelId;

    @ApiModelProperty("渠道名称")
    private String channelName;

    @ApiModelProperty("部门CODE（目前只有渠道中心、互娱）")
    private String departmentCode;

    @ApiModelProperty("部门名称（目前只有渠道中心、互娱）")
    private String departmentName;

    @ApiModelProperty("是否自营（自营1，非自营2）")
    private String channelType;

    @ApiModelProperty("保密类型secretType只能传1(共享)、2(私有)")
    private Integer secretType;

    @ApiModelProperty("是否内结（1：是，2：否）")
    private String settlementType;

    @ApiModelProperty("用户ID")
    private String userId;

    @ApiModelProperty("用户名称")
    private String userName;

    @ApiModelProperty("用户名称")
    private String dataSource;
}
