package com.stnts.bi.datamanagement.module.channel.param;

import com.stnts.bi.entity.sys.UserDmEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.stnts.bi.entity.common.BasePageParam;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 媒介信息 分页参数对象
 * </pre>
 *
 * @author 刘天元
 * @date 2021-02-04
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "媒介信息分页参数")
public class ChannelMediumPageParam extends BasePageParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("PID")
    private String pid;

    @ApiModelProperty("部门CODE")
    private String departmentCode;

    @ApiModelProperty("媒介ID集合, ,号分隔")
    private String mediumIdLists;

    @ApiModelProperty("部门CODE集合")
    private List<String> departmentCodeAllList;
    @ApiModelProperty("权限勾选集合")
    private Map<Integer, List<UserDmEntity>> mapAll;
}
