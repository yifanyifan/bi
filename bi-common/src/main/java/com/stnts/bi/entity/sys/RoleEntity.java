package com.stnts.bi.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author liang.zhang
 * @date 2020年3月29日
 * @desc TODO
 */
@Getter
@Setter
@RequiredArgsConstructor
@TableName("stbi_role")
@ApiModel("角色类")
@JsonIgnoreProperties(value = { "handler"})
public class RoleEntity extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2367996226846659755L;

	@ApiModelProperty("角色ID")
	@TableId(value = "role_id", type = IdType.AUTO)
	private Integer id;
	@ApiModelProperty("角色名称")
	@NonNull
	@TableField(value = "role_name")
	private String name;
	@ApiModelProperty("角色描述")
	@NonNull
	private String roleDesc;
	@ApiModelProperty("角色状态1:有效")
	private Integer status = 1;

	@TableField(exist = false)
	@ApiModelProperty("覆盖用户数")
	private Integer coverUserNum;
}
