package com.stnts.bi.datamanagement.module.channel.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 渠道类型关联CCID
 *
 * @author 易樊
 * @since 2021-09-22
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ChannelClassCooperation对象")
public class ChannelClassCooperationVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("ID")
    private Long id;

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

    @ApiModelProperty("渠道类型ID路径")
    private String channelClassIdPath;

    @ApiModelProperty("渠道类型路径")
    private String channelClassPath;

    @ApiModelProperty("CCID")
    private String ccid;

    @ApiModelProperty("计费方式")
    private String chargeRule;

    @ApiModelProperty(value = "渠道ID")
    private Long channelId;

    @ApiModelProperty(value = "渠道名称")
    private String channelName;

    @ApiModelProperty("节点路径")
    private String nodePath;

    @ApiModelProperty("负责人名称")
    private String username;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("按计费方式")
    private List<String> selectByChargeRule;
    @ApiModelProperty("按渠道")
    private List<String> selectByChannel;
    @ApiModelProperty("按CCID")
    private List<String> selectByCCID;
}
