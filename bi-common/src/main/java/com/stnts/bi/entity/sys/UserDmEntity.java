package com.stnts.bi.entity.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/6/2
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("stbi_user_dm")
@ApiModel("用户关联数据管理类")
public class UserDmEntity {

    @ApiModelProperty("用户ID")
    private Integer userId;
    @ApiModelProperty("数据管理ID(当dmType=1时值为组织ID, 当dmType=2时值为CCID, 当dmType=3时值为用户ID)")
    private String dmId;
    @ApiModelProperty("数据管理类型, 1为部门组织, 2为CCID, 3为PID负责人")
    private Integer dmType;
    @ApiModelProperty("数据管理父节点ID")
    private String dmPid;
}
