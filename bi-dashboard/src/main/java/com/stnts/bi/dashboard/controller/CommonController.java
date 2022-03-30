package com.stnts.bi.dashboard.controller;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.dashboard.service.BaseService;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2020/11/3
 */
@RestController
@RequestMapping("common")
public class CommonController {

    @Autowired
    private BaseService baseService;

    @PostMapping("channels")
    public ResultEntity<QueryChartResultVO> chart(@RequestParam("data") String data) {
        return baseService.getChart(data);
    }
}
