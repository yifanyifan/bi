package com.stnts.bi.sys.params;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/6/2
 */
@Data
@ApiModel("用户关联数据管理参数类")
public class UserDmSubParam {

    private String dmId;
    private Integer dmType;
    private String dmPid;
}
