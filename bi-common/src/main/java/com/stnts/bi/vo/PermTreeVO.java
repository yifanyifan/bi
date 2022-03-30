package com.stnts.bi.vo;

import java.util.List;

import com.stnts.bi.entity.sys.ProductEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liang.zhang
 * @date 2020年6月16日
 * @desc TODO
 * 加入SDK板块后的权限树
 */
@Data
@ApiModel("权限树")
public class PermTreeVO {

	@ApiModelProperty(name="产品线列表")
	private List<ProductEntity> products;
	
	@ApiModelProperty(name="权限树")
	private List<SimplePermVO> perms;
}
