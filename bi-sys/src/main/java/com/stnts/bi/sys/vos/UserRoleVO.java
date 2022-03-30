package com.stnts.bi.sys.vos;

import com.stnts.bi.entity.sys.UserDmEntity;
import com.stnts.bi.entity.sys.UserProductEntity;
import com.stnts.bi.entity.sys.UserRoleEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/6/2
 */
@Data
@ApiModel("用户权限类")
public class UserRoleVO {

    @ApiModelProperty("用户名")
    private String cnname;
    @ApiModelProperty("部门名称")
    private String departmentName;
    @ApiModelProperty("组织名称, 多个以,分割")
    private String orgNames;
    @ApiModelProperty("角色ID集合")
    private List<Integer> roleIds;

    @ApiModelProperty("SDK")
    private List<TreeVO> products;

    @ApiModelProperty("数据管理")
    private List<TreeVO> dms;

    //dm 用一个map

}
