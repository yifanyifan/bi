package com.stnts.bi.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apiguardian.api.API;

/**
 * @author liang.zhang
 * @date 2020年4月8日
 * @desc TODO
 */
@ApiModel("产品线类")
@Data
@TableName("stbi_product")
@JsonIgnoreProperties(value = { "handler"})
public class ProductEntity {

	@ApiModelProperty("产品线ID")
	@TableId(type=IdType.INPUT)
	private String productId;
	@ApiModelProperty("产品线名称")
	private String productName;
	@ApiModelProperty("产品线状态[1:启用,0:停用]")
	private Integer status;

	@ApiModelProperty("编码")
	private String business;
	@ApiModelProperty("业务线")
	private String classification;
	@ApiModelProperty("SDK产品线编号")
	private Integer sdkproduct;
	@ApiModelProperty("SDK产品线名称")
	private String sdkproductDisplay;

	@TableField(exist = false)
	@ApiModelProperty("覆盖用户数")
	private Integer coverUserNum;

	@ApiModelProperty("数据层级ID")
	private Integer levelId;

	@TableField(exist = false)
	@ApiModelProperty("数据层级")
	private String dataLevel;
}
