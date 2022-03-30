package com.stnts.bi.sys.vos.olap;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/1/6
 */
@Data
@ApiModel("OLAP权限树状类")
public class OlapPermVO {

    @ApiModelProperty("BI权限ID")
    private String permId;
    @ApiModelProperty("对应OLAP权限ID")
    private Integer olapPermId;
    @ApiModelProperty("权限名称")
    private String permName;
    @ApiModelProperty("权限类型:1目录(菜单) 2仪表盘(页面)")
    private Integer permType;
    @ApiModelProperty("是否启用,1启用 0停用")
    private Integer status;
    @ApiModelProperty("权限子集")
    private List<OlapPermVO> children;
}
