package com.stnts.tc.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.mortbay.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.stnts.bi.sql.service.ExportChartService;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.util.JacksonUtil;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import com.stnts.tc.common.Constants;
import com.stnts.tc.common.HbaseConfiguration;
import com.stnts.tc.common.ResultEntity;
import com.stnts.tc.service.BaseService;
import com.stnts.tc.utils.HbaseClient;
import com.stnts.tc.vo.IndexVO;

/**
 * @author liang.zhang
 * @date 2019年12月10日
 * @desc TODO
 */
@Service("baseService")
public class BaseServiceImpl implements BaseService, Constants {

	@Autowired
	private HbaseConfiguration hbaseConf;

	@Autowired
	private HbaseClient hbase;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	@Autowired
    private ExportChartService exportChartService;

	@Autowired
	private QueryChartService queryChartService;

	@Override
	public List<IndexVO> listBar(String pre) {

		try {

			if (StringUtils.isBlank(pre)) { // 直接返回活跃网吧10个
				Map<Object, Object> map = redisTemplate.opsForHash().entries(REDIS_BAR_HASH);
				if (null != map && !map.isEmpty()) {
					List<IndexVO> indexes = new ArrayList<IndexVO>();
					map.entrySet().stream().limit(10).forEach(e -> {
						indexes.add(new IndexVO(String.valueOf(e.getKey()), String.valueOf(e.getValue())));
					});
					return indexes;
				}
			}
			return hbase.scanByIndex(hbaseConf.getBarIndexTable(), pre, DE_F, DE_C);
		} catch (Exception e) {
			Log.warn("listBar: " + e.getMessage());
		}
		return null;
	}

	@Override
	public List<IndexVO> listPlugin(String pre) {
		try {

			return hbase.scanByIndex(hbaseConf.getPluginIndexTable(), pre, DE_F, DE_C);
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public ResultEntity<QueryChartResultVO> getChart(String data) {
		try {

			QueryChartParameterVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterVO.class);
			queryChartParameterVO.setDatabaseName("bi_ey");
			QueryChartResultVO queryChartResultVO = queryChartService.queryChart(queryChartParameterVO);
			return ResultEntity.success(queryChartResultVO);
		} catch (Exception e) {
			Log.warn("[ENGINE]: {}", e.getMessage());
			return ResultEntity.exception(e.getMessage());
		}

	}

	@Override
	public void export(String data, HttpServletResponse response) {

		try {
			
			QueryChartParameterVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterVO.class);
			queryChartParameterVO.setDatabaseName("bi_ey");
			exportChartService.exportChart(queryChartParameterVO, response);
		} catch (Exception e) {
			Log.warn("[EXPORT]: {}", e.getMessage());
		}
	}
}
