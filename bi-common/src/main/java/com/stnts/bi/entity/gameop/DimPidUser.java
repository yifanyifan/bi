package com.stnts.bi.entity.gameop;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/8/24
 */
@Getter
@Setter
@ApiModel("PID运营负责人维护表")
@TableName("dim_pid_user")
public class DimPidUser {

    @TableId(type=IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String cnname;
    private String gameNames;
    private String channelNames;
    private String pids;
    private Date updatedTime;
}
