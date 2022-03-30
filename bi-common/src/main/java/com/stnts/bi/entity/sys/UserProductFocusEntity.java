package com.stnts.bi.entity.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/11/30
 */
@Data
@TableName("stbi_user_product_focus")
@ApiModel("用户关注产品线")
public class UserProductFocusEntity {

    @ApiModelProperty("用户ID")
    private Integer userId;
    @ApiModelProperty("关注产品线ID")
    private String productId;
}
