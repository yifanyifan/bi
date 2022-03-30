package com.stnts.bi.entity.gameop;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/8/24
 */
@Getter
@Setter
@ApiModel("PID运营负责人维护表")
@TableName("dim_cost")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DimCost {

    @TableId(type = IdType.AUTO)
    private Integer id;
    @NotNull(message = "必传参数")
    @TableField(value = "cost_date")
    @ApiModelProperty("日期")
    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    private Date costDate;
    @NotNull(message = "必传参数")
    private String pid;
    @NotNull(message = "必传参数")
    @TableField("charge_model")
    private String chargeModel;
    @NotNull(message = "必传参数")
    private String source;
    @TableField("book_cost")
    @NotNull(message = "必传参数")
    private Double bookCost;
    @TableField("real_cost")
    @NotNull(message = "必传参数")
    private Double realCost;
}
