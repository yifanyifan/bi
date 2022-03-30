package com.stnts.bi.gameop.controller;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.gameop.service.BaseService;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: liang.zhang
 * @description: 游戏运营 产品数据
 * @date: 2021/4/21
 */
@RestController
@RequestMapping("op/product")
public class OpProductController {

    @Autowired
    private BaseService baseService;

    //    @CheckPerm(authCode= AuthCodeEnum.BP_DASHBOARD_COVER_VIEW)
    @PostMapping("box/chart")
    public ResultEntity<QueryChartResultVO> box(@RequestBody String data) {
        return baseService.getChart(data);
    }

    @PostMapping("app/chart")
    public ResultEntity<QueryChartResultVO> app(@RequestBody String data) {
        return baseService.getChart(data);
    }

    //    @CheckPerm(authCode=AuthCodeEnum.BP_DASHBOARD_COVER_EXPORT)
    @PostMapping("box/export")
    public void boxExport(@RequestParam("data") String data, HttpServletResponse response) throws IOException {
        baseService.export(data, response);
    }

    @PostMapping("app/export")
    public void appExport(@RequestParam("data") String data, HttpServletResponse response) throws IOException {
        baseService.export(data, response);
    }
}
