package com.stnts.bi.gameop.controller;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.gameop.service.BaseService;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 游戏运营2.0 归因查询
 *
 * @author chenchen
 * @since 2021/11/3.
 */
@RestController
@RequestMapping("op2/attribution")
@Slf4j
public class Op2AttributionController {

    @Autowired
    private BaseService baseService;

    @PostMapping("chart")
    public ResultEntity<QueryChartResultVO> attributionChart(@RequestBody String data){
        return baseService.getChart(data);
    }
}
