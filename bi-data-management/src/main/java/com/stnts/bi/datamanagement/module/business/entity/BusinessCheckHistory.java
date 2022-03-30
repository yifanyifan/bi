package com.stnts.bi.datamanagement.module.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * 业务考核历史记录
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("dm_business_check_history")
public class BusinessCheckHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 业务考核ID
     */
    private Long businessCheckId;

    /**
     * 部门
     */
    private String department;

    /**
     * 变更前
     */
    private String sourceContent;

    /**
     * 变更后
     */
    private String targetContent;

    /**
     * 操作
     */
    private String opration;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建人名称
     */
    @TableField(exist = false)
    private String createUserName;

    private String createUserId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
        this.createUserName = StringUtils.isNotBlank(this.createUser) && this.createUser.contains("(") ? this.createUser.substring(0, this.createUser.indexOf("(")) : this.createUser;
    }
}
