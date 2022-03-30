package com.stnts.bi.sys.vos.olap;

import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@ApiModel("OLAP打通权限编辑类")
public class OlapPermModVO {

    @ApiModelProperty("权限ID集合")
    private List<String> perms;
    @ApiModelProperty("用户ID集合")
    private List<Integer> users;

    @JsonIgnore
    public boolean isReady(){
        return CollectionUtil.isNotEmpty(perms) && CollectionUtil.isNotEmpty(users);
    };
}
