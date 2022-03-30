package com.stnts.bi.datamanagement.module.channel.param;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.stnts.bi.entity.common.BasePageParam;
import com.stnts.bi.entity.sys.UserDmEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author liang.zhang
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "渠道分页参数")
public class ChannelPageParam extends BasePageParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("公司名称ID")
    private Long companyId;

    @ApiModelProperty("部门CODE")
    private String departmentCode;

    @ApiModelProperty("渠道名称")
    private String channelName;

    @ApiModelProperty("渠道ID")
    private Long channelId;

    @ApiModelProperty("渠道ID集合")
    private List<Long> channelIdList;

    @ApiModelProperty("保密类型secretType只能传1(共享)、2(私有)")
    private Integer secretType;

    @ApiModelProperty("为了前端展示需要,当传ccid时把对应渠道信息加到列表中")
    private String ccid;

    @ApiModelProperty("部门CODE集合")
    private List<String> departmentCodeAllList;
    @ApiModelProperty("权限勾选集合")
    private Map<Integer, List<UserDmEntity>> mapAll;
}
