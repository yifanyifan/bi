package com.stnts.bi.sys.params;

import org.apache.commons.lang3.StringUtils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("角色参数")
public class RoleParam implements Param{
	
	@ApiModelProperty(value="角色名称")
	private String name;
	@ApiModelProperty(value="角色描述")
	private String roleDesc;
	
	@Override
	public boolean valid() {
		return StringUtils.isNotBlank(name);
	}
}
