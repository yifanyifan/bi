package com.stnts.bi.sdk.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sdk.exception.ValidationException;
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
 * 趋势分析
 * @Author: 刘天元
 * @Date: 2021/5/20 15:07
 */
@RestController
@RequestMapping("/data/overview")
@Slf4j
public class SdkDataOverviewController {

    private final SdkQueryChartService sdkQueryChartService;

    private final ExportChartService exportChartService;

    public SdkDataOverviewController(SdkQueryChartService chartService, ExportChartService exportChartService) {
        this.sdkQueryChartService = chartService;
        this.exportChartService = exportChartService;
    }

    @PostMapping("/selector/get")
    public ResultEntity selector(@RequestParam("data") String data) {
        QueryChartParameterForSdkVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterForSdkVO.class);

        ResultEntity resultEntity = validateProduct(queryChartParameterVO);
        if (resultEntity != null) {
            return resultEntity;
        }

        QueryChartResultVO queryChartResultVO = sdkQueryChartService.getQueryChartResultVO(queryChartParameterVO);
        return ResultEntity.success(queryChartResultVO);
    }

    @PostMapping("/operation/chart/get")
    public ResultEntity getOperationChart(@RequestParam("data") String data) {
        QueryChartParameterForSdkVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterForSdkVO.class);

        ResultEntity resultEntity = validateProduct(queryChartParameterVO);
        if (resultEntity != null) {
            return resultEntity;
        }

        QueryChartResultVO queryChartResultVO = sdkQueryChartService.getQueryChartResultVO(queryChartParameterVO);
        return ResultEntity.success(queryChartResultVO);
    }

    private ResultEntity validateProduct(QueryChartParameterForSdkVO queryChartParameterVO) {
        if(CollectionUtil.isNotEmpty(queryChartParameterVO.getDashboard())) {
            for (QueryChartParameterVO.ConditionVO conditionVO : queryChartParameterVO.getDashboard()) {
                if(StrUtil.equals(conditionVO.getName(), "product_id")) {
                    String productValue = conditionVO.getValue();
                    if(StrUtil.isEmpty(productValue)) {
                        return ResultEntity.param("请选择产品线");
                    }
                }
            }
        }
        return null;
    }

    @PostMapping("/payment/chart/get")
    public ResultEntity getPaymentChart(@RequestParam("data") String data) {
        QueryChartParameterForSdkVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterForSdkVO.class);
        QueryChartResultVO queryChartResultVO = sdkQueryChartService.getQueryChartResultVO(queryChartParameterVO);
        return ResultEntity.success(queryChartResultVO);
    }

    @RequestMapping(value = "/operation/chart/export" ,method = {RequestMethod.GET, RequestMethod.POST})
    public void exportOperationChart(@RequestParam("data") String data, 
                                     HttpServletResponse response) throws IOException {
        QueryChartParameterForSdkVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterForSdkVO.class);
        QueryChartResultVO queryChartResultVO = sdkQueryChartService.getQueryChartResultVO(queryChartParameterVO);
        exportChartService.exportChart(queryChartParameterVO, queryChartResultVO, response);
    }

    @RequestMapping(value = "/payment/chart/export" ,method = {RequestMethod.GET, RequestMethod.POST})
    public void exportPaymentChart(@RequestParam("data") String data, 
                                   HttpServletResponse response) throws IOException {
        QueryChartParameterForSdkVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterForSdkVO.class);
        QueryChartResultVO queryChartResultVO = sdkQueryChartService.getQueryChartResultVO(queryChartParameterVO);
        exportChartService.exportChart(queryChartParameterVO, queryChartResultVO, response);
    }
}
