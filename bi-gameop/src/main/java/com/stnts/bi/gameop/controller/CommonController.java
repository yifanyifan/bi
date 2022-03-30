package com.stnts.bi.gameop.controller;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.gameop.service.BaseService;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/4/27
 */
@RestController
@RequestMapping("common")
public class CommonController {

    @Autowired
    private BaseService baseService;

    @PostMapping("chart")
    public ResultEntity<QueryChartResultVO> chart(@RequestBody String data) {
        return baseService.getChart(data);
    }
}
