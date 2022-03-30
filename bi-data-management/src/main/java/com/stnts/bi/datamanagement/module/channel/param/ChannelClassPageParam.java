package com.stnts.bi.datamanagement.module.channel.param;

import com.stnts.bi.datamanagement.module.channel.entity.ChannelClassCooperation;
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
 * 渠道类型 分页参数对象
 * </pre>
 *
 * @author 易樊
 * @date 2021-09-22
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "渠道类型分页参数")
public class ChannelClassPageParam extends BasePageParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("父级ID, -1:部门")
    private Long parentId;

    @ApiModelProperty("节点CODE，部门CODE")
    private String code;

    @ApiModelProperty("节点名称")
    private String name;

    @ApiModelProperty("节点类型，1：目录，2：叶子节点")
    private String nodeType;

    @ApiModelProperty("产品CODE")
    private String productCode;

    @ApiModelProperty("部门CODE")
    private String departmentCode;

    @ApiModelProperty("渠道分类ID-搜索条件")
    private String channelClassId;

    @ApiModelProperty("渠道分类ID-数字版-向下兼容")
    private String channelClassIdNum;

    @ApiModelProperty("按计费方式")
    private List<ChannelClassCooperation> selectByChargeRule;
    @ApiModelProperty("按渠道")
    private List<ChannelClassCooperation> selectByChannel;
    @ApiModelProperty("按子渠道")
    private List<ChannelClassCooperation> selectByCCID;

    @ApiModelProperty("部门CODE集合")
    private List<String> departmentCodeAllList;
    @ApiModelProperty("权限勾选集合")
    private Map<Integer, List<UserDmEntity>> mapAll;
}
