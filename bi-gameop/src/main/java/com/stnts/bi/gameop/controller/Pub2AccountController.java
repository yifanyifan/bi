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
 * @description: 帐号分析
 * @date: 2021/8/24
 */
@RestController
@RequestMapping("pub2/account")
public class Pub2AccountController {

    @Autowired
    private BaseService baseService;

    @PostMapping("loc/chart")
    public ResultEntity<QueryChartResultVO> locChart(@RequestBody String data){
        return baseService.getChart(data);
    }

    @PostMapping("loc/export")
    public void locExport(@RequestParam("data") String data, HttpServletResponse response) throws IOException {
        baseService.export(data, response);
    }

    @PostMapping("os/chart")
    public ResultEntity<QueryChartResultVO> osChart(@RequestBody String data){
        return baseService.getChart(data);
    }

    @PostMapping("os/export")
    public void osExport(@RequestParam("data") String data, HttpServletResponse response) throws IOException {
        baseService.export(data, response);
    }

    @PostMapping("game/chart")
    public ResultEntity<QueryChartResultVO> gameChart(@RequestBody String data){
        return baseService.getChart(data);
    }

    @PostMapping("game/export")
    public void gameExport(@RequestParam("data") String data, HttpServletResponse response) throws IOException {
        baseService.export(data, response);
    }
}
