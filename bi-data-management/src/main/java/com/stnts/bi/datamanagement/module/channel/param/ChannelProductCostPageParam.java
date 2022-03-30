package com.stnts.bi.datamanagement.module.channel.param;

import com.stnts.bi.entity.common.BasePageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * <pre>
 * 产品分成 分页参数对象
 * </pre>
 *
 * @author 易樊
 * @date 2021-09-24
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "产品分成分页参数")
public class ChannelProductCostPageParam extends BasePageParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("产品code")
    private String productCode;

    @ApiModelProperty("结算指标 1.收入2.利润3.注册4.激活")
    private String channelShareType;

    @ApiModelProperty("渠道分成")
    private BigDecimal channelShare;

    @ApiModelProperty("渠道阶梯分成")
    private String channelShareStep;

    @ApiModelProperty("渠道分类Tab页ID")
    private String costId;

    @ApiModelProperty("渠道分类Tab页需排除ID - 设置分成")
    private String costIdNo;

    @ApiModelProperty("只查未知 - 设置分成")
    private String OnlyNo;

    @ApiModelProperty("全部")
    private List<String> selectByAll;
    @ApiModelProperty("类型")
    private List<String> selectByChannelClass;
    @ApiModelProperty("CCID")
    private List<String> selectByCCID;
}
