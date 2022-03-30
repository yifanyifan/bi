package com.stnts.bi.datamanagement.module.channel.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.stnts.bi.validator.groups.Update;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 产品分成关联CCID
 *
 * @author 易樊
 * @since 2021-09-24
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("dm_channel_product_cost_cooperation")
@ApiModel(value = "ChannelProductCostCooperation对象")
public class ChannelProductCostCooperation implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "id不能为空", groups = {Update.class})
    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @NotNull(message = "产品分成ID不能为空")
    @ApiModelProperty("产品分成ID")
    private Long channelProductCostId;

    @ApiModelProperty("分类对应标识：-1.全部, 54.渠道类型ID, CCID10004CPMVZE.CCID, 0.未知")
    private String modeId;

    @ApiModelProperty("分类模式：1.全部, 2.渠道类型, 3.CCID, 4.未知")
    private String modeType;

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

}
