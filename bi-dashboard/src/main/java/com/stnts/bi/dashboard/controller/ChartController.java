package com.stnts.bi.dashboard.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.dashboard.service.ChartService;
import com.stnts.bi.sql.entity.OlapChartMeasure;
import com.stnts.bi.sql.service.ExportChartService;
import com.stnts.bi.sql.util.JacksonUtil;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;


/**
 * @author liutianyuan
 */
@RestController
@RequestMapping("/analyse/chart")
@Slf4j
@Validated
public class ChartController {

    private final ChartService chartService;

    private final ExportChartService exportChartService;

    public ChartController(ChartService chartService, ExportChartService exportChartService) {
        this.chartService = chartService;
        this.exportChartService = exportChartService;
    }

    @PostMapping("/get")
    public ResultEntity getChart(@RequestParam("data") String data,
                                 @RequestParam(name = "sankeyNodeNumberPerLayer", required = false) Integer sankeyNodeNumberPerLayer,
                                 @RequestParam(name = "roiType", required = false) Integer roiType) {
        QueryChartParameterVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterVO.class);
        QueryChartResultVO resultVO = chartService.getChart(queryChartParameterVO, sankeyNodeNumberPerLayer, roiType);
        return ResultEntity.success(resultVO);
    }

    @PostMapping("/export")
    public void exportChartPost(@RequestParam("data") String data, HttpServletResponse response) throws IOException {
        export(data, response);
    }

    private void export(String data, HttpServletResponse response) throws IOException {
        QueryChartParameterVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterVO.class);
        queryChartParameterVO.setLimit(100000.0);
        queryChartParameterVO.getMeasure().forEach(v -> v.setPercentOfMax(null));
        if(StrUtil.equals(queryChartParameterVO.getId(), "income-analysis-50")) {
            queryChartParameterVO.getMeasure().removeIf(v -> StrUtil.equalsAny(v.getName(), "(sum(real_income) - sum(real_cost))/any(profit_target_b)", "sum(real_income)/any(income_target_b)"));
        }
        QueryChartResultVO resultVO = chartService.getChart(queryChartParameterVO);
        exportChartService.exportChart(queryChartParameterVO, resultVO, response);
    }

    @GetMapping("/export")
    public void exportChartGet(@RequestParam("data") String data, HttpServletResponse response) throws IOException {
        export(data, response);
    }

    @GetMapping("/incomeanalysis45/select")
    public ResultEntity selectForIncomeAnalysis45() {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.set("id",1);
        jsonObject1.set("name", "日均新增用户");
        jsonObject1.set("field", "new_user");
        jsonArray.add(jsonObject1);

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.set("id",2);
        jsonObject2.set("name", "有效付费用户数");
        jsonObject2.set("field", "pay_user");
        jsonArray.add(jsonObject2);

        JSONObject jsonObject3 = new JSONObject();
        jsonObject3.set("id",3);
        jsonObject3.set("name", "KA产品数");
        jsonObject3.set("field", "ka_product");
        jsonArray.add(jsonObject3);

        JSONObject jsonObject4 = new JSONObject();
        jsonObject4.set("id",4);
        jsonObject4.set("name", "CPS的KA渠道数");
        jsonObject4.set("field", "cps_channel");
        jsonArray.add(jsonObject4);

        JSONObject jsonObject5 = new JSONObject();
        jsonObject5.set("id",5);
        jsonObject5.set("name", "非CPS的KA渠道数");
        jsonObject5.set("field", "no_cps_channel");
        jsonArray.add(jsonObject5);

        JSONObject jsonObject6 = new JSONObject();
        jsonObject6.set("id",6);
        jsonObject6.set("name", "总充值超过2w的用户MAU");
        jsonObject6.set("field", "keral_user");
        jsonArray.add(jsonObject6);
        return ResultEntity.success(jsonArray);
    }

}
