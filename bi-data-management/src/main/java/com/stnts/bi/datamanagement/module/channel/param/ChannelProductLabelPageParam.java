package com.stnts.bi.datamanagement.module.channel.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.stnts.bi.entity.common.BasePageParam;

/**
 * <pre>
 * 产品标签 分页参数对象
 * </pre>
 *
 * @author 易樊
 * @date 2022-01-26
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "产品标签分页参数")
public class ChannelProductLabelPageParam extends BasePageParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("标签域")
    private String labelArea;

    @ApiModelProperty("标签层级，1：productClass，2：productTheme")
    private String labelLevel;

    @ApiModelProperty("标签值")
    private String labelValue;
}
