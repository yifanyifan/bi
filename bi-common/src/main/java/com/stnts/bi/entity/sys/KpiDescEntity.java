package com.stnts.bi.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author liang.zhang
 * @date 2020年3月25日
 * @desc TODO
 */
@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
@TableName("stbi_kpi_desc")
@ApiModel("指标类")
public class KpiDescEntity extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3070990615042179386L;
	
	@ApiModelProperty(value="指标ID")
	@TableId(type=IdType.AUTO)
	@TableField("kpi_id")
	private Integer kpiId;
	@ApiModelProperty(value="指标KEY")
	@TableField("kpi_key")
	private String kpiKey;
	@ApiModelProperty(value="指标名称")
	@TableField("kpi_name")
	private String kpiName;
	@ApiModelProperty(value="指标描述")
	@TableField("kpi_desc")
	private String kpiDesc;
	@ApiModelProperty(value="指标备注")
	@TableField("kpi_comment")
	private String kpiComment;
	@ApiModelProperty(value="指标创建人ID")
	@TableField("created_by")
	private Integer createdBy;
	@ApiModelProperty("创建人名称")
	@TableField(exist=false)
	private String cnname;

}
