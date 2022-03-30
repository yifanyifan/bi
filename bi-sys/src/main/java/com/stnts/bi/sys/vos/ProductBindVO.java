package com.stnts.bi.sys.vos;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/5/28
 */
@Data
@ApiModel("产品线绑定类")
public class ProductBindVO {

    @ApiModelProperty("产品线ID")
    private String productId;
    @ApiModelProperty("数据层级ID")
    private Integer levelId;
}
