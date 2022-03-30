package com.stnts.bi.sys.params;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author liang.zhang
 * @date 2020年4月3日
 * @desc TODO
 */
@Getter
@Setter
@ApiModel
public class RolePermParam extends BaseParam {

	@ApiModelProperty(name = "menuId", value = "一级菜单ID")
	private Integer menuId;

	@ApiModelProperty(name = "permCode", value = "菜单对应的权限CODE")
	private String permCode;
	
	@ApiModelProperty(name = "perms", value = "权限ID列表")
	private List<Integer> perms;

	@Override
	public boolean valid() {
		return null != menuId && null != perms && !perms.isEmpty();
	}
}
