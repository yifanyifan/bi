package com.stnts.bi.datamanagement.module.channel.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.stnts.bi.validator.groups.Add;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.stnts.bi.validator.groups.Update;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 媒介信息
 *
 * @author 刘天元
 * @since 2021-02-04
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("dm_channel_medium")
@ApiModel(value = "ChannelMedium对象")
public class ChannelMedium implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "id不能为空", groups = {Update.class})
    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("部门CODE")
    private String departmentCode;

    @ApiModelProperty("部门名称")
    private String departmentName;

    @Length(min = 1, max = 20, message = "媒介名称限制20个非特殊字符", groups = {Add.class})
    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("修改人id")
    private Long userid;

    @ApiModelProperty("修改人")
    private String username;

    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @TableField(exist = false)
    @ApiModelProperty("修改人名称")
    private String usernameName;
    @TableField(exist = false)
    @ApiModelProperty(value = "关联PID数量")
    private String pIdNum;

    public void setUsername(String username) {
        this.username = username;
        this.usernameName = StringUtils.isNotBlank(this.username) && this.username.contains("(") ? this.username.substring(0, this.username.indexOf("(")) : this.username;
    }

    public String getUsername() {
        return username;
    }
}
