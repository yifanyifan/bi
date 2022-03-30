package com.stnts.bi.datamanagement.module.channel.param;

import com.stnts.bi.entity.common.BasePageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <pre>
 * 产品历史CP厂商记录 分页参数对象
 * </pre>
 *
 * @author 易樊
 * @date 2021-09-28
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "产品历史CP厂商记录分页参数")
public class ChannelProductVendorHistoryPageParam extends BasePageParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("产品code")
    private String productCode;
}
