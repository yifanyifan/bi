package com.stnts.bi.sql.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.*;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;
import com.stnts.bi.sql.constant.*;
import com.stnts.bi.sql.entity.OlapChartDimension;
import com.stnts.bi.sql.util.JacksonUtil;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

/**
 * @author 刘天元
 */
@Slf4j
@Service
public class ExportChartService {

    private final QueryChartService queryChartService;

    public ExportChartService(QueryChartService queryChartService) {
        this.queryChartService = queryChartService;
    }

    public void exportChart(QueryChartParameterVO queryChartParameterVO, HttpServletResponse response) throws IOException {
        exportChart(queryChartParameterVO, null, response);
    }

    public void exportChart(QueryChartParameterVO queryChartParameterVO, QueryChartResultVO queryChartResultVO, HttpServletResponse response) throws IOException {
        if(queryChartResultVO == null) {
            queryChartParameterVO.setLimit(100000.0);
            queryChartResultVO = queryChartService.queryChart(queryChartParameterVO);
        }
        ExcelWriter writer = ExcelUtil.getBigWriter();
        if(StrUtil.equalsAny(queryChartParameterVO.getChartType(), ChartTypeConstant.RETAIN_TABLE, ChartTypeConstant.RETAIN_TABLE_WITH_OUT_TOTAL)) {
            char splitChar = '-';
            writeData(queryChartResultVO.getDatas(), writer, 0, "全部留存率", v -> {
                if(StrUtil.isNotEmpty(v) && StrUtil.contains(v, splitChar)) {
                    String str = ArrayUtil.get(StrUtil.splitToArray(v, splitChar), 0);
                    str = NumberUtil.formatPercent(Double.parseDouble(str), 2);
                    return str;
                }
                return v;
            });
            writeData(queryChartResultVO.getDatas(), writer, 1, "全部留存", v -> {
                if(StrUtil.isNotEmpty(v) && StrUtil.contains(v, splitChar)) {
                    String str = ArrayUtil.get(StrUtil.splitToArray(v, splitChar), 1);
                    str = NumberUtil.roundStr(str, 0);
                    return str;
                }
                return v;
            });
        }  else if(StrUtil.equalsAny(queryChartParameterVO.getChartType(), ChartTypeConstant.RETAIN_TABLE_LTV, ChartTypeConstant.RETAIN_TABLE_LTV_WITH_OUT_TOTAL)) {
            char splitChar = '-';
            writeData(queryChartResultVO.getDatas(), writer, 0, "LTV", v -> {
                if(StrUtil.isNotEmpty(v) && StrUtil.contains(v, splitChar)) {
                    String str = ArrayUtil.get(StrUtil.splitToArray(v, splitChar), 0);
                    str = NumberUtil.roundStr(str, 2);
                    return str;
                }
                return v;
            });
            writeData(queryChartResultVO.getDatas(), writer, 1, "累计付费金额", v -> {
                if(StrUtil.isNotEmpty(v) && StrUtil.contains(v, splitChar)) {
                    String str = ArrayUtil.get(StrUtil.splitToArray(v, splitChar), 1);
                    str = NumberUtil.roundStr(str, 0);
                    return str;
                }
                return v;
            });
            writeData(queryChartResultVO.getDatas(), writer, 2, "用户数", v -> {
                if(StrUtil.isNotEmpty(v) && StrUtil.contains(v, splitChar)) {
                    String str = ArrayUtil.get(StrUtil.splitToArray(v, splitChar), 2);
                    str = NumberUtil.roundStr(str, 0);
                    return str;
                }
                return v;
            });
        } else {
            List<Object> datas = queryChartResultVO.getDatas();
            try {
                handleCompareData(datas, queryChartParameterVO);
            } catch (Exception e) {
                log.info("handleCompareData exception", e);
            }
            writeData(datas, writer, 0, "sheet1", v -> {
                if(StrUtil.equalsAny(v, "nan", "inf", "-inf")) {
                    return "";
                }
                return v;
            });
        }
        response.setContentType("application/vnd.ms-excel;charset=utf-8");

        String name = getName(queryChartParameterVO);
        response.setHeader("Content-Disposition", StrUtil.format("attachment;filename={}.xlsx", URLUtil.encode(name)));
        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        writer.close();
        IoUtil.close(out);
    }

    private String getName(QueryChartParameterVO queryChartParameterVO) {
        String name = Optional.ofNullable(queryChartParameterVO.getChartName()).orElse("图表");
        String nameAppend = "";
        for (QueryChartParameterVO.ConditionVO conditionVO : Optional.ofNullable(queryChartParameterVO.getDashboard()).orElse(Collections.emptyList())) {
            if (ColumnTypeConstant.DATE.equals(conditionVO.getOlapType())) {
                if(FilterLogicConstant.BETWEEN.equals(conditionVO.getLogic())) {
                    String value = queryChartService.handleDateValue(conditionVO.getValue());
                    List<String> stringList = JacksonUtil.fromJSONArray(value, String.class);
                    String start = CollectionUtil.get(stringList, 0);
                    String end = CollectionUtil.get(stringList, 1);
                    if(StrUtil.equals(start, end)) {
                        nameAppend = start;
                    } else {
                        nameAppend = StrUtil.format("{} -- {}", start, end);
                    }
                } else if(FilterLogicConstant.EQ.equals(conditionVO.getLogic())) {
                    nameAppend = conditionVO.getValue();
                }
                break;
            }
        }
        if(StrUtil.isNotEmpty(nameAppend)) {
            name = name + StrUtil.format("({})", nameAppend);
        }
        return name;
    }

    private void writeData(List<Object> datas, ExcelWriter writer, int sheetIndex, String sheetName, Function<String, String> measureDataFunction) {
        log.info("writeData 列数据转为行数据");
        List<String> firstRow = new LinkedList<>();
        List<List<Object>> rowDataList = Collections.emptyList();

        for (int i = 0; i < datas.size(); i++) {
            Object dataObj = datas.get(i);
            String displayName = ReflectUtil.invoke(dataObj, "getDisplayName");
            firstRow.add(displayName);
            List<String> colData = ReflectUtil.invoke(dataObj, "getData");
            if(CollectionUtil.isEmpty(colData)) {
                colData = new ArrayList<>(Collections.nCopies(rowDataList.size(), null));
            }

            QueryChartResultVO.MeasureData measureData = null;
            if(dataObj instanceof QueryChartResultVO.MeasureData) {
                measureData = (QueryChartResultVO.MeasureData) dataObj;
            }

            if(i == 0) {
                rowDataList = new ArrayList<>(colData.size());
            }

            for (int j = 0; j < colData.size(); j++) {
                String value = colData.get(j);
                if(measureData != null) {
                    value = measureDataFunction.apply(value);
                    if(AdvancedComputingConstant.FORMAT_PERCENT_SET.contains(measureData.getContrast()) || BooleanUtil.isTrue(measureData.getProportion())) {
                        value = formatPercent(value, 2);
                    } else {
                        if("percent".equals(measureData.getDigitDisplay())) {
                            int decimal = Optional.ofNullable(measureData.getDecimal()).orElse(2);
                            value = formatPercent(value, decimal);
                        }
                        if(StrUtil.isEmpty(measureData.getDigitDisplay()) && measureData.getDecimal() != null && measureData.getDecimal() >= 0) {
                            value = formatRound(value, measureData.getDecimal());
                        }
                    }
                }
                Object valueObj;
                try {
                    valueObj = new BigDecimal(value);
                } catch (Exception newBigDecimalException) {
                    valueObj = value;
                }

                if(i == 0) {
                    rowDataList.add(new ArrayList<>(datas.size()));
                }
                List<Object> rowData = rowDataList.get(j);
                rowData.add(valueObj);
            }
        }
        log.info("writeData 生成excel");

        StyleSet style = writer.getStyleSet();
        style.getCellStyleForNumber().setDataFormat((short) 0);
        writer.setSheet(sheetIndex);
        writer.renameSheet(sheetName);
        writer.writeHeadRow(firstRow);
        writer.write(rowDataList, true);
        log.info("writeData end");
    }

    private void handleCompareData(List<Object> datas, QueryChartParameterVO queryChartParameterVO) {
        String group = Optional.ofNullable(CollectionUtil.get(queryChartParameterVO.getDimension(), 0)).map(OlapChartDimension::getGroup).orElse("");
        List<Object> compareDataList = new ArrayList<>(datas.size());
        List<Object> momCompareDataList = new ArrayList<>(datas.size());
        List<Object> yoyCompareDataList = new ArrayList<>(datas.size());
        for (Object data : datas) {
            String displayName = ReflectUtil.invoke(data, "getDisplayName");
            List<String> colCompareData = ReflectUtil.invoke(data, "getCompareData");
            if(CollectionUtil.isNotEmpty(colCompareData)) {
                Object cloneData = BeanUtil.toBean(data, data.getClass());
                ReflectUtil.invoke(cloneData, "setData", colCompareData);
                ReflectUtil.invoke(cloneData, "setDisplayName", StrUtil.format("{}(对比)", displayName));
                compareDataList.add(cloneData);
            }

            if(ReflectUtil.getMethodOfObj(data, "getMomCompareData") != null) {
                List<String> colMomCompareData = ReflectUtil.invoke(data, "getMomCompareData");
                if(CollectionUtil.isNotEmpty(colMomCompareData)) {
                    Object cloneData = BeanUtil.toBean(data, data.getClass());
                    ReflectUtil.invoke(cloneData, "setData", colMomCompareData);
                    ReflectUtil.invoke(cloneData, "setDisplayName", StrUtil.format("{}({})", displayName,
                            StrUtil.equalsAny(group, TimeUnitConstant.HOUR, TimeUnitConstant.DAY) ? "昨日" : "环比 "));
                    momCompareDataList.add(cloneData);
                }
            }

            if(ReflectUtil.getMethodOfObj(data, "getYoyCompareData") != null) {
                List<String> colYoyCompareData = ReflectUtil.invoke(data, "getYoyCompareData");
                if(CollectionUtil.isNotEmpty(colYoyCompareData)) {
                    Object cloneData = BeanUtil.toBean(data, data.getClass());
                    ReflectUtil.invoke(cloneData, "setData", colYoyCompareData);
                    ReflectUtil.invoke(cloneData, "setDisplayName", StrUtil.format("{}({})", displayName,
                            StrUtil.equalsAny(group, TimeUnitConstant.HOUR, TimeUnitConstant.DAY) ? "上周同期" : "去年同期"));
                    yoyCompareDataList.add(cloneData);
                }
            }
        }
        datas.addAll(compareDataList);
        datas.addAll(momCompareDataList);
        datas.addAll(yoyCompareDataList);
    }

    private String formatPercent(String datum, int scale) {
        try {
            double number = Double.parseDouble(datum);
            return NumberUtil.formatPercent(number, scale);
        } catch (Exception e) {
            log.info("{} formatPercent 异常", datum);
        }
        return datum;
    }

    private String formatRound(String datum, int scale) {
        try {
            double number = Double.parseDouble(datum);
            return NumberUtil.roundStr(number, scale);
        } catch (Exception e) {
            log.info("{} formatPercent 异常", datum);
        }
        return datum;
    }

}
