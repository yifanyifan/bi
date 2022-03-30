package com.stnts.bi.datamanagement.module.business.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 业务分类
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("dm_business_dict")
@ApiModel(value = "业务考核分类")
public class BusinessDict implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("自增ID")
    private Integer id;

    @ApiModelProperty("年份开始")
    private Integer yearStart;

    @ApiModelProperty("年份结束")
    private Integer yearEnd;

    @ApiModelProperty("部门名称")
    private String rootLevel;

    @ApiModelProperty("部门CODE")
    private String departmentCode;

    @ApiModelProperty("一级分类")
    private String firstLevel;

    @ApiModelProperty("二级分类")
    private String secondLevel;

    @ApiModelProperty("三级分类")
    private String thirdLevel;

    @ApiModelProperty("是否有效。0：否；1：是。")
    @TableLogic(value = "1", delval = "0")//逻辑删除
    private Integer isValid;

    @ApiModelProperty("创建人")
    private String createUser;

    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("创建人")
    private String updateUser;

    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty("更新日期")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    @ApiModelProperty("创建人名称")
    private String createUserName;
    @TableField(exist = false)
    @ApiModelProperty("更新人名称")
    private String updateUserName;
    @TableField(exist = false)
    @ApiModelProperty("更新日期开始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTimeStart;
    @TableField(exist = false)
    @ApiModelProperty("更新日期结束")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTimeEnd;

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
        this.createUserName = StringUtils.isNotBlank(this.createUser) && this.createUser.contains("(") ? this.createUser.substring(0, this.createUser.indexOf("(")) : this.createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
        this.updateUserName = StringUtils.isNotBlank(this.updateUser) && this.updateUser.contains("(") ? this.updateUser.substring(0, this.updateUser.indexOf("(")) : this.updateUser;
    }
}
