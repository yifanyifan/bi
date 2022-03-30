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
 * @description: 游戏运营V2 质量分析
 * @date: 2021/8/24
 */
@RestController
@RequestMapping("op2/qa")
public class Op2QaController {

    @Autowired
    private BaseService baseService;

    @PostMapping("ret/chart")
    public ResultEntity<QueryChartResultVO> retChart(@RequestBody String data){
        return baseService.getChart(data);
    }

    @PostMapping("ret/export")
    public void retExport(@RequestParam("data") String data, HttpServletResponse response) throws IOException {
        baseService.export(data, response);
    }

    @PostMapping("ltv/chart")
    public ResultEntity<QueryChartResultVO> ltvChart(@RequestBody String data){
        return baseService.getChart(data);
    }

    @PostMapping("ltv/export")
    public void ltvExport(@RequestParam("data") String data, HttpServletResponse response) throws IOException {
        baseService.export(data, response);
    }

    @PostMapping("recharge/chart")
    public ResultEntity<QueryChartResultVO> rechargeChart(@RequestBody String data){
        return baseService.getChart(data);
    }

    @PostMapping("recharge/export")
    public void rechargeExport(@RequestParam("data") String data, HttpServletResponse response) throws IOException {
        baseService.export(data, response);
    }
}
