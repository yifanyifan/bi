package com.stnts.bi.entity.gameop;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * 运营PID负责人表
 *
 * @author chenchen
 * @since 2021/11/4.
 */
@Getter
@Setter
@ApiModel("运营PID负责人表")
@TableName("dim_pid_user_op")
public class DimPidUserOp {

    @ApiModelProperty("PID")
    @NotNull(message = "必传参数")
    @TableField(value = "pid")
    private String pid;

    @ApiModelProperty("游戏code")
    @NotNull(message = "必传参数")
    @TableField(value = "game_code")
    private String gameCode;

    @ApiModelProperty("游戏名称")
    @NotNull(message = "必传参数")
    @TableField(value = "game_name")
    private String gameName;

    @ApiModelProperty("渠道ID")
    @NotNull(message = "必传参数")
    @TableField(value = "channel_id")
    private String channelId;

    @ApiModelProperty("渠道名称")
    @NotNull(message = "必传参数")
    @TableField(value = "channel_name")
    private String channelName;

    @ApiModelProperty("用户ID")
    @NotNull(message = "必传参数")
    @TableField(value = "user_id")
    private String userId;

    @ApiModelProperty("用户名称")
    @NotNull(message = "必传参数")
    @TableField(value = "user_name")
    private String userName;

}
