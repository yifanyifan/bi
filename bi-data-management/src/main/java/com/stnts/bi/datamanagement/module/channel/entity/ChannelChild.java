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
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 子渠道
 *
 * @author 刘天元
 * @since 2021-02-04
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("dm_channel_child")
@ApiModel(value = "ChannelChild对象")
public class ChannelChild implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "id不能为空", groups = {Update.class})
    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("CCID")
    private String ccid;

    @NotNull(message = "渠道ID不能为空")
    @ApiModelProperty("渠道ID")
    private Long channelId;

    @ApiModelProperty("子渠道ID")
    private String subChannelId;

    @NotNull(message = "子渠道名称不能为空")
    @Length(min = 2, max = 55, message = "子渠道名称只允许2-55个字符")
    @ApiModelProperty("子渠道名称")
    private String subChannelName;

    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "更新时间", hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty("数据来源")
    private String dataSource;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品编码")
    private String productCode;
    @TableField(exist = false)
    @ApiModelProperty(value = "产品名")
    private String productName;
    @TableField(exist = false)
    @ApiModelProperty(value = "应用ID")
    private String applicationId;
    @TableField(exist = false)
    @ApiModelProperty(value = "应用名")
    private String applicationName;
    @TableField(exist = false)
    @ApiModelProperty(value = "关联PID数量")
    private String pIdNum;
    @TableField(exist = false)
    @ApiModelProperty(value = "关联子渠道推广位数量")
    private Long subChannelPPIDNumber;
    @TableField(exist = false)
    @ApiModelProperty(value = "子渠道对应推广位集合")
    private List<ChannelPromotionPosition> channelPromotionPositionList;
    @TableField(exist = false)
    @ApiModelProperty("渠道名称")
    private String channelName;
    @TableField(exist = false)
    @ApiModelProperty("推广位ID")
    private Long ppId;
    @TableField(exist = false)
    @ApiModelProperty("推广位名称")
    private String ppName;
    @TableField(exist = false)
    @ApiModelProperty("推广位状态1：启用，0：停用")
    private Integer ppStatus;
    @TableField(exist = false)
    @ApiModelProperty("推广位状态")
    private String ppStatusStr;
    @TableField(exist = false)
    @ApiModelProperty(value = "插件ID")
    private String plugId;
    @TableField(exist = false)
    @ApiModelProperty(value = "插件名称")
    private String plugName;
}
