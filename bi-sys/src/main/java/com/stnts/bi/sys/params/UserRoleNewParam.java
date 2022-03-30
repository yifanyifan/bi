package com.stnts.bi.sys.params;

import cn.hutool.core.collection.CollectionUtil;
import com.stnts.bi.entity.sys.UserDmEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/6/2
 */
@Getter
@Setter
@ApiModel("用户权限参数类")
public class UserRoleNewParam extends BaseParam{

    @ApiModelProperty("用户ID")
    private Integer userId;
    @ApiModelProperty("角色ID集合")
    private List<Integer> roleIds;
    @ApiModelProperty("产品线ID集合")
    private List<String> productIds;
    @ApiModelProperty("数据管理权限集合")
    private List<UserDmSubParam> dms;

    @Override
    public boolean valid() {
        return null != userId;
    }
}
