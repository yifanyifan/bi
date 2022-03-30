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
 * 合作伙伴 源表（EAS金蝶）
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("dm_cooperation_eas")
public class CooperationEas implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * eas合作方编码
     */
    @TableId(value = "eas_code")
    private String easCode;

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
     * 所属行业（一级）
     */
    private String parentIndustry;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人（公司员工）
     */
    private String createUser;

    /**
     * 创建人所属部门
     */
    private String createDepartment;

    /**
     * 核准状态（财务审查核实）
     */
    private Integer isApproved;

    /**
     * 是否集团内公司
     */
    private Integer isSelf;

    /**
     * 当前跟进状态
     */
    private Integer lastStatus;

    /**
     * 最新跟进记录
     */
    private String lastRemark;

    /**
     * 数据来源标识
     */
    private Integer dataSource;

    /**
     * 是否关联合同
     */
    private Integer isRelateContract;

    /**
     * 关联合同ID（集合）
     */
    private String contractIdSet;

    /**
     * 更新者
     */
    private String updateUser;

    /**
     * 客户信息更新时间
     */
    private LocalDateTime updateTime;

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
