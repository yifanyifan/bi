package com.stnts.bi.gameop.controller;

import com.stnts.bi.authorization.AuthCodeEnum;
import com.stnts.bi.authorization.CheckPerm;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.gameop.service.BaseService;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: liang.zhang
 * @description: 游戏运营 整体数据
 * @date: 2021/4/21
 */
@RestController
@RequestMapping("op/global")
public class OpGlobalController {

    @Autowired
    private BaseService baseService;

//    @CheckPerm(authCode= AuthCodeEnum.BP_DASHBOARD_COVER_VIEW)
    @PostMapping("game/chart")
    public ResultEntity<QueryChartResultVO> game(@RequestBody String data) {
        return baseService.getChart(data);
    }

    @PostMapping("channel/chart")
    public ResultEntity<QueryChartResultVO> channel(@RequestBody String data) {
        return baseService.getChart(data);
    }

    @PostMapping("kpi/chart")
    public ResultEntity<QueryChartResultVO> kpi(@RequestBody String data) {
        return baseService.getChart(data);
    }

//    @CheckPerm(authCode=AuthCodeEnum.BP_DASHBOARD_COVER_EXPORT)
    @PostMapping("game/export")
    public void gameExport(@RequestParam("data") String data, HttpServletResponse response) throws IOException {
        baseService.export(data, response);
    }

    @PostMapping("channel/export")
    public void channelExport(@RequestParam("data") String data, HttpServletResponse response) throws IOException {
        baseService.export(data, response);
    }

    @PostMapping("kpi/export")
    public void kpiExport(@RequestParam("data") String data, HttpServletResponse response) throws IOException {
        baseService.export(data, response);
    }
}
