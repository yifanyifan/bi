package com.stnts.bi.entity.sys;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liang.zhang
 * @date 2020年4月5日
 * @desc TODO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("stbi_role_perm")
@ApiModel("角色权限类")
public class RolePermEntity {
	
	@ApiModelProperty("角色ID")
	@TableField(value="role_id")
	private Integer roleId;
	@ApiModelProperty("权限ID")
	@TableField(value="perm_id")
	private Integer permId;
}
