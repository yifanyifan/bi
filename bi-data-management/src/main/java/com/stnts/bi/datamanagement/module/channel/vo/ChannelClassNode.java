package com.stnts.bi.datamanagement.module.channel.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 渠道类型
 *
 * @author 易樊
 * @since 2021-09-22
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ChannelClass节点")
public class ChannelClassNode implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("父级ID, -1:部门")
    private String parentId;

    @ApiModelProperty("渠道类型ID")
    private String channelClassId;

    @ApiModelProperty("节点CODE，当节点为部门时使用")
    private String code;

    @ApiModelProperty("节点名称")
    private String name;

    @ApiModelProperty("节点类型，1：目录，2：叶子节点")
    private String nodeType;

    @ApiModelProperty("产品CODE")
    private String productCode;

    @ApiModelProperty("关联CCID数量")
    private Long associatedCCIDNum;

    @ApiModelProperty("子节点集合")
    private List<ChannelClassNode> channelClassNodeList;

    @ApiModelProperty("CCID集合-产品分成使用")
    private List<ChannelClassNode> ccidList;

    /*@ApiModelProperty("CCID集合-产品分成使用")
    private List<ChannelCooperation> channelCooperationList;*/

    /*@ApiModelProperty("树状图是否选中【1：选中，2：未选中，3：禁用】")
    private String select;*/

    @ApiModelProperty("分类状态：【渠道类型：1.按计费方式, 2.按渠道, 3.按CCID】【产品分成：1.全部, 2.按类型, 3.按CCID, 4.未知】(全部mode_id=-1)")
    private String modeType;

    @ApiModelProperty("渠道名称")
    private String channelName;

    @ApiModelProperty("渠道类型")
    private String channelClassStr;
}
