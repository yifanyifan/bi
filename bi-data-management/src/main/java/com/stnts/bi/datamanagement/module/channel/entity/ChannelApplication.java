package com.stnts.bi.datamanagement.module.channel.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.stnts.bi.validator.groups.Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 应用表
 *
 * @author 易樊
 * @since 2022-01-13
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("dm_channel_application")
@ApiModel(value = "ChannelApplication对象")
public class ChannelApplication implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "id不能为空", groups = {Update.class})
    @ApiModelProperty("业务考核ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("应用")
    private String applicationName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建人id")
    private Long userid;

    @ApiModelProperty("创建人名称")
    private String username;

    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "创建时间", hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @TableField(exist = false)
    @ApiModelProperty("产品名称")
    private String departmentCodeParam;
    @TableField(exist = false)
    @ApiModelProperty("产品名称")
    private String departmentNameParam;
    @TableField(exist = false)
    @ApiModelProperty("产品CODE")
    private String productCodeParam;
    @TableField(exist = false)
    @ApiModelProperty("产品名称")
    private String productNameParam;
    @TableField(exist = false)
    @ApiModelProperty("产品CODE集合")
    private List<String> productCodeList;
    @TableField(exist = false)
    @ApiModelProperty("负责人名称简化")
    private String usernameSimple;

    public void setUsername(String username) {
        this.username = username;
        this.usernameSimple = StringUtils.isNotBlank(this.username) && this.username.contains("(") ? this.username.substring(0, this.username.indexOf("(")) : this.username;
    }

    public String getUsername() {
        return username;
    }
}
