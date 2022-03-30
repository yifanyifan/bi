package com.stnts.bi.datamanagement.module.channel.param;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.stnts.bi.entity.common.BasePageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <pre>
 * 应用表 分页参数对象
 * </pre>
 *
 * @author 易樊
 * @date 2022-01-13
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "应用表分页参数")
public class ChannelApplicationPageParam extends BasePageParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "部门Code")
    private String departmentCode;

    @ApiModelProperty("应用名称")
    private String applicationName;

    @ApiModelProperty("更新日期开始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTimeStart;

    @ApiModelProperty("更新日期结束")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTimeEnd;
}
