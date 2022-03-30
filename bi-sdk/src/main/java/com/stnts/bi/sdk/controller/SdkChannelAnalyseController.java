package com.stnts.bi.sdk.controller;

import cn.hutool.core.thread.ThreadUtil;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sdk.service.SdkQueryChartService;
import com.stnts.bi.sdk.util.JacksonUtil;
import com.stnts.bi.sdk.vo.QueryChartParameterForSdkVO;
import com.stnts.bi.sql.service.ExportChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 渠道分析
 * @Author: 刘天元
 * @Date: 2021/5/20 15:07
 */
@RestController
@RequestMapping("/analyse/channel")
@Slf4j
public class SdkChannelAnalyseController {

    private final SdkQueryChartService sdkQueryChartService;

    private final ExportChartService exportChartService;

    public SdkChannelAnalyseController(SdkQueryChartService chartService, ExportChartService exportChartService) {
        this.sdkQueryChartService = chartService;
        this.exportChartService = exportChartService;
    }

    @PostMapping("/selector/get")
    public ResultEntity selector(@RequestParam("data") String data) {
        QueryChartParameterForSdkVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterForSdkVO.class);

        QueryChartResultVO queryChartResultVO = sdkQueryChartService.getQueryChartResultVO(queryChartParameterVO);
        return ResultEntity.success(queryChartResultVO);
    }

    @PostMapping("/distribution/chart/get")
    public ResultEntity getDistributionChart(@RequestParam("data") String data) {
        QueryChartParameterForSdkVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterForSdkVO.class);
        QueryChartResultVO queryChartResultVO = sdkQueryChartService.getQueryChartResultVO(queryChartParameterVO);
        return ResultEntity.success(queryChartResultVO);
    }

    @PostMapping("/tendency/chart/get")
    public ResultEntity getTendencyChart(@RequestParam("data") String data) {
        QueryChartParameterForSdkVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterForSdkVO.class);
        QueryChartResultVO queryChartResultVO = sdkQueryChartService.getQueryChartResultVO(queryChartParameterVO);
        return ResultEntity.success(queryChartResultVO);
    }

    @PostMapping("/effect/chart/get")
    public ResultEntity getEffectChart(@RequestParam("data") String data) {
        QueryChartParameterForSdkVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterForSdkVO.class);
        QueryChartResultVO queryChartResultVO = sdkQueryChartService.getQueryChartResultVO(queryChartParameterVO);
        return ResultEntity.success(queryChartResultVO);
    }

    @RequestMapping(value = "/distribution/chart/export" ,method = {RequestMethod.GET, RequestMethod.POST})
    public void exportDistributionChart(@RequestParam("data") String data,
                                     HttpServletResponse response) throws IOException {
        QueryChartParameterForSdkVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterForSdkVO.class);
        QueryChartResultVO queryChartResultVO = sdkQueryChartService.getQueryChartResultVO(queryChartParameterVO);
        exportChartService.exportChart(queryChartParameterVO, queryChartResultVO, response);
    }

    @RequestMapping(value = "/tendency/chart/export" ,method = {RequestMethod.GET, RequestMethod.POST})
    public void exportTendencyChart(@RequestParam("data") String data,
                                   HttpServletResponse response) throws IOException {
        QueryChartParameterForSdkVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterForSdkVO.class);
        QueryChartResultVO queryChartResultVO = sdkQueryChartService.getQueryChartResultVO(queryChartParameterVO);
        exportChartService.exportChart(queryChartParameterVO, queryChartResultVO, response);
    }

    @RequestMapping(value = "/effect/chart/export" ,method = {RequestMethod.GET, RequestMethod.POST})
    public void exportEffectChart(@RequestParam("data") String data,
                                    HttpServletResponse response) throws IOException {
        QueryChartParameterForSdkVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterForSdkVO.class);
        QueryChartResultVO queryChartResultVO = sdkQueryChartService.getQueryChartResultVO(queryChartParameterVO);
        exportChartService.exportChart(queryChartParameterVO, queryChartResultVO, response);
    }

    @RequestMapping(value = "/test/monitor" ,method = {RequestMethod.GET, RequestMethod.POST})
    public void testMonitor() {
        ThreadUtil.sleep(1000 * 60 * 3);
    }

}
