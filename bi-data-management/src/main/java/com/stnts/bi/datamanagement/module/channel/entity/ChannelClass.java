package com.stnts.bi.datamanagement.module.channel.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.stnts.bi.validator.groups.Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 渠道类型
 *
 * @author 易樊
 * @since 2021-09-22
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("dm_channel_class")
@ApiModel(value = "ChannelType对象")
public class ChannelClass implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "id不能为空", groups = {Update.class})
    @ApiModelProperty("ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("父级ID, 若部门节点无ID，则只用传code")
    private Long parentId;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("节点CODE，部门编码")
    private String code;

    @NotBlank(message = "节点名称不能为空")
    @ApiModelProperty("节点名称")
    private String name;

    @ApiModelProperty("节点类型，1：目录，2：叶子节点")
    private String nodeType;

    @ApiModelProperty("节点Id路径【含部门，不含当前节点】")
    private String nodeIdPath;

    @ApiModelProperty("节点路径【含部门，不含当前节点】")
    private String nodePath;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("负责人id")
    private Long userid;

    @ApiModelProperty("负责人名称")
    private String username;

    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
