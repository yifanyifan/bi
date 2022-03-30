package com.stnts.bi.datamanagement.module.channel.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelCooperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 产品分成
 *
 * @author 易樊
 * @since 2021-09-24
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ChannelProductCost对象")
public class ChannelProductCostVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("分成策略")
    private String costName;

    @ApiModelProperty("部门CODE")
    private String departmentCode;

    @ApiModelProperty("产品code")
    private String productCode;

    @ApiModelProperty("渠道ID")
    private String channelId;

    @ApiModelProperty("渠道名称")
    private String channelName;

    @ApiModelProperty("ccid")
    private String ccid;

    @ApiModelProperty("计费方式")
    private String chargeRule;

    @ApiModelProperty("结算指标 1.收入2.利润3.注册4.激活")
    private String channelShareType;

    @ApiModelProperty("渠道分成")
    private BigDecimal channelShare;

    @ApiModelProperty("渠道阶梯分成")
    private String channelShareStep;

    @ApiModelProperty("渠道费率")
    private BigDecimal channelRate = BigDecimal.ZERO;

    @ApiModelProperty("渠道类型ID路径")
    private String channelClassIdPath;

    @ApiModelProperty("渠道类型路径")
    private String channelClassPath;

    @ApiModelProperty("负责人id")
    private Long userid;

    @ApiModelProperty("负责人名称")
    private String username;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty("树结构")
    private ChannelClassNode channelClassNodeRoot;

    @ApiModelProperty("关联的渠道类型")
    private List<ChannelCooperation> channelCooperationList;

    @ApiModelProperty("全部")
    private List<String> selectByAll;
    @ApiModelProperty("类型")
    private List<String> selectByChannelClass;
    @ApiModelProperty("CCID")
    private List<String> selectByCCID;
    @ApiModelProperty("未知")
    private List<String> selectByNo;

    @ApiModelProperty("全部")
    private List<String> selectByAllOther;
    @ApiModelProperty("类型-其它节点")
    private List<String> selectByChannelClassOther;
    @ApiModelProperty("CCID-其它节点")
    private List<String> selectByCCIDOther;
    @ApiModelProperty("未知-其它节点")
    private List<String> selectByNoOther;

    @ApiModelProperty("全部-父亲节点")
    private List<String> selectByAllOtherParent;
    @ApiModelProperty("类型-其它节点-父节点")
    private List<String> selectByChannelClassOtherParent;
    @ApiModelProperty("CCID-其它节点-父节点")
    private List<String> selectByCCIDOtherParent;
    @ApiModelProperty("未知-其它节点-父节点")
    private List<String> selectByNoOtherParent;
}
