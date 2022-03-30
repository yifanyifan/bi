package com.stnts.tc.controller.ey;

import java.util.Map;

import com.stnts.bi.authorization.AuthCodeEnum;
import com.stnts.bi.authorization.CheckPerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stnts.tc.common.ResultEntity;
import com.stnts.tc.query.EyBarBasicBaseQuery;
import com.stnts.tc.query.EyBarBasicChannelQuery;
import com.stnts.tc.query.EyBarBasicKpiQuery;
import com.stnts.tc.service.EyBarBasicService;


/**
 * @author liang.zhang
 * @date 2019年11月15日
 * @desc TODO
 * 易乐游-网吧-基础数据
 */
@RestController
@RequestMapping("bar/basic")
public class EyBarBasicController {
	
	@Autowired
	private EyBarBasicService eyBarBasicService;

	@CheckPerm(authCode = AuthCodeEnum.EY_BAR_INFO_VIEW)
	@RequestMapping("base")
	public ResultEntity<Map<String, Object>> base(EyBarBasicBaseQuery query){
		
		Map<String, Object> dat = eyBarBasicService.base(query);
		return ResultEntity.success(dat);
	}

	@CheckPerm(authCode = AuthCodeEnum.EY_BAR_ACTIVE_VIEW)
	@RequestMapping("kpi")
	public ResultEntity<Map<String, Object>> kpi(EyBarBasicKpiQuery query){
		
		Map<String, Object> dat = eyBarBasicService.kpi(query);
		return ResultEntity.success(dat);
	}

	@CheckPerm(authCode = AuthCodeEnum.EY_BAR_CHANNEL_VIEW)
	@RequestMapping("channel")
	public ResultEntity<Map<String, Object>> channel(EyBarBasicChannelQuery query){
		
		Map<String, Object> dat = eyBarBasicService.channel(query);
		return ResultEntity.success(dat);
	}
}
