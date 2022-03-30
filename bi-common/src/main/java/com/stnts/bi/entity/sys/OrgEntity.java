package com.stnts.bi.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stnts.bi.groups.InsertGroup;
import com.stnts.bi.groups.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/5/27
 */
@Getter
@Setter
@ApiModel("组织实体类")
@TableName("stbi_org")
public class OrgEntity extends BaseEntity{

    @NotNull(groups = UpdateGroup.class)
    @ApiModelProperty("组织ID")
    @TableId(value = "org_id", type = IdType.AUTO)
    private Integer orgId;
    @NotNull(groups = {InsertGroup.class, UpdateGroup.class}, message = "组织名称不允许为空")
    @ApiModelProperty("组织名称")
    @TableField("org_name")
    private String orgName;
    @ApiModelProperty("创建者ID")
    @TableField("created_by")
    private Integer createdBy;

    @ApiModelProperty("创建人名称")
    @TableField(exist = false)
    private String createdUser;
}
