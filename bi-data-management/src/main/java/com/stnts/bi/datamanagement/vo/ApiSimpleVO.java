package com.stnts.bi.datamanagement.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/5/13
 */
@Data
@ApiModel("游戏运营接口数据模型-简单版")
public class ApiSimpleVO {

    @NotNull(message = "CCID不允许为空")
    @ApiModelProperty("CCID")
    private String ccid;
    @ApiModelProperty("子渠道ID")
    private String subChannelId;
    @ApiModelProperty("子渠道名称")
    private String subChannelName;
    @NotNull(message = "计费名别名不允许为空")
    @ApiModelProperty("计费名别名")
    private String pidAlias;

    @ApiModelProperty("数据来源")
    private String dataSource;
}
