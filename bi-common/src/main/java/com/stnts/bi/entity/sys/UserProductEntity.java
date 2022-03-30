package com.stnts.bi.entity.sys;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/6/2
 */
@Data
@TableName("stbi_user_product")
@ApiModel("用户关联产品线类")
@RequiredArgsConstructor()
public class UserProductEntity {

    @NonNull
    @ApiModelProperty("用户ID")
    private Integer userId;
    @NonNull
    @ApiModelProperty("产品线ID")
    private String productId;

    @TableField(exist = false)
    @ApiModelProperty("产品线名称")
    private String productName;
}
