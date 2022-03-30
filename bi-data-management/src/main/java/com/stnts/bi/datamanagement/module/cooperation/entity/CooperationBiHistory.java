package com.stnts.bi.datamanagement.module.cooperation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 信息变更记录
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("dm_cooperation_bi_history")
public class CooperationBiHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 合作方ID
     */
    private Long cooperationBiId;

    /**
     * 变更项
     */
    private String changeItem;

    /**
     * 变更前
     */
    private String sourceContent;

    /**
     * 变更后
     */
    private String targetContent;

    /**
     * 创建人
     */
    private String createUser;

    private String createUserId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    private LocalDateTime createTime;


}
