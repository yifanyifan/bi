package com.stnts.bi.dashboard.controller;

import com.stnts.bi.authorization.AuthCodeEnum;
import com.stnts.bi.authorization.CheckPerm;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.dashboard.service.BaseService;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2020/10/22
 * 带带业务大盘-收入分析
 */
@RestController
@RequestMapping("income")
public class DdIncomeController {

    @Autowired
    private BaseService baseService;

    @CheckPerm(authCode = AuthCodeEnum.DASHBOARD_BIZ_DDPW_INCOME_VIEW)
    @PostMapping("chart")
    public ResultEntity<QueryChartResultVO> chart(@RequestParam("data") String data) {
        return baseService.getChart(data);
    }

    @CheckPerm(authCode = AuthCodeEnum.DASHBOARD_BIZ_DDPW_INCOME_EXPORT)
    @RequestMapping("export")
    public void export(@RequestParam("data") String data, HttpServletResponse response){
        baseService.export(data, response);
    }
}
