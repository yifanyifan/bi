package com.stnts.tc.controller.ey;

import java.util.Map;

import com.stnts.bi.authorization.AuthCodeEnum;
import com.stnts.bi.authorization.CheckPerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stnts.tc.common.ResultEntity;
import com.stnts.tc.query.EyBarPluginAnalysisQuery;
import com.stnts.tc.query.EyBarPluginProfileQuery;
import com.stnts.tc.service.EyBarPluginService;

/**
 * @author liang.zhang
 * @date 2019年11月15日
 * @desc TODO
 * 易乐游-网吧-插件数据
 */
@RestController
@RequestMapping("bar/plugin")
public class EyBarPluginController {
	
	@Autowired
	private EyBarPluginService eyBarPluginService;

	@CheckPerm(authCode = AuthCodeEnum.EY_BAR_PLUGIN_VIEW)
	@RequestMapping("profile")
	public ResultEntity<Map<String, Object>> profile(EyBarPluginProfileQuery query){
		
		Map<String, Object> result = eyBarPluginService.profile(query);
		return ResultEntity.success(result);
	}

	@CheckPerm(authCode = AuthCodeEnum.EY_BAR_PLUGIN_VIEW)
	@RequestMapping("analysis")
	public ResultEntity<Map<String, Object>> analysis(EyBarPluginAnalysisQuery query){
		
		Map<String, Object> result = eyBarPluginService.analysis(query);
		return ResultEntity.success(result);
	}
}
