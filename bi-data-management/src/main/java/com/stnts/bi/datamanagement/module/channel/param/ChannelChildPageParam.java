package com.stnts.bi.datamanagement.module.channel.param;

import com.stnts.bi.entity.common.BasePageParam;
import com.stnts.bi.entity.sys.UserDmEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 子渠道 分页参数对象
 * </pre>
 *
 * @author 刘天元
 * @date 2021-02-04
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "子渠道分页参数")
public class ChannelChildPageParam extends BasePageParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("供应商id")
    private Long agentId;

    @ApiModelProperty("渠道ID")
    private Long channelId;

    @ApiModelProperty("渠道ID")
    private String subChannelId;

    @ApiModelProperty("渠道名称")
    private String subChannelName;

    @ApiModelProperty("一级分类")
    private String firstLevelBusiness;

    @ApiModelProperty("二级分类")
    private String secondLevelBusiness;

    @ApiModelProperty("三级分类")
    private String thirdLevelBusiness;

    @ApiModelProperty("CCID")
    private String ccid;

    @ApiModelProperty("部门code")
    private String departmentCode;

    @ApiModelProperty("部门CODE集合")
    private List<String> departmentCodeAllList;
    @ApiModelProperty("权限勾选集合")
    private Map<Integer, List<UserDmEntity>> mapAll;

}
