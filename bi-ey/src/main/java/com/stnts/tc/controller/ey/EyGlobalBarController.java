package com.stnts.tc.controller.ey;

import java.util.Map;

import com.stnts.bi.authorization.AuthCodeEnum;
import com.stnts.bi.authorization.CheckPerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stnts.bi.sql.vo.QueryChartResultVO;
import com.stnts.tc.common.ResultEntity;
import com.stnts.tc.query.EyGlobalBarNewQuery;
import com.stnts.tc.query.EyGlobalBarReQuery;
import com.stnts.tc.service.BaseService;
import com.stnts.tc.service.EyGlobalBarService;

import javax.servlet.http.HttpServletResponse;

/**
 * @author liang.zhang
 * @date 2019年11月15日
 * @desc TODO
 * 易乐游-全局-网吧数据
 */
@RestController
@RequestMapping("global/bar")
public class EyGlobalBarController {
	
	@Autowired
	private EyGlobalBarService eyGlobalBarService;
	
	@Autowired
	private BaseService baseService;

	@CheckPerm(authCode = AuthCodeEnum.EY_GLOBAL_NEWLY_VIEW)
	@GetMapping("newly")
	public ResultEntity<Map<String, Object>> newly(EyGlobalBarNewQuery query){
		
		Map<String, Object> dat = eyGlobalBarService.eyGlobalBarNew(query);
		return ResultEntity.success(dat);
	}

	@CheckPerm(authCode = AuthCodeEnum.EY_GLOBAL_RETAIN_VIEW)
	@GetMapping("retention")
	public ResultEntity<Map<String, Object>> retention(EyGlobalBarReQuery eyGlobalBarReQuery){
		
		Map<String, Object> dat = eyGlobalBarService.eyGlobalBarRetention(eyGlobalBarReQuery);
		return ResultEntity.success(dat);
	}

	@CheckPerm(authCode = AuthCodeEnum.EY_GLOBAL_NEWLY_VIEW)
	@PostMapping("chart/newly")
	public ResultEntity<QueryChartResultVO> chartNewly(@RequestParam("data") String data) {
        return baseService.getChart(data);
    }

	@CheckPerm(authCode = AuthCodeEnum.EY_GLOBAL_RETAIN_VIEW)
	@PostMapping("chart/retention")
	public ResultEntity<QueryChartResultVO> chartRetention(@RequestParam("data") String data) {
		return baseService.getChart(data);
    }

	@CheckPerm(authCode = AuthCodeEnum.EY_GLOBAL_NEWLY_EXPORT)
	@PostMapping("export/newly")
	public void exportNewly(@RequestParam("data") String data, HttpServletResponse response) {
		baseService.export(data, response);
	}

	@CheckPerm(authCode = AuthCodeEnum.EY_GLOBAL_RETAIN_EXPORT)
	@PostMapping("export/retention")
	public void exportRetention(@RequestParam("data") String data, HttpServletResponse response) {
		baseService.export(data, response);
	}
}
