package com.stnts.bi.plugin.special.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import com.stnts.bi.sql.vo.QueryChartParameterVO.ConditionVO;


/**
 * @author liang.zhang
 * @date 2020年8月27日
 * @desc TODO
 *  -多维交叉
 */
public class PluginCross4MultiDim extends BaseHandler{
	
	public static final String HANDLER_ID = "p013";
	
	public static final String TABLE_NAME = "plugin_dwb_realtime_cross_off_110";
	
	public static final String KEY_PLUGIN = "plugin_ids";
	public static final String KEY_CHANNEL = "channel_ids";
	
	@Override
	public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService,
			QueryChartParameterVO queryChartParameterVO) {
		
		List<ConditionVO> conds = queryChartParameterVO.getDashboard();
		List<String> _conds  = new ArrayList<String>();
		for(Iterator<ConditionVO> it = conds.iterator() ; it.hasNext() ; ) {
			
			ConditionVO cond = it.next();
			String colName = cond.getName();
			if(StringUtils.equalsAny(colName, KEY_PLUGIN, KEY_CHANNEL)) {
				
				String subCond = cond.getValue();
				if(StringUtils.equals(colName, KEY_CHANNEL)) {
					//渠道传参可能是整型，可能是字符串
					List<String> subCondList = new ArrayList<String>();
					JSON.parseArray(subCond).forEach(item -> {
						subCondList.add(String.valueOf(item));
					});
					subCond = JSON.toJSONString(subCondList);	
				}
//				subCond = StringUtils.equals(colName, KEY_CHANNEL) && subCond.indexOf("\"") != -1 ? subCond.replaceAll("\"", "\'") : String.format("\'%s\'", subCond);
				String _cond = String.format("hasAll(%s , %s)", colName, subCond.replaceAll("\"", "\'"));
				_conds.add(_cond);
				
				it.remove();
			}
		}
		String condSql = StringUtils.join(_conds, " and ");
		queryChartParameterVO.setConditionSql(condSql);
		
		return super.handler(queryChartService, queryChartParameterVO);
	}
}
