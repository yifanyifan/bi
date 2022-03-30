package com.stnts.bi.datamanagement.module.cooperation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 合作伙伴跟进状态
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("dm_cooperation_status_switch_history")
public class CooperationStatusSwitchHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 合作方ID
     */
    private Long cooperationId;

    /**
     * 跟进状态。合作状态：1=启用（默认），2=停用（暂停合作）
     */
    private Integer status;

    /**
     * 跟进记录
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人（公司员工）
     */
    private String createUserName;

    /**
     * 创建人id（公司员工）
     */
    private String createUserId;

}
