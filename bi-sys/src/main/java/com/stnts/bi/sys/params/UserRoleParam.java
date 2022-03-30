package com.stnts.bi.sys.params;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("用户角色参数")
public class UserRoleParam extends BaseParam{

	@ApiModelProperty(value="角色ID")
	private Integer roleId;
	@ApiModelProperty(value="产品线ID[多个以,分割]")
	private String productIds;
	@ApiModelProperty(value="产品线名称[多个以,分割]")
	private String productNames;
	
}
