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
 * @description: 明细数据
 * @date: 2021/8/24
 */
@RestController
@RequestMapping("pub2/detail")
public class Pub2DetailController {

    @Autowired
    private BaseService baseService;

    @PostMapping("chart")
    public ResultEntity<QueryChartResultVO> gameChart(@RequestBody String data){
        return baseService.getChart(data);
    }

    @PostMapping("export")
    public void gameExport(@RequestParam("data") String data, HttpServletResponse response) throws IOException {
        baseService.export(data, response);
    }
}
