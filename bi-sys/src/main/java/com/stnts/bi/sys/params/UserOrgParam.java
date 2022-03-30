package com.stnts.bi.sys.params;

import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.databind.ser.Serializers;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/6/1
 */
@Getter
@Setter
@ApiModel("用户组织参数类")
public class UserOrgParam extends BaseParam {

    @ApiModelProperty("用户ID")
    private Integer userId;
    @ApiModelProperty("组织ID集合")
    private List<Integer> orgIds;

    @Override
    public boolean valid() {
        return null != userId ;
    }
}
