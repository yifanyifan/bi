package com.stnts.bi.sys.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.LogOpEntity;
import com.stnts.bi.mapper.sys.LogOpMapper;
import com.stnts.bi.sys.common.SysConfig;
import com.stnts.bi.sys.service.LogOpService;
import com.stnts.bi.sys.utils.SysUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liang.zhang
 * @date 2020年3月26日
 * @desc TODO
 */
@Service
@Slf4j
public class LogOpServiceImpl implements LogOpService {

	@Autowired
	private SysConfig sysConfig;

	@Autowired
	private LogOpMapper logOpMapper;

	@Override
	public ResultEntity<Page<LogOpEntity>> findLogListByCond(Integer pageNo, String cnname, String beginDate,
			String endDate, String cond) {
		
		List<LogOpEntity> ops = new ArrayList<>();
		try {
			Page<LogOpEntity> page = SysUtil.toPage(pageNo, this.sysConfig.getPageSize().intValue());
			String _cond = SysUtil.toLike(cond);
			String _cnname = SysUtil.toLike(cnname);
			ops = this.logOpMapper.findLogListByCond(page, _cond, beginDate, endDate, _cnname);
			page.setRecords(ops);
			return ResultEntity.success(page);
		} catch (Exception e) {
			log.warn("findLogListByCond failed, err: " + e.getMessage());
			return ResultEntity.exception(e.getMessage());
		}
	}

}
