package com.stnts.bi.datamanagement.module.business.entity;

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
 * <p>
 * 业务考核
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("dm_business_check")
@ApiModel(value = "业务考核")
public class BusinessCheck implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "id不能为空", groups = {Update.class})
    @ApiModelProperty("业务考核ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("部门")
    private String department;

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

    @ApiModelProperty("考核指标")
    private String checkTarget;

    @ApiModelProperty(value = "S档")
    private String levelS;

    @ApiModelProperty(value = "A档")
    private String levelA;

    @ApiModelProperty(value = "B档")
    private String levelB;

    @ApiModelProperty(value = "C档")
    private String levelC;

    @ApiModelProperty(value = "D档")
    private String levelD;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "考核期限开始时间")
    private Date checkStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "考核期限结束时间")
    private Date checkEndDate;

    @ApiModelProperty("创建人")
    private String createUser;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新人")
    private String updateUser;

    @ApiModelProperty("更新日期")
    private Date updateTime;

    @ApiModelProperty("是否有效。0：否；1：是")
    private Integer isValid;

    @ApiModelProperty("是否删除。0：否；1：是")
    @TableLogic(value = "0", delval = "1")//逻辑删除
    private Integer isDelete;

    @TableField(exist = false)
    @ApiModelProperty("创建人名称")
    private String createUserName;
    @TableField(exist = false)
    @ApiModelProperty("更新人名称")
    private String updateUserName;
    @TableField(exist = false)
    @ApiModelProperty("考核目标集合")
    private List<BusinessCheckLine> businessCheckLineList;

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
        this.createUserName = StringUtils.isNotBlank(this.createUser) && this.createUser.contains("(") ? this.createUser.substring(0, this.createUser.indexOf("(")) : this.createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
        this.updateUserName = StringUtils.isNotBlank(this.updateUser) && this.updateUser.contains("(") ? this.updateUser.substring(0, this.updateUser.indexOf("(")) : this.updateUser;
    }
}
