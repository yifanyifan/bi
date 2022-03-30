package com.stnts.bi.datamanagement.module.cooperation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * VIEW
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("dm_cooperation_source")
public class CooperationSource implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private Integer cooperationType;

    @TableField(exist = false)
    private String cooperationTypeDisplay;

    private String cpName;

    private String companyName;

    private String companyTaxkey;

    private String companyContact;

    private String contactPhone;

    private String companyWebsite;

    private String companyAddress;

    private String createUser;

    /**
     * 创建人id（公司员工）
     */
    private String createUserId;

    private String createDepartment;

    /**
     * 创建人所属部门编码
     */
    private String createDepartmentCode;

    private Integer dataSource;

    private Long relatedCooperationId;

    /**
     * 是否启用隐私保护和保护级别.0=不保护（全公司开放，默认）；1=部门保护（部门可见）；2=私有保护（仅自己可见）
     */
    private Integer isProtection;

    /**
     * 隐私保护是否生效。0：否；1：是
     */
    @TableField(exist = false)
    private Integer isProtectionActive;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
