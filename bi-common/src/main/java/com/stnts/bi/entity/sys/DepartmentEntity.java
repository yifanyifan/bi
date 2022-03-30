package com.stnts.bi.entity.sys;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import com.stnts.bi.groups.InsertGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ApiModel("部门信息类")
@TableName("stbi_department")
public class DepartmentEntity extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3570380714761261778L;

	@ApiModelProperty(value = "部门ID")
	private Integer id;
	@ApiModelProperty("部门名称")
	private String name;
	@ApiModelProperty(value = "父ID", hidden = true)
	private Integer pid;
	@ApiModelProperty("部门CODE")
	private String code;
	@ApiModelProperty(value = "父CODE", hidden = true)
	private String pcode;
	@ApiModelProperty("组织ID")
	@TableField("org_id")
	private Integer orgId;
	@ApiModelProperty("组织名称")
	@TableField(exist = false)
	private String orgName;
}
