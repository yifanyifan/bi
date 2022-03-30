package com.stnts.bi.entity.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/5/31
 */
@Data
@ApiModel("用户组织关联类")
@TableName("stbi_user_org")
public class UserOrgEntity {

    private Integer userId;
    private Integer orgId;
}
