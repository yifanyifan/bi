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
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/3/1
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("dm_channel")
@ApiModel(value = "Channel对象")
public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "id不能为空", groups = {Update.class})
    @ApiModelProperty("主键")
    @TableId(value = "channel_id", type = IdType.INPUT)
    private Long channelId;

    @NotNull
    @Length(min = 2, max = 55, message = "渠道名称只允许2-55个字符")
    @ApiModelProperty("渠道名称")
    private String channelName;

    //CRM
    @ApiModelProperty("客户ID")
    private String customerId;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("CRM类型, 1：渠道版CRM，2: 通用版CRM")
    private String crmType;

    @NotNull
    @ApiModelProperty("公司ID")
    private Long companyId;

    @NotNull
    @ApiModelProperty("公司名称")
    private String companyName;

    @NotNull
    @ApiModelProperty("部门CODE")
    private String departmentCode;

    @NotNull
    @ApiModelProperty("部门名称")
    private String departmentName;

    @ApiModelProperty("是否自营（自营1，非自营2）")
    private String channelType;

    @Range(min = 1, max = 2, message = "secretType只能传1(共享)、2(私有)")
    @ApiModelProperty("保密类型secretType只能传1(共享)、2(私有)")
    private Integer secretType;

    @ApiModelProperty("是否内结（1：是，2：否）")
    private String settlementType;

    @ApiModelProperty("负责人id")
    private Long userid;

    @TableField(exist = false)
    @ApiModelProperty("负责人工号")
    private String userCode;

    @ApiModelProperty("负责人")
    private String username;

    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "创建时间", hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty("数据来源")
    private String dataSource;

    @TableField(exist = false)
    @ApiModelProperty("是否自营（自营1，非自营2）")
    private String channelTypeStr;
    @TableField(exist = false)
    @ApiModelProperty("是否内结（1：是，2：否）")
    private String settlementTypeStr;
    @TableField(exist = false)
    @ApiModelProperty("保密类型secretType只能传1(共享)、2(私有)")
    private String secretTypeStr;
    @TableField(exist = false)
    @ApiModelProperty("子渠道个数")
    private Long subChannelNum;
    @TableField(exist = false)
    @ApiModelProperty("推广位个数")
    private Long promotionPositionNum;
    @TableField(exist = false)
    @ApiModelProperty("CCID个数")
    private Long ccIdNum;
    @TableField(exist = false)
    @ApiModelProperty("负责人名称")
    private String usernameName;
    @TableField(exist = false)
    @ApiModelProperty("数量")
    private Long number;
    @TableField(exist = false)
    @ApiModelProperty(value = "更新时间开始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTimeStart;
    @TableField(exist = false)
    @ApiModelProperty(value = "更新时间结束")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTimeEnd;
    @TableField(exist = false)
    @ApiModelProperty(value = "产品编码")
    private String productCode;
    @TableField(exist = false)
    @ApiModelProperty("公司类型")
    private String companyType;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        this.usernameName = StringUtils.isNotBlank(this.username) && this.username.contains("(") ? this.username.substring(0, this.username.indexOf("(")) : this.username;
    }
}
