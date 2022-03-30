package com.stnts.bi.datamanagement.module.cooperationmain.param;

import com.stnts.bi.entity.common.BasePageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <pre>
 * 公司主体 分页参数对象
 * </pre>
 *
 * @author 易樊
 * @date 2021-09-17
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "公司主体分页参数")
public class CooperationMainPageParam extends BasePageParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("部门code")
    private String departmentCode;

    @ApiModelProperty("公司主体Id")
    private Long cooperationMainId;

    @ApiModelProperty("公司主体")
    private String cooperationMainName;

    @ApiModelProperty("部门CODE集合")
    private List<String> departmentCodeAllList;
}
