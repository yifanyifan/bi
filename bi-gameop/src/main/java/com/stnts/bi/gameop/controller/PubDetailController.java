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
 * @description:
 * @date: 2021/4/27
 */
@RestController
@RequestMapping("pub/detail")
public class PubDetailController {

    @Autowired
    private BaseService baseService;

    //    @CheckPerm(authCode= AuthCodeEnum.BP_DASHBOARD_COVER_VIEW)
    @PostMapping("chart")
    public ResultEntity<QueryChartResultVO> chart(@RequestBody String data) {
        return baseService.getChart(data);
    }

    //    @CheckPerm(authCode=AuthCodeEnum.BP_DASHBOARD_COVER_EXPORT)
    @PostMapping("export")
    public void export(@RequestParam("data") String data, HttpServletResponse response) throws IOException {
        baseService.export(data, response);
    }
}
