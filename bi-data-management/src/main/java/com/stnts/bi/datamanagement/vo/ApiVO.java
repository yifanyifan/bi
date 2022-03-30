package com.stnts.bi.datamanagement.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/5/12
 */
@Data
@Accessors(chain = true)
@ApiModel("游戏运营接口数据模型-完整版")
public class ApiVO implements Serializable {

    /** 渠道 */
    @ApiModelProperty("渠道ID,允许为空")
    private Long channelId;
    @NotNull(message = "渠道名称不允许为空")
    @ApiModelProperty(value = "渠道名称")
    private String channelName;
    @Range(message = "保密类型只能传参1、2", min = 1, max = 2)
    @ApiModelProperty("渠道保密类型：1共享,2私有(默认)")
    private Integer secretType = 2;
    @ApiModelProperty(hidden = true)
    private Long companyId;
    @NotNull(message = "公司名称不允许为空")
    @ApiModelProperty("公司名称")
    private String companyName;
    @NotNull(message = "部门CODE不允许为空")
    @ApiModelProperty("部门CODE")
    private String departmentCode;
    @NotNull(message = "部门名称不允许为空")
    @ApiModelProperty("部门名称")
    private String departmentName;
    @ApiModelProperty("是否内结（1：是，2：否）")
    private String settlementType;
    @NotNull(message = "用户ID不允许为空")
    @ApiModelProperty("用户ID,渠道信息中表示创建人,CCID和PID中表示负责人")
    private Long userid;
    @NotNull(message = "用户名不允许为空")
    @ApiModelProperty("用户名")
    private String username;

    /** CCID */
    @ApiModelProperty(value = "CCID,由后台生成")
    private String ccid;
    @ApiModelProperty("业务分类ID")
    private Integer businessDictId;
    @NotNull(message = "一级分类不允许为空")
    @ApiModelProperty("一级分类")
    private String firstLevelBusiness;
    @NotNull(message = "二级分类不允许为空")
    @ApiModelProperty("二级分类")
    private String secondLevelBusiness;
    @ApiModelProperty("三级分类,默认-")
    private String thirdLevelBusiness = "-";
    @NotNull(message = "计费规则不允许为空")
    @ApiModelProperty("计费规则")
    private String chargeRule;
    @NotNull(message = "渠道费率不允许为空")
    @ApiModelProperty("渠道费率")
    private BigDecimal channelRate = BigDecimal.ZERO;
    @ApiModelProperty(value = "渠道分成, 当没值时给null", required = true)
    private BigDecimal channelShare;
    @ApiModelProperty(value = "渠道阶梯分成, 当没值时给null", required = true, example = "[{\"share\":2,\"num\":[0,1000]},{\"share\":3,\"num\":[1001,2000]},{\"share\":4,\"num\":[2001,3000]},{\"share\":5,\"num\":[3001,4000]},{\"share\":6,\"num\":[4001,5000]}]")
    private String channelShareStep;
    @ApiModelProperty(value = "单价, 当没值时给null", required = true)
    private BigDecimal price;

    /** PID */
    @ApiModelProperty("PID别名")
    private String pidAlias;
    @ApiModelProperty("媒介ID, 多个媒介以,分割")
    private String mediumIds;
    @ApiModelProperty("媒介名称, 多个媒介以,分割")
    private String mediumNames;
    @ApiModelProperty("产品ID")
    private Long productId;
    @ApiModelProperty("产品Code")
    private String productCode;
    @ApiModelProperty("应用ID")
    private Long applicationId;
    /** 开放平台： 当传子渠道ID和CCID时  表示只创建PID */
    @ApiModelProperty("子渠道ID")
    private String subChannelId;
    @ApiModelProperty("子渠道名称")
    private String subChannelName;
    @Range(message = "PID生成最少1个, 最多1000", min = 1, max = 1000)
    @ApiModelProperty("生成pid个数")
    private Integer pidNum = 1;
    @ApiModelProperty(value = "拓展字段, 多个值中间以,分割", example = "网吧,其它标签")
    private String extra;
    @ApiModelProperty("推广位ID")
    private Long ppId;

    @ApiModelProperty(value = "PID列表", hidden = true)
    private List<String> pidList;
    @ApiModelProperty("数据来源")
    private String dataSource;
}
