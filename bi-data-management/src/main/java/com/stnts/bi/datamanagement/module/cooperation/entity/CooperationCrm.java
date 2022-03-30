package com.stnts.bi.datamanagement.module.cooperation.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 合作伙伴（CRM）
 * </p>
 *
 * @author yifan
 * @since 2021-07-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("dm_cooperation_crm")
@ApiModel(value="CooperationCrm对象", description="合作伙伴（CRM）")
public class CooperationCrm implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "合作方ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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

    @ApiModelProperty(value = "数据来源标识，1渠道版本CRM，2通用版CRM")
    private Integer dataSource;

    @ApiModelProperty(value = "关联汇总表（dm_cooperation）的id")
    private String relatedCooperationId;

    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "客户信息更新时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


}
