package com.stnts.bi.sys.params;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;

@Data
@ApiModel("指标新增Param")
@ToString
public class KpiDescParam {

	@ApiModelProperty("指标KEY")
	@TableField("kpi_key")
	private String kpiKey;

	@ApiModelProperty("指标名称")
	@TableField("kpi_name")
	private String kpiName;

	@ApiModelProperty("指标描述")
	@TableField("kpi_desc")
	private String kpiDesc;

	@ApiModelProperty("指标备注")
	@TableField("kpi_comment")
	private String kpiComment;

	@ApiModelProperty("创建时间")
	@TableField("created_by")
	private Integer createdBy;
}
