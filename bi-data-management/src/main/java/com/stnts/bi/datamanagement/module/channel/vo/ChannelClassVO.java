package com.stnts.bi.datamanagement.module.channel.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 渠道类型
 *
 * @author 易樊
 * @since 2021-09-22
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ChannelType对象")
public class ChannelClassVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    @ApiModelProperty("父级ID, -1:部门")
    private Long parentId;

    @ApiModelProperty("部门名称")
    private String departmentCode;

    @ApiModelProperty("部门名称")
    private String departmentName;

    @ApiModelProperty("渠道ID")
    private Long channelId;

    @ApiModelProperty("渠道名称")
    private String channelName;

    @ApiModelProperty("渠道类型ID路径")
    private String channelClassIdPath;

    @ApiModelProperty("渠道类型路径")
    private String channelClassPath;

    @ApiModelProperty("CCID")
    private String ccid;

    @ApiModelProperty("计费方式")
    private String chargeRule;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("负责人名称")
    private String username;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("关联PID个数")
    private Long pidNum;

    @ApiModelProperty("按计费方式")
    private List<ChannelClassNode> treeByChargeRule;
    @ApiModelProperty("按渠道")
    private List<ChannelClassNode> treeByChannel;

    @ApiModelProperty("用户选择节点的集合")
    private List<ChannelClassNode> selectOK;

    @ApiModelProperty("按计费方式")
    private List<String> selectByChargeRule;
    @ApiModelProperty("按渠道")
    private List<String> selectByChannel;
    @ApiModelProperty("按CCID")
    private List<String> selectByCCID;

    @ApiModelProperty("按计费方式（其它节点）")
    private List<String> selectByChargeRuleOther;
    @ApiModelProperty("按渠道（其它节点）")
    private List<String> selectByChannelOther;
    @ApiModelProperty("按CCID（其它节点）")
    private List<String> selectByCCIDOther;

    @ApiModelProperty("按CCID（其它节点）- 按计费方式父级，（如果当前CCID被其它节点选中，则被禁用的父亲级）")
    private List<String> selectByCCIDOtherChargeRuleParent;
    @ApiModelProperty("按CCID（其它节点）- 按渠道父级，（如果当前CCID被其它节点选中，则被禁用的父亲级）")
    private List<String> selectByCCIDOtherChannelParent;

    @ApiModelProperty("负责人名称简化")
    private String usernameSimple;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        this.usernameSimple = StringUtils.isNotBlank(this.getUsername()) && this.getUsername().contains("(") ? this.getUsername().substring(0, this.getUsername().indexOf("(")) : this.getUsername();
    }
}
