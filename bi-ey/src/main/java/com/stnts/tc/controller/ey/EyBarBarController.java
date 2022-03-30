package com.stnts.tc.controller.ey;

import java.util.Map;

import com.stnts.bi.authorization.AuthCodeEnum;
import com.stnts.bi.authorization.CheckPerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stnts.tc.common.ResultEntity;
import com.stnts.tc.query.EyBarBarAuditQuery;
import com.stnts.tc.query.EyBarBarChannelQuery;
import com.stnts.tc.service.EyBarBarService;

/**
 * @author liang.zhang
 * @date 2019年11月15日
 * @desc TODO
 * 易乐游-网吧-网吧质量
 */
@RestController
@RequestMapping("bar/bar")
public class EyBarBarController {
	
	@Autowired
	private EyBarBarService eyBarBarService;

	@CheckPerm(authCode = AuthCodeEnum.EY_BAR_SCORE_VIEW)
	@RequestMapping("audit")
	public ResultEntity<Map<String, Object>> audit(EyBarBarAuditQuery query){
		
		Map<String, Object> result = eyBarBarService.audit(query);
		return ResultEntity.success(result);
	}

	@CheckPerm(authCode = AuthCodeEnum.EY_BAR_SCORE_VIEW)
	@RequestMapping("channel")
	public ResultEntity<Map<String, Object>> channel(EyBarBarChannelQuery query){
		
		Map<String, Object> result = eyBarBarService.channel(query);
		return ResultEntity.success(result);
	}
}
