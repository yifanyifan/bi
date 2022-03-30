package com.stnts.bi.datamanagement.module.channel.vo;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * 产品信息
 *
 * @author 刘天元
 * @since 2021-02-04
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ChannelProductVO对象")
public class ChannelProductVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("部门CODE")
    private String departmentCode;

    @ApiModelProperty("部门名称")
    private String departmentName;

    @ApiModelProperty("公司主体id")
    private String cooperationMainId;

    @ApiModelProperty("公司主体名称")
    private String cooperationMainName;

    @ApiModelProperty("产品名称")
    private String productName;

    @ApiModelProperty("产品code")
    private String productCode;

    @ApiModelProperty(value = "CP厂商ID")
    private Long vendorId;

    @ApiModelProperty(value = "CP厂商名称")
    private String vendorName;

    @ApiModelProperty(value = "CP厂商有效期开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date vendorCheckStartDate;

    @ApiModelProperty(value = "CP厂商有效期结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date vendorCheckEndDate;

    @ApiModelProperty(value = "应用集合")
    private String applicationListStr;

    @ApiModelProperty("年份")
    private String year;

    @ApiModelProperty("一级分类")
    private String firstLevelBusiness;

    @ApiModelProperty("二级分类")
    private String secondLevelBusiness;

    @ApiModelProperty("三级分类")
    private String thirdLevelBusiness;

    @ApiModelProperty("推广部门CODE集合")
    private String saleDepartmentCode;

    @ApiModelProperty("推广部门名称集合")
    private String saleDepartmentName;

    @ApiModelProperty("负责人名称")
    private String username;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("分成信息ID集合")
    private List<String> channelProductCostIdList;

    @ApiModelProperty("游戏类别")
    private String productFlag;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("游戏屏幕，单选，游戏类别为H5时，必填")
    private String productScreen;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("游戏类型，多个则用,号分隔")
    private String productClass;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("游戏题材，多个则用,号分隔")
    private String productTheme;
}
