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

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 产品分成
 *
 * @author 易樊
 * @since 2021-09-24
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("dm_channel_product_cost")
@ApiModel(value = "ChannelProductCost对象")
public class ChannelProductCost implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "id不能为空", groups = {Update.class})
    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("分成策略")
    private String costName;

    @ApiModelProperty("产品code")
    private String productCode;

    @ApiModelProperty("结算指标 1.收入2.利润3.注册4.激活")
    private String channelShareType;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("渠道分成")
    private BigDecimal channelShare;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("渠道阶梯分成")
    private String channelShareStep;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("渠道费率")
    private BigDecimal channelRate = BigDecimal.ZERO;

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
    @ApiModelProperty("用户选择节点的集合")
    private List<Map<String, Object>> selectRequest;
    @TableField(exist = false)
    @ApiModelProperty("全部")
    private List<String> selectByAll;
    @TableField(exist = false)
    @ApiModelProperty("类型")
    private List<String> selectByChannelClass;
    @TableField(exist = false)
    @ApiModelProperty("CCID")
    private List<String> selectByCCID;
    @TableField(exist = false)
    @ApiModelProperty("未知")
    private List<String> selectByNo;
}
