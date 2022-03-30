package com.stnts.bi.datamanagement.module.cooperation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 合作伙伴 源表（Youtop）
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("dm_cooperation_youtop")
public class CooperationYoutop implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * youtop的广告主或者媒体id
     */
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
     * 公司全称（营业执照全称）
     */
    private String companyName;

    /**
     * 公司联系人（对接人）
     */
    private String companyContact;

    /**
     * 公司联系人手机
     */
    private String contactPhone;

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
     * 客户信息更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 信息更新版本记录号
     */
    private Integer updateVersion;


}
