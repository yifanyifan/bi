package com.stnts.bi.sys.vos.olap;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/1/13
 */
@Data
@ApiModel("OLAP权限列表类")
public class OlapPermItemVO {

    @ApiModelProperty("权限ID[二级导航ID]")
    private String permId;
    @ApiModelProperty("模块名称/产品线")
    private String moduleName;
    @ApiModelProperty("目录ID[一级导航ID]")
    private String menuName;
    @ApiModelProperty("二级导航[页面]")
    private String pageName;

    @ApiModelProperty(value="创建时间", example="2020-03-26 05:54:57")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    protected Date createdAt ;

    private List<String> users;
}
