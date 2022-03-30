package com.stnts.bi.datamanagement.module.channel.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/3/29
 */
@Data
@AllArgsConstructor
@ApiModel("部门搜索类")
public class DepartmentVO {

    @ApiModelProperty("部门CODE")
    private String departmentCode;
    @ApiModelProperty("部门名称")
    private String departmentName;
}
