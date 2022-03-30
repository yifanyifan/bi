package com.stnts.bi.datamanagement.module.channel.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.stnts.bi.validator.groups.Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author: liang.zhang
 * @description:  推广位
 * @date: 2021/3/2
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("dm_channel_promotion_position")
@ApiModel(value = "ChannelPromotionPosition对象")
public class ChannelPromotionPosition implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "id不能为空", groups = {Update.class})
    @TableId(value = "pp_id", type = IdType.AUTO)
    @ApiModelProperty("推广位ID")
    private Long ppId;

    @Length(min = 2, max = 55, message = "推广位名称只允许2-55个字符")
    @ApiModelProperty("推广位名称")
    private String ppName;

    @Range(min = 0, max = 1, message = "secretType只能传1、0")
    @ApiModelProperty("推广位状态1：启用，0：停用")
    private Integer ppStatus;

    @ApiModelProperty(value = "渠道ID")
    private Long channelId;

    @ApiModelProperty(value = "子渠道ID")
    private String subChannelId;

    @ApiModelProperty("推广位标识：1:渠道推广位 2:子渠道推广位")
    private Integer ppFlag;

    @ApiModelProperty(value = "插件ID")
    private String plugId;

    @ApiModelProperty(value = "插件名称")
    private String plugName;

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
    @ApiModelProperty(value = "子渠道关联了多少推广位数量")
    private Long subChannelPPIDNumber = 0l;

    @TableField(exist = false)
    @ApiModelProperty(value = "推广位关联了多少PID数量")
    private Long ppIdPIDNumber = 0l;

    @TableField(exist = false)
    @ApiModelProperty(value = "渠道名称")
    private String channelName;

    @TableField(exist = false)
    @ApiModelProperty(value = "子渠道名称")
    private String subChannelName;
    @TableField(exist = false)
    @ApiModelProperty("推广位状态1：启用，0：停用")
    private String ppStatusStr;



}
