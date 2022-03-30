package com.stnts.bi.sql.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.stnts.bi.sql.bo.QueryTableColumnResultBO;
import com.stnts.bi.sql.entity.OlapChartDimension;
import com.stnts.bi.sql.entity.OlapChartMeasure;
import com.stnts.bi.sql.util.JacksonDeserializer;
import lombok.Data;

import java.util.List;

/**
 * @author liutianyuan
 */
@Data
public class QueryChartParameterVO {
    private String id;
    private Boolean cache;
    private String chartType;
    private String chartName;
    private Double limit;
    /**
     * 总计，通过去掉所有维度计算
     */
    private Boolean rollup;

    /**
     * 上卷。group by之后加上with rollup
     */
    private Boolean withRollup;
    /**
     * 上卷使用的名称，如总计、全部等等
     */
    private String withRollupName;

    private String databaseName;
    private String tableName;
    private Boolean tableAppendFinal;
    private String viewSql;
    List<Object> viewBindValues;
    private List<QueryTableColumnResultBO> viewColumns;

    private List<OlapChartDimension> dimension;
    private List<OlapChartMeasure> measure;

    private List<ConditionVO> screen;
    private List<ConditionVO> dashboard;
    private String conditionSql;
    private String havingSql;
    private String withSql;
    private List<ConditionVO> compare;
    private List<FilterVO> filter;

    /**
     *  留存图距起始时间间隔，多个用逗号分隔。
     */
    private String retainTimeNum;

    /**
     * 当前周期不完整不能和上一个完整周期对比。
     */
    private Boolean showCurrentGroup;

    /**
     * 是否行格式返回数据。默认列格式返回
     */
    private Boolean rowFormat;

    /**
     * 数据源
     */
    private String dataSource;

    @Data
    public static class ConditionVO {
        private String logic;
        private String func;
        private String name;
        private String olapType;
        /**
         * 前端传递的可能是字符串，可能是数组
         */
        @JsonDeserialize(using = JacksonDeserializer.KeepAsJsonDeserializer.class)
        private String value;
    }

    @Data
    public static class FilterVO {

        /**
         * 条件关系。传值and或者or
         */
        private String relation;

        private List<ConditionVO> member;
    }

}
