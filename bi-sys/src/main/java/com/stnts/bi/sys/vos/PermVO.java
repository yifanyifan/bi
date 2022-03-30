package com.stnts.bi.sys.vos;

import java.util.List;

import com.stnts.bi.entity.sys.PermEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("展示权限类")
public class PermVO {

	@ApiModelProperty("菜单ID")
	private Integer topId;
	@ApiModelProperty("菜单名称")
	private String topName;
	@ApiModelProperty("一级页面ID")
	private Integer fstMenuId;
	@ApiModelProperty("一级页面名称")
	private String fstMenuName;
	@ApiModelProperty("二级页面ID")
	private Integer secMenuId;
	@ApiModelProperty("二级页面名称")
	private String secMenuName;
	@ApiModelProperty("三级页面ID")
	private Integer thdMenuId;
	@ApiModelProperty("三级页面名称")
	private String thdMenuName;
	
	@ApiModelProperty("菜单级根目录")
	private Integer leafId;
	
	@ApiModelProperty("操作权限集合")
	private List<PermEntity> perms;
}
