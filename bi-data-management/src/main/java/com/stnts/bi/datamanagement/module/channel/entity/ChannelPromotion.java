package com.stnts.bi.datamanagement.module.channel.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.stnts.bi.validator.groups.Add;
import com.stnts.bi.validator.groups.Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 渠道推广
 *
 * @author 刘天元
 * @since 2021-02-04
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("dm_channel_promotion")
@ApiModel(value = "ChannelPromotion对象")
public class ChannelPromotion implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "id不能为空", groups = {Update.class})
    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("PID")
    private String pid;

    @Length(min = 2, max = 55, message = "计费名别名长度为2-55个字符", groups = {Add.class})
    @ApiModelProperty("PID别名")
    private String pidAlias;

    @ApiModelProperty("CCID")
    private String ccid;

    @ApiModelProperty("子渠道id")
    private String subChannelId;

    @ApiModelProperty("推广位ID")
    private Long ppId;

    @ApiModelProperty("产品Code")
    private String productCode;

    @ApiModelProperty("应用表id")
    private Long applicationId;

    @ApiModelProperty("推广媒介id,多个以,分割")
    private String mediumId;

    @Min(value = 1, groups = {Add.class})
    @ApiModelProperty("生成pid数量")
    @TableField(exist = false)
    private Integer pidNum;

    @ApiModelProperty("拓展字段")
    private String extra;

    @ApiModelProperty("负责人id")
    private Long userid;

    @ApiModelProperty("负责人名称")
    private String username;

    @ApiModelProperty(value = "创建时间", hidden = true)
    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "更新时间", hidden = true)
    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "数据有效期开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkStartDate;

    @ApiModelProperty(value = "数据有效期结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkEndDate;

    @ApiModelProperty("内结CCID")
    private String ccidSettlement;

    @ApiModelProperty("数据来源")
    private String dataSource;

    @TableField(exist = false)
    @ApiModelProperty("渠道名称")
    private String channelName;
    @TableField(exist = false)
    @ApiModelProperty("子渠道名称")
    private String subChannelName;
    @TableField(exist = false)
    @ApiModelProperty("推广位名称")
    private String ppName;
    @TableField(exist = false)
    @ApiModelProperty("推广位标识：1:渠道推广位 2:子渠道推广位")
    private Integer ppFlag;
    @TableField(exist = false)
    @ApiModelProperty("产品Id")
    private String productId;
    @TableField(exist = false)
    @ApiModelProperty("产品名称")
    private String productName;
    @TableField(exist = false)
    @ApiModelProperty("应用名称")
    private String applicationName;
    @TableField(exist = false)
    @ApiModelProperty("推广媒介名称")
    private String mediumName;
    @TableField(exist = false)
    @ApiModelProperty("PID别名-不包含数字")
    private String pidAliasStr;
    @TableField(exist = false)
    @ApiModelProperty("数据有效期开始时间字符串")
    private String checkStartDateStr;
    @TableField(exist = false)
    @ApiModelProperty("数据有效期结束时间字符串")
    private String checkEndDateStr;
    @TableField(exist = false)
    @ApiModelProperty("子字段批量更新传ID集合")
    private List<Long> idList;
    @TableField(exist = false)
    @ApiModelProperty("字符串替换源")
    private String replaceSource;
    @TableField(exist = false)
    @ApiModelProperty("字符串替换目标")
    private String replaceTarget;
    @TableField(exist = false)
    @ApiModelProperty("历史CCID数量")
    private String ccidHistoryNum;
    @TableField(exist = false)
    @ApiModelProperty(value = "内结渠道ID")
    private Long channelIdSettlement;
    @TableField(exist = false)
    @ApiModelProperty(value = "内结渠道名称")
    private String channelNameSettlement;
    @TableField(exist = false)
    @ApiModelProperty(value = "PID集合")
    private List<String> pidList;
}
