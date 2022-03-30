package com.stnts.bi.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/1/6
 */
@ApiModel("OLAP菜单顺序类")
@Data
public class OlapPermSubVO {

    @ApiModelProperty("权限ID")
    private String permId;
    @ApiModelProperty("BI中对应权限ID")
    private Integer biPermId;
    @ApiModelProperty("权限名称")
    private String permName;
    @ApiModelProperty("是否启用：1启用 0停用")
    private Integer status;
    @ApiModelProperty("权限顺序")
    private Integer orderNum;
    @ApiModelProperty("产品线ID,只有SDK看板才有此参数")
    private Integer productId;
    @ApiModelProperty("父类权限ID")
    private String parentPermId;
}
