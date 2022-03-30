package com.stnts.bi.dashboard.controller;

import cn.hutool.core.util.StrUtil;
import com.stnts.bi.authorization.AuthCodeEnum;
import com.stnts.bi.authorization.CheckPerm;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.dashboard.service.ChartService;
import com.stnts.bi.sql.service.ExportChartService;
import com.stnts.bi.sql.util.JacksonUtil;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2020/12/14
 */
@RestController
@RequestMapping("gameop")
public class GameopController {

    private final ChartService chartService;

    private final ExportChartService exportChartService;

    public GameopController(ChartService chartService, ExportChartService exportChartService) {
        this.chartService = chartService;
        this.exportChartService = exportChartService;
    }

    @CheckPerm(authCode = AuthCodeEnum.DASHBOARD_BIZ_GAMEOP_INCOME_VIEW)
    @RequestMapping("income/chart")
    public ResultEntity income(@RequestParam("data") String data,
                               @RequestParam(name = "sankeyNodeNumberPerLayer", required = false) Integer sankeyNodeNumberPerLayer,
                               @RequestParam(name = "roiType", required = false) Integer roiType){
        return getChart(data, sankeyNodeNumberPerLayer, roiType);
    }

    @CheckPerm(authCode = AuthCodeEnum.DASHBOARD_BIZ_GAMEOP_INCOME_EXPORT)
    @RequestMapping("income/export")
    public void incomeExport(@RequestParam("data") String data, HttpServletResponse response) throws IOException{
        export(data, response);
    }

    @CheckPerm(authCode = AuthCodeEnum.DASHBOARD_BIZ_GAMEOP_USER_VIEW)
    @RequestMapping("user/chart")
    public ResultEntity user(@RequestParam("data") String data,
                               @RequestParam(name = "sankeyNodeNumberPerLayer", required = false) Integer sankeyNodeNumberPerLayer,
                               @RequestParam(name = "roiType", required = false) Integer roiType){
        return getChart(data, sankeyNodeNumberPerLayer, roiType);
    }

    @CheckPerm(authCode = AuthCodeEnum.DASHBOARD_BIZ_GAMEOP_USER_EXPORT)
    @RequestMapping("user/export")
    public void userExport(@RequestParam("data") String data, HttpServletResponse response) throws IOException{
        export(data, response);
    }

    @CheckPerm(authCode = AuthCodeEnum.DASHBOARD_BIZ_GAMEOP_PRODUCT_VIEW)
    @RequestMapping("product/chart")
    public ResultEntity product(@RequestParam("data") String data,
                               @RequestParam(name = "sankeyNodeNumberPerLayer", required = false) Integer sankeyNodeNumberPerLayer,
                               @RequestParam(name = "roiType", required = false) Integer roiType){
        return getChart(data, sankeyNodeNumberPerLayer, roiType);
    }

    @CheckPerm(authCode = AuthCodeEnum.DASHBOARD_BIZ_GAMEOP_PRODUCT_EXPORT)
    @RequestMapping("product/export")
    public void productExport(@RequestParam("data") String data, HttpServletResponse response) throws IOException{
        export(data, response);
    }

    @CheckPerm(authCode = AuthCodeEnum.DASHBOARD_BIZ_GAMEOP_CHANNEL_VIEW)
    @RequestMapping("channel/chart")
    public ResultEntity channel(@RequestParam("data") String data,
                               @RequestParam(name = "sankeyNodeNumberPerLayer", required = false) Integer sankeyNodeNumberPerLayer,
                               @RequestParam(name = "roiType", required = false) Integer roiType){
        return getChart(data, sankeyNodeNumberPerLayer, roiType);
    }

    @CheckPerm(authCode = AuthCodeEnum.DASHBOARD_BIZ_GAMEOP_CHANNEL_EXPORT)
    @RequestMapping("channel/export")
    public void channelExport(@RequestParam("data") String data, HttpServletResponse response) throws IOException{
        export(data, response);
    }

    public ResultEntity getChart(@RequestParam("data") String data,
                                 @RequestParam(name = "sankeyNodeNumberPerLayer", required = false) Integer sankeyNodeNumberPerLayer,
                                 @RequestParam(name = "roiType", required = false) Integer roiType) {
        QueryChartParameterVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterVO.class);
        QueryChartResultVO resultVO = chartService.getChart(queryChartParameterVO, sankeyNodeNumberPerLayer, roiType);
        return ResultEntity.success(resultVO);
    }

    public void exportChartPost(@RequestParam("data") String data, HttpServletResponse response) throws IOException {
        export(data, response);
    }

    private void export(String data, HttpServletResponse response) throws IOException {
        QueryChartParameterVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterVO.class);
        queryChartParameterVO.setLimit(100000.0);
        queryChartParameterVO.getMeasure().forEach(v -> v.setPercentOfMax(null));
        if(StrUtil.equals(queryChartParameterVO.getId(), "income-analysis-50")) {
            //queryChartParameterVO.getMeasure().removeIf(v -> StrUtil.equalsAny(v.getName(), "(sum(real_income) - sum(real_cost))/any(profit_target_b)", "sum(real_income)/any(income_target_b)"));
            queryChartParameterVO.getMeasure().removeIf(v -> StrUtil.equalsAny(v.getAliasName(), "考核目标/完成度-利润完成度", "考核目标/完成度-收入完成度"));
        }
        QueryChartResultVO resultVO = chartService.getChart(queryChartParameterVO);
        exportChartService.exportChart(queryChartParameterVO, resultVO, response);
    }

    public void exportChartGet(@RequestParam("data") String data, HttpServletResponse response) throws IOException {
        export(data, response);
    }
}
