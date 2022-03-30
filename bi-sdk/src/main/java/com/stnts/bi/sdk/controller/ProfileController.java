package com.stnts.bi.sdk.controller;

import cn.hutool.core.util.StrUtil;
import com.stnts.bi.authorization.AuthCodeEnum;
import com.stnts.bi.authorization.CheckPerm;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sdk.service.ChartService;
import com.stnts.bi.sdk.util.JacksonUtil;
import com.stnts.bi.sql.service.ExportChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: liang.zhang
 * @description: SDK - 数据概况
 * @date: 2020/12/11
 */
@RestController
@RequestMapping("profile")
public class ProfileController {

    @Autowired
    private ChartService chartService;
    @Autowired
    private ExportChartService exportChartService;
    @Autowired
    private PageChartPCController pageChartPCController;

    @CheckPerm(authCode = AuthCodeEnum.SDK_PROFILE_REALTIME_KEY_VIEW, checkProduct = true)
    @PostMapping("realtime/key")
    public ResultEntity key(@RequestParam("data") String data) {
        QueryChartParameterVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterVO.class);
        if(StrUtil.startWith(queryChartParameterVO.getId(), "sdk-pageAnalysis-v2")) {
            return pageChartPCController.getChart(queryChartParameterVO);
        }
        QueryChartResultVO queryChartResultVO = chartService.getChart(queryChartParameterVO);
        return ResultEntity.success(queryChartResultVO);
    }

    @PostMapping("realtime/kpi")
    public ResultEntity kpi(@RequestParam("data") String data) {
        QueryChartParameterVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterVO.class);
        if(StrUtil.startWith(queryChartParameterVO.getId(), "sdk-pageAnalysis-v2")) {
            return pageChartPCController.getChart(queryChartParameterVO);
        }
        QueryChartResultVO queryChartResultVO = chartService.getChart(queryChartParameterVO);
        return ResultEntity.success(queryChartResultVO);
    }
}
