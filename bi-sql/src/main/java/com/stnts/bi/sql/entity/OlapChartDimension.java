package com.stnts.bi.sql.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author liutianyuan
 * @since 2019-03-29
 */
@Data
@Accessors(chain = true)
public class OlapChartDimension implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String name;

    private String olapType;

    /**
     * 日期类型字段的分组粒度，年、月、周、日等
     */
    private String group;

    /**
     * 维度自定义函数
     */
    private String func;

    private String aliasName;

    /**
     * -1:倒序，0:正序，大于1:自定义排序，-2：不排序
     */
    private Integer order;

    /**
     * 是否行转列
     */
    private Boolean isColumn;

    /**
     * 格式化
     */
    private String format;

    /**
     * 指定的自定义排序
     */
    private List<String> orderContentList;

    /**
     * 图例自定义排序
     */
    private List<String> groupDataOrderContentList;

    private String havingLogic;

    private Object havingValue;

    /**
     * 拼接sql使用的asName
     */
    private String asName;

    /**
     * 虚拟维度类型。0：不是虚拟，1：数据周期虚拟维度。
     */
    private Integer virtual;
}
