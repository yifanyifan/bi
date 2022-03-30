package com.stnts.bi.datamanagement.module.channel.param;

import com.fasterxml.jackson.annotation.JsonFormat;
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
 * 产品信息 分页参数对象
 * </pre>
 *
 * @author 刘天元
 * @date 2021-02-04
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "产品信息分页参数")
public class ChannelProductPageParam extends BasePageParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("ccid")
    private String ccid;

    @ApiModelProperty("产品CODE")
    private String productCode;

    @ApiModelProperty(value = "CP厂商ID")
    private Long vendorId;

    @ApiModelProperty(value = "CP厂商名称")
    private String vendorName;

    @ApiModelProperty(value = "数据有效期开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date vendorCheckStartDate;

    @ApiModelProperty(value = "数据有效期结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date vendorCheckEndDate;

    @ApiModelProperty("公司主体ID")
    private String cooperationMainId;

    @ApiModelProperty("公司主体名称")
    private String cooperationMainName;

    @ApiModelProperty("部门CODE")
    private String departmentCode;

    @ApiModelProperty("部门NAME")
    private String departmentName;

    @ApiModelProperty("部门CODE集合")
    private List<String> departmentCodeAllList;
    @ApiModelProperty("权限勾选集合")
    private Map<Integer, List<UserDmEntity>> mapAll;
}
