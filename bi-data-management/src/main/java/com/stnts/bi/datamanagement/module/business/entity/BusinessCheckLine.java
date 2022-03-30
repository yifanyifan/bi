package com.stnts.bi.datamanagement.module.business.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 考核目标
 *
 * @author 易樊
 * @since 2022-01-12
 */
@Data
@ApiModel(value = "BusinessCheckLine对象")
public class BusinessCheckLine implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("考核指标")
    private String checkTarget;

    @ApiModelProperty("S档")
    private String levelS;

    @ApiModelProperty("A档")
    private String levelA;

    @ApiModelProperty("B档")
    private String levelB;

    @ApiModelProperty("C档")
    private String levelC;

    @ApiModelProperty("D档")
    private String levelD;
}
