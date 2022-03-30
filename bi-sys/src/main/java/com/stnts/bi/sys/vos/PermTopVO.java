package com.stnts.bi.sys.vos;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liang.zhang
 * @date 2020年4月10日
 * @desc TODO
 */
@Data
@ApiModel("权限顶层菜单类")
public class PermTopVO {

	@ApiModelProperty("菜单ID")
	private Integer id;
	@ApiModelProperty("菜单名称")
	private String name;
	@ApiModelProperty("权限CODE[对应编辑权限时的permCode]")
	private String permCode;
	@ApiModelProperty("权限集合")
	private List<PermVO> perms;
}
