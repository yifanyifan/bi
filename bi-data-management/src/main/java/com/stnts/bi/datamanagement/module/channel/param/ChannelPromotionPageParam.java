package com.stnts.bi.datamanagement.module.channel.param;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelApplication;
import com.stnts.bi.entity.common.BasePageParam;
import com.stnts.bi.entity.sys.UserDmEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 渠道推广 分页参数对象
 * </pre>
 *
 * @author 刘天元
 * @date 2021-02-04
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "渠道推广分页参数")
public class ChannelPromotionPageParam extends BasePageParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("pid多选")
    private List<String> pidList;

    @ApiModelProperty("供应商id,公司ID")
    private Long agentId;

    @ApiModelProperty("渠道ID")
    private Long channelId;

    @ApiModelProperty("渠道名称")
    private String channelName;

    @ApiModelProperty("子渠道ID")
    private String subChannelId;

    @ApiModelProperty("子渠道名称")
    private String subChannelName;

    @ApiModelProperty("产品自增id")
    private Long productId;

    @ApiModelProperty("产品Code")
    private String productCode;

    @ApiModelProperty("产品部门集合")
    private List<String> productDepartmentCodeList;

    @ApiModelProperty("产品名称")
    private String productName;

    @ApiModelProperty("应用id")
    private String applicationId;

    @ApiModelProperty("应用名称")
    private String applicationName;

    @ApiModelProperty("产品/应用名称【废弃】")
    private String productApplicatioName;

    @ApiModelProperty("产品名称+应用名称")
    private String productNameAndApplicationName;

    @ApiModelProperty("产品名称+应用名称【下拉选，productCode:applicationID 或者 productCode】")
    private String prodApp;

    @ApiModelProperty("推广媒介id")
    private Long mediumId;

    @ApiModelProperty("推广媒介名称")
    private String mediumName;

    @ApiModelProperty("PID")
    private String pid;

    @ApiModelProperty("CCID")
    private String ccid;

    @ApiModelProperty("部门CODE")
    private String departmentCode;

    @ApiModelProperty("部门名称")
    private String departmentName;

    @ApiModelProperty("推广位ID集合【给ZA接口使用】")
    private List<Long> promoteIdList;

    @ApiModelProperty("计费别名")
    private String pidAlias;

    @ApiModelProperty("拓展字段")
    private String extra;

    @ApiModelProperty("负责人名称")
    private String username;

    @ApiModelProperty("目标渠道ID")
    private Long targetChannelId;

    @ApiModelProperty("目标公司ID")
    private Long targetCompanyId;

    @ApiModelProperty("目标CCID")
    private String targetCCId;

    @ApiModelProperty("是否内结（1：是，2：否）")
    private String settlementType;

    @ApiModelProperty(value = "目标数据有效期开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkStartDate;

    @ApiModelProperty(value = "目标数据有效期结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkEndDate;

    @ApiModelProperty(value = "创建时间", hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("负责人名称")
    private String usernameName;

    @ApiModelProperty("部门CODE集合")
    private List<String> departmentCodeAllList;
    @ApiModelProperty("权限勾选集合")
    private Map<Integer, List<UserDmEntity>> mapAll;

    @TableField(exist = false)
    @ApiModelProperty("PID搜索-多个产品应用下拉选")
    private List<ChannelApplication> channelApplicationList;

    @TableField(exist = false)
    @ApiModelProperty("历史CCID")
    private String historyCCID;

    @ApiModelProperty("数据来源")
    private String dataSource;

}
