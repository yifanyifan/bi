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

/**
 * 产品标签
 *
 * @author 易樊
 * @since 2022-01-26
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("dm_channel_product_label")
@ApiModel(value = "ChannelProductLabel对象")
public class ChannelProductLabel implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "id不能为空", groups = {Update.class})
    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("标签域")
    private String labelArea;

    @ApiModelProperty("标签层级，1：productClass，2：productTheme")
    private String labelLevel;

    @ApiModelProperty("标签值")
    private String labelValue;

    @ApiModelProperty("创建人id")
    private Long userid;

    @ApiModelProperty("创建人")
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
    @ApiModelProperty("标签层级中文")
    private String labelLevelStr;
    @TableField(exist = false)
    @ApiModelProperty("创建人名称")
    private String usernameSimple;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        this.usernameSimple = StringUtils.isNotBlank(this.getUsername()) && this.getUsername().contains("(") ? this.getUsername().substring(0, this.getUsername().indexOf("(")) : this.getUsername();
    }
}
