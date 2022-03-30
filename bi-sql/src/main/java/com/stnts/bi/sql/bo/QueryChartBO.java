package com.stnts.bi.sql.bo;

import com.stnts.bi.sql.entity.*;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * 查询图表业务类
 * @author liutianyuan
 */
@Data
public class QueryChartBO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 图表信息
     */
    private OlapChart olapChart;

    /**
     * 当前周期不完整不能和上一个完整周期对比。
     */
    private Boolean showCurrentGroup;

    /**
     * 是否行格式返回数据。默认列格式返回
     */
    private Boolean rowFormat;

    /**
     * 查询图表前端传递参数对象
     */
    private QueryChartParameterVO queryChartParameterVO;

    /**
     * 查询图表结果对象
     */
    private QueryChartResultVO queryChartResultVO;

    /**
     * 维度列表
     */
    private List<OlapChartDimension> dimensionList;

    /**
     * 度量列表
     */
    private List<OlapChartMeasure> measureList;

    /**
     * 表结果信息
     */
    private Map<String, QueryTableColumnResultBO> tableColumnMap;

    /**
     * 数据表信息
     */
    private OlapDsTable olapDsTable;

    /**
     * 数据库信息
     */
    private OlapDsDatabase dsDatabase;

    /**
     * 指定的条件sql
     */
    private String conditionSql;

    /**
     * having语句
     */
    private String havingSql;

    /**
     * with语句
     */
    private String withSql;

    /**
     * 拼接sql业务类
     */
    BuildSqlBO buildSqlBO;
}
