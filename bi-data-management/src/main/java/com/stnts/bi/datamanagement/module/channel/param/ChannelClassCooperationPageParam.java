package com.stnts.bi.datamanagement.module.channel.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.stnts.bi.entity.common.BasePageParam;

/**
 * <pre>
 * 渠道类型关联CCID 分页参数对象
 * </pre>
 *
 * @author 易樊
 * @date 2021-09-22
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "渠道类型关联CCID分页参数")
public class ChannelClassCooperationPageParam extends BasePageParam {
    private static final long serialVersionUID = 1L;
}
