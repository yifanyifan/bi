package com.stnts.bi.plugin.controller;

import com.stnts.bi.authorization.AuthCodeEnum;
import com.stnts.bi.authorization.CheckPerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.plugin.service.BaseService;
import com.stnts.bi.sql.vo.QueryChartResultVO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author liang.zhang
 * @date 2020年6月28日
 * @desc TODO
 * 覆盖
 */
@RestController
@RequestMapping("cover")
public class CoverController {

	@Autowired
	private BaseService baseService;

	@CheckPerm(authCode=AuthCodeEnum.BP_DASHBOARD_COVER_VIEW)
	@PostMapping("chart")
	public ResultEntity<QueryChartResultVO> chart(@RequestParam("data") String data) {
		return baseService.getChart(data);
	}

//	@PostMapping("channel/top")
//	public ResultEntity<QueryChartResultVO> channelTop(@RequestParam("data") String data) {
//		return baseService.getChart(data);
//    }
//
//	@PostMapping("plugin/top")
//	public ResultEntity<QueryChartResultVO> pluginTop(@RequestParam("data") String data) {
//		return baseService.getChart(data);
//    }

	@CheckPerm(authCode=AuthCodeEnum.BP_DASHBOARD_COVER_EXPORT)
	@RequestMapping("export")
	public void export(@RequestParam("data") String data, HttpServletResponse response) throws IOException {
		baseService.export(data, response);
	}
}
