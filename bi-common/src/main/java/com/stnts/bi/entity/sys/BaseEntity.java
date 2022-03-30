package com.stnts.bi.entity.sys;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;


/**
 * @author liang.zhang
 * @date 2020年3月25日
 * @desc TODO
 * 基类
 */
public class BaseEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5509130183238730832L;
	
	@ApiModelProperty(value="创建时间", example="2020-03-26 05:54:57")
	@TableField("created_at")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	protected Date createdAt = new Date();
	@ApiModelProperty(value="修改时间", example="2020-03-26 05:54:57")
	@TableField("updated_at")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	protected Date updatedAt = new Date();
}
