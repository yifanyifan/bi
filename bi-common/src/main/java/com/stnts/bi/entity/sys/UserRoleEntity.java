package com.stnts.bi.entity.sys;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @author liang.zhang
 * @date 2020年3月29日
 * @desc TODO
 */
@Data
@TableName("stbi_user_role")
@JsonIgnoreProperties(value = {"handler"})
@ApiModel("用户角色类")
public class UserRoleEntity implements Serializable{
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 2110279553807215704L;
	
	@ApiModelProperty("用户ID")
	@TableField("user_id")
	private Integer userId;
	@ApiModelProperty("角色ID")
	@TableField("role_id")
	private Integer roleId;
	@ApiModelProperty("产品线ID[多个以,分隔]")
	@TableField("product_ids")
	private String productIds;
	@ApiModelProperty("产品线名称[多个以,分割][弃用]")
	@TableField("product_names")
	private String productNames; 
	
	@ApiModelProperty("角色名称")
	@TableField(exist=false)
	private String roleName;

	public String getProductNames(){

		if(!CollectionUtils.isEmpty(products)){
			Optional<String> pNames = products.stream().map(ProductEntity::getProductName).reduce((p1, p2) -> p1.concat(",").concat(p2));
			if(pNames.isPresent()){
				return pNames.get();
			}
		}
		return productNames;
	}
	
	@ApiModelProperty("产品线列表")
	@TableField(exist=false)
	private List<ProductEntity> products;

	public UserRoleEntity(Integer userId, Integer roleId, String productIds, String productNames) {
		super();
		this.userId = userId;
		this.roleId = roleId;
		this.productIds = productIds;
		this.productNames = productNames;
	}
}
