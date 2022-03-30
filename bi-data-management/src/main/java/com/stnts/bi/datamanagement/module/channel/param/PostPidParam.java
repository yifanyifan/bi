package com.stnts.bi.datamanagement.module.channel.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@ApiModel(value = "计费名提交youtop参数")
public class PostPidParam implements Serializable {
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("PID")
    private String pid;

    @ApiModelProperty("PID别名")
    private String pidAlias;

    @ApiModelProperty("产品Id")
    private String productId;
}
