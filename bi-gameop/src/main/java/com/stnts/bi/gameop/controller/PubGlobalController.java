package com.stnts.bi.gameop.controller;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.gameop.service.BaseService;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * @author: liang.zhang
 * @description: 游戏发行
 * @date: 2021/4/27
 */
@RestController
@RequestMapping("pub/global")
public class PubGlobalController {

    @Autowired
    private BaseService baseService;

    @PostMapping("game/chart")
    public ResultEntity<QueryChartResultVO> game(@RequestBody String data) {
        return baseService.getChart(data);
    }

    @PostMapping("channel/chart")
    public ResultEntity<QueryChartResultVO> channel(@RequestBody String data) {
        return baseService.getChart(data);
    }

    //    @CheckPerm(authCode=AuthCodeEnum.BP_DASHBOARD_COVER_EXPORT)
    @PostMapping("game/export")
    public void gameExport(@RequestParam("data") String data, HttpServletResponse response)  {
        baseService.export(data, response);
    }

    @PostMapping("channel/export")
    public void channelExport(@RequestParam("data") String data, HttpServletResponse response) {
        baseService.export(data, response);
    }
}
