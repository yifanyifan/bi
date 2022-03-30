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
import com.stnts.tc.query.EyGlobalBasicBarQuery;
import com.stnts.tc.query.EyGlobalBasicChannelQuery;
import com.stnts.tc.query.EyGlobalBasicProfileQuery;
import com.stnts.tc.service.BaseService;
import com.stnts.tc.service.EyGlobalBasicService;

import javax.servlet.http.HttpServletResponse;

/**
 * @author liang.zhang
 * @date 2019年11月15日
 * @desc TODO
 * 易乐游-全局-基础数据
 */
@RestController
@RequestMapping("global/basic")
//@Api("易乐游-全局-基础数据")
public class EyGlobalBasicController {
	
	@Autowired
	private EyGlobalBasicService eyGlobalBasicService;
	
	@Autowired
	private BaseService baseService;
	
//	@ApiOperation("数据概况")
	@CheckPerm(authCode = AuthCodeEnum.EY_GLOBAL_ACTIVE_VIEW)
	@GetMapping("profile")
	public ResultEntity<Map<String, Object>> profile(EyGlobalBasicProfileQuery query){
		
		Map<String, Object> dat = eyGlobalBasicService.eyGlobalBasicProfile(query);
		return ResultEntity.success(dat);
	}

	@CheckPerm(authCode = AuthCodeEnum.EY_GLOBAL_CHANNEL_VIEW)
	@GetMapping("channel")
	public ResultEntity<Map<String, Object>> channel(EyGlobalBasicChannelQuery query){
		
		Map<String, Object> dat = eyGlobalBasicService.eyGlobalBasicChannel(query);
		return ResultEntity.success(dat);
	}

	@CheckPerm(authCode = AuthCodeEnum.EY_GLOBAL_SCORE_VIEW)
	@GetMapping("bar")
	public ResultEntity<Map<String, Object>> bar(EyGlobalBasicBarQuery query){
		
		Map<String, Object> dat = eyGlobalBasicService.eyGlobalBasicBar(query);
		return ResultEntity.success(dat);
	}

	// clickhouse

	@CheckPerm(authCode = AuthCodeEnum.EY_GLOBAL_ACTIVE_VIEW)
	@PostMapping("chart/active")
	public ResultEntity<QueryChartResultVO> chartActive(@RequestParam("data") String data) {
        return baseService.getChart(data);
    }

	@CheckPerm(authCode = AuthCodeEnum.EY_GLOBAL_CHANNEL_VIEW)
	@PostMapping("chart/channel")
	public ResultEntity<QueryChartResultVO> chartChannel(@RequestParam("data") String data) {
		return baseService.getChart(data);
    }

	@CheckPerm(authCode = AuthCodeEnum.EY_GLOBAL_SCORE_VIEW)
	@PostMapping("chart/bar")
	public ResultEntity<QueryChartResultVO> chartBar(@RequestParam("data") String data) {
		return baseService.getChart(data);
    }

    //导出

	@CheckPerm(authCode = AuthCodeEnum.EY_GLOBAL_ACTIVE_EXPORT)
	@PostMapping("export/active")
	public void exportActive(@RequestParam("data") String data, HttpServletResponse response) {
		baseService.export(data, response);
	}

	@CheckPerm(authCode = AuthCodeEnum.EY_GLOBAL_CHANNEL_EXPORT)
	@PostMapping("export/channel")
	public void exportChannel(@RequestParam("data") String data, HttpServletResponse response) {
		baseService.export(data, response);
	}

	@CheckPerm(authCode = AuthCodeEnum.EY_GLOBAL_SCORE_EXPORT)
	@PostMapping("export/bar")
	public void exportBar(@RequestParam("data") String data, HttpServletResponse response) {
		baseService.export(data, response);
	}
}
