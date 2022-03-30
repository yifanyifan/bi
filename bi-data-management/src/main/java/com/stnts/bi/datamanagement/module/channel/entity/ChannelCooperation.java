package com.stnts.bi.datamanagement.module.channel.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.stnts.bi.datamanagement.module.exportdata.dataenum.SettlementTypeEnum;
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
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 渠道合作
 *
 * @author 刘天元
 * @since 2021-02-03
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("dm_channel_cooperation")
@ApiModel(value = "ChannelCooperation对象")
public class ChannelCooperation implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "id不能为空", groups = {Update.class})
    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("CCID")
    private String ccid;

    @ApiModelProperty("供应商id")
    private Long agentId;

    @ApiModelProperty("供应商名称")
    private String agentName;

    @ApiModelProperty("渠道ID")
    private Long channelId;

    @ApiModelProperty("渠道名称")
    private String channelName;

    @ApiModelProperty("部门名称")
    private String departmentName;

    @ApiModelProperty("部门code")
    private String departmentCode;

    @ApiModelProperty("业务分类ID")
    private Integer businessDictId;

    @ApiModelProperty("一级分类")
    private String firstLevelBusiness;

    @ApiModelProperty("二级分类")
    private String secondLevelBusiness;

    @ApiModelProperty("三级分类")
    private String thirdLevelBusiness;

    @ApiModelProperty("计费方式")
    private String chargeRule;

    @ApiModelProperty("渠道费率")
    private BigDecimal channelRate;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("渠道分成")
    private BigDecimal channelShare;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("渠道阶梯分成")
    private String channelShareStep;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("单价")
    private BigDecimal price;

    @ApiModelProperty("结算指标【1.收入2.利润3.注册4.激活】")
    private String channelShareType;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("数据来源")
    private String dataSource;

    @ApiModelProperty("负责人id")
    private Long userid;

    @ApiModelProperty("负责人名称")
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

    @ApiModelProperty("关联子渠道个数")
    @TableField(exist = false)
    private Long subChannelNum;
    @ApiModelProperty("关联产品、应用个数")
    @TableField(exist = false)
    private Long appNum;
    @ApiModelProperty("关联PID个数")
    @TableField(exist = false)
    private Long pidNum;
    @ApiModelProperty("迁移PID个数")
    @TableField(exist = false)
    private Long pidHistoryNum;
    @TableField(exist = false)
    @ApiModelProperty("业务分类")
    private String levelBusiness;
    @TableField(exist = false)
    @ApiModelProperty("负责人名称")
    private String usernameName;
    @TableField(exist = false)
    @ApiModelProperty("数量")
    private Long number;
    @TableField(exist = false)
    @ApiModelProperty("是否自营（自营1，非自营2）")
    private String channelType;
    @TableField(exist = false)
    @ApiModelProperty("保密类型secretType只能传1(共享)、2(私有)")
    private Integer secretType;
    @TableField(exist = false)
    @ApiModelProperty("保密类型secretType")
    private String secretTypeStr;
    @TableField(exist = false)
    @ApiModelProperty("是否内结（1：是，2：否）")
    private String settlementType;
    @TableField(exist = false)
    @ApiModelProperty("是否内结")
    private String settlementTypeStr;
    @TableField(exist = false)
    @ApiModelProperty("树状图是否选中【1：选中，2：未选中，3：禁用】")
    private String select;
    @TableField(exist = false)
    @ApiModelProperty("分类状态，渠道类型：1.按计费方式(CPS/CPA/CPD等), 2.按渠道, 3.按CCID / 产品分成：1.全部, 2.按类型, 3.按CCID, 4.未知")
    private String modeType;
    @TableField(exist = false)
    @ApiModelProperty("渠道类型")
    private String channelClassStr;
    @TableField(exist = false)
    @ApiModelProperty("产品编码")
    private String productCode;
    @TableField(exist = false)
    @ApiModelProperty("渠道部门名称")
    private String channelDepartmentName;
    @TableField(exist = false)
    @ApiModelProperty("渠道部门code")
    private String channelDepartmentCode;
    @TableField(exist = false)
    @ApiModelProperty("错误提示信息")
    private List<String> returnErrorMsgList;

    public String getLevelBusiness() {
        try {
            return this.firstLevelBusiness.concat("/").concat(this.secondLevelBusiness).concat("/").concat(this.thirdLevelBusiness);
        } catch (Exception e) {
            return null;
        }
    }

    public String getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(String settlementType) {
        this.settlementType = settlementType;
        if (StringUtils.isNotBlank(settlementType)) {
            this.settlementTypeStr = SettlementTypeEnum.getByKey(Integer.valueOf(settlementType)).getValue();
        }
    }

    public void setUsername(String username) {
        this.username = username;
        this.usernameName = StringUtils.isNotBlank(this.username) && this.username.contains("(") ? this.username.substring(0, this.username.indexOf("(")) : this.username;
    }

    public String getUsername() {
        return username;
    }

}
