package com.stnts.bi.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author liang.zhang
 * @date 2020年3月26日
 * @desc TODO
 */
@Data
@Builder
@TableName("stbi_olap_log_op")
@ApiModel("日志类")
public class OlapLogOpEntity implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2891179931647614150L;

	@ApiModelProperty(value="日志ID")
	@TableId(type=IdType.AUTO)
	@TableField("log_id")
	private Integer logId;
	@ApiModelProperty(value="请求URL")
	private String reqUrl;
	@ApiModelProperty(value="操作说明")
	private String logType;
	@ApiModelProperty("请求参数")
	private String logBody;
	@ApiModelProperty(value="请求IP")
	private String logIp;
	@ApiModelProperty(value="操作人IP")
	private Integer createdBy;
	@ApiModelProperty(value="创建时间", example="2020-03-26 13:54:57")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	private Date createdAt;
	@ApiModelProperty("操作人名称")
	@TableField(exist=false)
	private String cnname;
}
