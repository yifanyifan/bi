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
 * 带带业务大盘-渠道分析
 */
@RestController
@RequestMapping("channel")
public class DdChannelController {

    @Autowired
    private BaseService baseService;

    @CheckPerm(authCode = AuthCodeEnum.DASHBOARD_BIZ_DDPW_CHANNEL_VIEW)
    @PostMapping("chart")
    public ResultEntity<QueryChartResultVO> chart(@RequestParam("data") String data,
                                                  @RequestParam(name = "sankeyNodeNumberPerLayer", required = false) Integer sankeyNodeNumberPerLayer) {
        return baseService.getChart(data, sankeyNodeNumberPerLayer);
    }

    @CheckPerm(authCode = AuthCodeEnum.DASHBOARD_BIZ_DDPW_CHANNEL_EXPORT)
    @RequestMapping("export")
    public void export(@RequestParam("data") String data,
                       @RequestParam(name = "sankeyNodeNumberPerLayer", required = false) Integer sankeyNodeNumberPerLayer,
                       HttpServletResponse response){
        baseService.export(data, sankeyNodeNumberPerLayer, response);
    }
}
