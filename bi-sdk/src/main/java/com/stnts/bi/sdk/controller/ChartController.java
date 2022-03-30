package com.stnts.bi.sdk.controller;

import cn.hutool.core.util.StrUtil;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sdk.service.ChartService;
import com.stnts.bi.sdk.util.JacksonUtil;
import com.stnts.bi.sql.service.ExportChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


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
    private final PageChartPCController pageChartPCController;

    public ChartController(ChartService chartService, ExportChartService exportChartService, PageChartPCController pageChartPCController) {
        this.chartService = chartService;
        this.exportChartService = exportChartService;
        this.pageChartPCController = pageChartPCController;
    }


    @PostMapping("/get")
    public ResultEntity getChart(@RequestParam("data") String data) {
        QueryChartParameterVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterVO.class);
        if(StrUtil.startWith(queryChartParameterVO.getId(), "sdk-pageAnalysis-v2")) {
            return pageChartPCController.getChart(queryChartParameterVO);
        }
        QueryChartResultVO queryChartResultVO = chartService.getChart(queryChartParameterVO);
        return ResultEntity.success(queryChartResultVO);
    }

    @GetMapping("/export")
    public void exportChartGet(@RequestParam("data") String data, HttpServletResponse response) throws IOException {
        QueryChartParameterVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterVO.class);
        QueryChartResultVO queryChartResultVO = export(queryChartParameterVO);
        exportChartService.exportChart(queryChartParameterVO, queryChartResultVO, response);
    }

    @PostMapping("/export")
    public void exportChartPost(@RequestParam("data") String data, HttpServletResponse response) throws IOException {
        QueryChartParameterVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterVO.class);
        QueryChartResultVO queryChartResultVO = export(queryChartParameterVO);
        exportChartService.exportChart(queryChartParameterVO, queryChartResultVO, response);
    }

    private QueryChartResultVO export(QueryChartParameterVO queryChartParameterVO) {
        queryChartParameterVO.setLimit(100000.0);
        QueryChartResultVO queryChartResultVO;
        if(StrUtil.startWith(queryChartParameterVO.getId(), "sdk-pageAnalysis-v2")) {
            queryChartResultVO = pageChartPCController.getChart(queryChartParameterVO).getData();
        } else {
            queryChartResultVO = chartService.getChart(queryChartParameterVO);
        }
        return queryChartResultVO;
    }

}
