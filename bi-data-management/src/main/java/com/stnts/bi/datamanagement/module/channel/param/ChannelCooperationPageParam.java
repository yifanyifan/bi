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
 * 渠道合作 分页参数对象
 * </pre>
 *
 * @author 刘天元
 * @date 2021-02-03
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "渠道合作分页参数")
public class ChannelCooperationPageParam extends BasePageParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("CCID")
    private String ccid;

    @ApiModelProperty("部门名称")
    private String departmentName;

    @ApiModelProperty("业务分类")
    private String levelBusiness;

    @ApiModelProperty("业务分类-搜索框")
    private String levelBusinessBox;

    @ApiModelProperty("业务分类ID")
    private Integer businessDictId;

    @ApiModelProperty("一级分类")
    private String firstLevelBusiness;

    @ApiModelProperty("二级分类")
    private String secondLevelBusiness;

    @ApiModelProperty("三级分类")
    private String thirdLevelBusiness;

    @ApiModelProperty("供应商名称")
    private String agentName;

    @ApiModelProperty("渠道名称")
    private String channelName;

    @ApiModelProperty("供应商id")
    private Long agentId;

    @ApiModelProperty("渠道ID")
    private Long channelId;

    @ApiModelProperty("子渠道ID")
    private String subChannelId;

    @ApiModelProperty("计费规则")
    private String chargeRule;

    @ApiModelProperty("部门CODE")
    private String departmentCode;

    @ApiModelProperty("渠道费率")
    private String channelRate;

    @ApiModelProperty("负责人")
    private String userName;

    @ApiModelProperty("负责人名称")
    private String usernameName;

    @ApiModelProperty("点击 关联子渠道、关联产品/应用、关联PID 后 为true")
    private String longTimeOrder;

    @ApiModelProperty("CCID集合")
    private List<String> ccidList;
    @ApiModelProperty("部门CODE集合")
    private List<String> departmentCodeAllList;
    @ApiModelProperty("权限勾选集合")
    private Map<Integer, List<UserDmEntity>> mapAll;

}
