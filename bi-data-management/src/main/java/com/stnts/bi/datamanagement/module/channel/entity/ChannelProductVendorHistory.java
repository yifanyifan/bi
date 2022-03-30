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
 * 产品历史CP厂商记录
 *
 * @author 易樊
 * @since 2021-09-28
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("dm_channel_product_vendor_history")
@ApiModel(value = "ChannelProductVendorHistory对象")
public class ChannelProductVendorHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "id不能为空", groups = {Update.class})
    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("产品code")
    private String productCode;

    @ApiModelProperty("产品名称")
    private String productName;

    @ApiModelProperty("厂商ID")
    private Long vendorId;

    @ApiModelProperty("厂商名称")
    private String vendorName;

    @ApiModelProperty("数据有效期开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date vendorCheckStartDate;

    @ApiModelProperty("数据有效期结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date vendorCheckEndDate;

    @ApiModelProperty("负责人id")
    private Long userid;

    @ApiModelProperty("负责人名称")
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
