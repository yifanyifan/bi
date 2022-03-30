package com.stnts.bi.sql.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author liutianyuan
 * @since 2019-03-29
 */
@Data
@Accessors(chain = true)
public class OlapChartMeasure implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String type;

    private String name;

    private String olapType;

    private String aliasName;

    private String unit;

    private String desc;

    private String color;

    private Integer order;

    private String func;

    /**
     * 同比环比
     */
    private String contrast;

    /**
     * 总计占比
     */
    private Boolean proportion;

    /**
     * 最大值占比
     */
    private Boolean percentOfMax;

    private Long minvalue;

    private Long maxvalue;

    private Long minValueNotEqual;

    private Long maxValueNotEqual;

    /**
     * 小数位数
     */
    private Integer decimal;

    /**
     * 数值显示
     */
    private String digitDisplay;

    /**
     * 千位分隔符
     */
    private Boolean separator;

    private String summary;

    /**
     * 拼接sql使用的asName
     */
    private String asName;

    /**
     * 程序中虚拟的度量类型。0：不是虚拟，1：同比环比虚拟度量，2：行专列虚拟度量
     */
    private Integer virtual;

    /**
     * 前端是否显示。
     */
    private Boolean show;
}
