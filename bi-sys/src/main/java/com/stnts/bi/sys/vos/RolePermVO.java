package com.stnts.bi.sys.vos;

import java.util.List;
import java.util.Map;

import com.stnts.bi.entity.sys.PermEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liang.zhang
 * @date 2020年4月5日
 * @desc TODO
 */
@Data
@ApiModel("角色权限类")
public class RolePermVO {
	
	@ApiModelProperty("角色ID")
	private Integer roleId;
	@ApiModelProperty("权限MAP：key=菜单ID,value=权限集合")
	private Map<PermEntity, List<PermVO>> perms;
}
