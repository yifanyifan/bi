package com.stnts.bi.datamanagement.module.channel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.stnts.bi.validator.groups.Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 渠道类型关联CCID
 *
 * @author 易樊
 * @since 2021-09-22
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("dm_channel_class_cooperation")
@ApiModel(value = "ChannelClassCooperation对象")
public class ChannelClassCooperation implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "id不能为空", groups = {Update.class})
    @ApiModelProperty("ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @NotNull(message = "渠道类型ID不能为空")
    @ApiModelProperty("渠道类型ID")
    private Long channelClassId;

    @ApiModelProperty("分类对应标识")
    private String modeId;

    @ApiModelProperty("分类模式：1.按计费方式(CPS/CPA/CPD等), 2.按渠道, 3.按CCID")
    private String modeType;

    @ApiModelProperty("部门编码")
    private String departmentCode;

    @ApiModelProperty("部门名称")
    private String departmentName;

    @ApiModelProperty("渠道类型ID路径【不含部门】")
    private String channelClassIdPath;

    @ApiModelProperty("渠道类型路径【不含部门】")
    private String channelClassPath;

    @ApiModelProperty("负责人名称")
    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
