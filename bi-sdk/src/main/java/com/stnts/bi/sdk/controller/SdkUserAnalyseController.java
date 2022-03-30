package com.stnts.bi.sdk.controller;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sdk.service.SdkQueryChartService;
import com.stnts.bi.sdk.util.JacksonUtil;
import com.stnts.bi.sdk.vo.QueryChartParameterForSdkVO;
import com.stnts.bi.sql.service.ExportChartService;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 趋势分析
 * @Author: 刘天元
 * @Date: 2021/5/20 15:07
 */
@RestController
@RequestMapping("/analyse/user")
@Slf4j
public class SdkUserAnalyseController {

    private final SdkQueryChartService sdkQueryChartService;

    private final ExportChartService exportChartService;

    public SdkUserAnalyseController(SdkQueryChartService chartService, ExportChartService exportChartService) {
        this.sdkQueryChartService = chartService;
        this.exportChartService = exportChartService;
    }

    @PostMapping("/retain/chart/get")
    public ResultEntity getRetainChart(@RequestParam("data") String data) {
        QueryChartParameterForSdkVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterForSdkVO.class);

        QueryChartResultVO queryChartResultVO = sdkQueryChartService.getQueryChartResultVO(queryChartParameterVO);
        return ResultEntity.success(queryChartResultVO);
    }

    @PostMapping("/ltv/chart/get")
    public ResultEntity getLTVChart(@RequestParam("data") String data) {
        QueryChartParameterForSdkVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterForSdkVO.class);

        QueryChartResultVO queryChartResultVO = sdkQueryChartService.getQueryChartResultVO(queryChartParameterVO);
        return ResultEntity.success(queryChartResultVO);
    }

    @RequestMapping(value = "/retain/chart/export" ,method = {RequestMethod.GET, RequestMethod.POST})
    public void exportRetainChartGet(@RequestParam("data") String data, HttpServletResponse response) throws IOException {
        QueryChartParameterForSdkVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterForSdkVO.class);
        QueryChartResultVO queryChartResultVO = sdkQueryChartService.getQueryChartResultVO(queryChartParameterVO);
        exportChartService.exportChart(queryChartParameterVO, queryChartResultVO, response);
    }

    @RequestMapping(value = "/ltv/chart/export" ,method = {RequestMethod.GET, RequestMethod.POST})
    public void exportLTVChartGet(@RequestParam("data") String data, HttpServletResponse response) throws IOException {
        QueryChartParameterForSdkVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterForSdkVO.class);
        QueryChartResultVO queryChartResultVO = sdkQueryChartService.getQueryChartResultVO(queryChartParameterVO);
        exportChartService.exportChart(queryChartParameterVO, queryChartResultVO, response);
    }
}
