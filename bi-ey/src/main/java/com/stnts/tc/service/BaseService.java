package com.stnts.tc.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.stnts.bi.sql.vo.QueryChartResultVO;
import com.stnts.tc.common.ResultEntity;
import com.stnts.tc.vo.IndexVO;

/**
 * @author liang.zhang
 * @date 2019年12月10日
 * @desc TODO
 */
public interface BaseService {
	
	/** 查询网吧信息 根据前缀匹配 */
	public List<IndexVO> listBar(String pre);

	public List<IndexVO> listPlugin(String pre);
	
	/**
	 * 公共的查询方法
	 * @param data
	 * @return
	 */
	public ResultEntity<QueryChartResultVO> getChart(String data);
	
	
	public void export(String data, HttpServletResponse response);
}
