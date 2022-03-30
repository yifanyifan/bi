package com.stnts.bi.entity.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/1/12
 */
@Data
@Builder
@TableName("stbi_user_perm_olap")
@ApiModel("OLAP用户权限类")
public class OlapUserPermEntity {

    @ApiModelProperty("用户ID")
    private Integer userId;
    @ApiModelProperty("权限ID")
    private String permId;
}
