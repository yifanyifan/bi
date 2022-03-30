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
import com.stnts.tc.query.EyGlobalPluginAnalysisQuery;
import com.stnts.tc.query.EyGlobalPluginProfileQuery;
import com.stnts.tc.service.BaseService;
import com.stnts.tc.service.EyGlobalPluginService;

import javax.servlet.http.HttpServletResponse;

/**
 * @author liang.zhang
 * @date 2019年11月15日
 * @desc TODO
 * 易乐游-全局-插件数据
 */
@RestController
@RequestMapping("global/plugin")
public class EyGlobalPluginController {
	
	@Autowired
	private EyGlobalPluginService eyGlobalPluginService;
	
	@Autowired
	private BaseService baseService;

	@CheckPerm(authCode = AuthCodeEnum.EY_GLOBAL_PLUGIN_VIEW)
	@GetMapping("profile")
	public ResultEntity<Map<String, Object>> profile(EyGlobalPluginProfileQuery query){
		
		Map<String, Object> dat = eyGlobalPluginService.eyGlobalPluginProfile(query);
		return ResultEntity.success(dat);
	}

	@CheckPerm(authCode = AuthCodeEnum.EY_GLOBAL_PLUGIN_VIEW)
	@GetMapping("analysis")
	public ResultEntity<Map<String, Object>> analysis(EyGlobalPluginAnalysisQuery query){
		
		Map<String, Object> dat = eyGlobalPluginService.eyGlobalPluginAnalysis(query);
		return ResultEntity.success(dat);
	}

	@CheckPerm(authCode = AuthCodeEnum.EY_GLOBAL_PLUGIN_VIEW)
	@PostMapping("chart")
	public ResultEntity<QueryChartResultVO> getChart(@RequestParam("data") String data) {
        return baseService.getChart(data);
    }

	@CheckPerm(authCode = AuthCodeEnum.EY_GLOBAL_PLUGIN_EXPORT)
	@PostMapping("export")
	public void export(@RequestParam("data") String data, HttpServletResponse response) {
		baseService.export(data, response);
	}
}
