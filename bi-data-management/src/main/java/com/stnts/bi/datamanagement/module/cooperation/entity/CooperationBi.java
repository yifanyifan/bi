package com.stnts.bi.datamanagement.module.cooperation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 合作伙伴 源表（BI平台+订单系统）
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("dm_cooperation_bi")
public class CooperationBi implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 合作方ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 合作方类型。1=上游客户；2=下游供应商
     */
    private Integer cooperationType;

    /**
     * 客户简称（公司简称）
     */
    private String cpName;

    /**
     * 助记码（类似简称/英文缩写）
     */
    private String cpCode;

    /**
     * 公司全称（营业执照全称）
     */
    private String companyName;

    /**
     * 公司合作类型.1=代理;2=直客
     */
    private Integer companyType;

    /**
     * 税务登记号
     */
    private String companyTaxkey;

    /**
     * 公司法人代表（营业执照）
     */
    private String companyLegal;

    /**
     * 公司电话
     */
    private String companyTel;

    /**
     * 公司联系人（对接人）
     */
    private String companyContact;

    /**
     * 公司联系人姓名
     */
    private String companyContactName;

    /**
     * 公司联系人手机
     */
    private String contactPhone;

    /**
     * 联系邮箱
     */
    private String contactMail;

    /**
     * 传真
     */
    private String contactFax;

    /**
     * 公司网址（官网）
     */
    private String companyWebsite;

    /**
     * 公司地址
     */
    private String companyAddress;

    /**
     * 公司简介
     */
    private String companyDesc;

    /**
     * 主要产品说明
     */
    private String companyProducts;

    /**
     * 1=50人以下，2=50-200人，3=200-1000人，4=1000人以上
     */
    private Integer companySize;

    /**
     * 公司资质（上传的一些资质证明）
     */
    private String companyQualification;

    /**
     * 所属行业（一级）
     */
    private String parentIndustry;

    /**
     * 所属行业（二级）
     */
    private String childIndustry;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人（公司员工）
     */
    private String createUser;

    /**
     * 创建人id（公司员工）
     */
    private String createUserId;

    /**
     * 创建人所属部门
     */
    private String createDepartment;

    /**
     * 创建人所属部门编码
     */
    private String createDepartmentCode;

    /**
     * 经手人（公司员工）
     */
    private String handlerUser;

    /**
     * 经手人姓名
     */
    private String handlerUserName;

    /**
     * 是否启用隐私保护和保护级别.0=不保护（全公司开放，默认）；1=部门保护（部门可见）；2=私有保护（仅自己可见）
     */
    private Integer isProtection;

    /**
     * 是否测试使用。0=不是（默认），1=是
     */
    private Integer isTest;

    /**
     * 当前跟进状态。合作状态：1=启用（默认），2=停用（暂停合作）
     */
    private Integer lastStatus;

    /**
     * 最新跟进记录
     */
    private String lastRemark;

    /**
     * 数据来源标识。来源方式：1. BI平台新增；2. 金蝶同步；3. 友拓同步；4. 友拓新增；5. 业务方订单系统新增
     */
    private Integer dataSource;


    /**
     * 更新者
     */
    private String updateUser;

    /**
     * 更新人id
     */
    private String updateUserId;

    /**
     * 客户信息更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 客户关联产品信息
     */
    private String relatedProducts;

    /**
     * 最后订单时间
     */
    private LocalDateTime lastOrderTime;

    /**
     * 信息更新版本记录号
     */
    private Integer updateVersion;

    /**
     * 关联汇总表（dm_cooperation）的id
     */
    @JsonIgnore
    private Long relatedCooperationId;
}
