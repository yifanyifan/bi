package com.stnts.bi.datamanagement.module.channel.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/3/30
 */
@Data
@AllArgsConstructor
@ApiModel("代理商搜索类")
public class AgentVO {

    @ApiModelProperty("供应商id")
    private Long agentId;

    @ApiModelProperty("供应商名称")
    private String agentName;
}
