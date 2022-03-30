package com.stnts.bi.datamanagement.module.channel.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.stnts.bi.validator.groups.Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
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
@TableName("dm_channel_product")
@ApiModel(value = "ChannelProduct对象")
public class ChannelProduct implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "id不能为空", groups = {Update.class})
    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
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

    @ApiModelProperty("产品Id")
    private String productId;

    @ApiModelProperty("产品code")
    private String productCode;

    @ApiModelProperty("业务分类ID")
    private Integer businessDictId;

    @ApiModelProperty("一级分类")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String firstLevelBusiness;

    @ApiModelProperty("二级分类")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String secondLevelBusiness;

    @ApiModelProperty("三级分类")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String thirdLevelBusiness;

    @ApiModelProperty(value = "CP厂商ID")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long vendorId;

    @ApiModelProperty(value = "CP厂商名称")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String vendorName;

    @ApiModelProperty(value = "CP厂商有效期开始时间")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date vendorCheckStartDate;

    @ApiModelProperty(value = "CP厂商有效期结束时间")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date vendorCheckEndDate;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("推广部门CODE，多个则用,号分隔")
    private String saleDepartmentCode;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("推广部门名称，多个则用,号分隔")
    private String saleDepartmentName;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("游戏类别，单选")
    private String productFlag;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("游戏屏幕，单选，游戏类别为H5时，必填")
    private String productScreen;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("游戏类型ID集合，多个则用,号分隔")
    private String productClass;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("游戏题材ID集合，多个则用,号分隔")
    private String productTheme;

    @ApiModelProperty("负责人id")
    private Long userid;

    @ApiModelProperty("负责人")
    private String username;

    @ApiModelProperty("数据来源")
    private String dataSource;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("应用ID集合")
    private String applicationIds;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("应用名称集合")
    private String applicationNames;

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
    @ApiModelProperty("关联CCID个数")
    private Long ccidNum;
    @TableField(exist = false)
    @ApiModelProperty("关联PID个数")
    private Long pidNum;
    @TableField(exist = false)
    @ApiModelProperty("应用集合")
    private List<ChannelApplication> applicationList;
    @TableField(exist = false)
    @ApiModelProperty("分成信息ID集合")
    private List<String> channelProductCostIdList;
    @TableField(exist = false)
    @ApiModelProperty("应用id")
    private String applicationId;
    @TableField(exist = false)
    @ApiModelProperty("应用")
    private String applicationName;
    @TableField(exist = false)
    @ApiModelProperty("游戏类型集合，多个则用,号分隔")
    private String productClassStr;
    @TableField(exist = false)
    @ApiModelProperty("游戏题材集合，多个则用,号分隔")
    private String productThemeStr;
    @TableField(exist = false)
    @ApiModelProperty("负责人名称")
    private String usernameName;

    public void setUsername(String username) {
        this.username = username;
        this.usernameName = StringUtils.isNotBlank(this.username) && this.username.contains("(") ? this.username.substring(0, this.username.indexOf("(")) : this.username;
    }

    public String getUsername() {
        return username;
    }
}
