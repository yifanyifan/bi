package com.stnts.bi.plugin.special.handler;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.plugin.vo.QueryChartResultForTextLineRollupVO;
import com.stnts.bi.sql.entity.OlapChartDimension;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartParameterVO.ConditionVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import com.stnts.bi.sql.vo.QueryChartResultVO.MeasureData;

import cn.hutool.core.collection.CollectionUtil;


/**
 * @author liang.zhang
 * @date 2020年7月30日
 * @desc TODO
   *   异常分析  卡片处理
 */
public class PluginErrCardHandler extends BaseHandler{
	
	public static final String HANDLER_ID = "p005";

	@Override
	public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService,
			QueryChartParameterVO queryChartParameterVO) {
		
//		choiceTable("plugin_all_dwb_realtime_view", queryChartParameterVO);
		choiceTable("plugin_all_dwb_realtime_daily", queryChartParameterVO);
		System.out.println(">>>>>Choice table: " + queryChartParameterVO.getTableName());
		
//		List<OlapChartDimension> dims = queryChartParameterVO.getDimension();
//		dims = CollectionUtil.isEmpty(dims) ? new ArrayList<OlapChartDimension>() : dims;
		Optional<ConditionVO> condOp = queryChartParameterVO.getDashboard().stream().filter(cond -> StringUtils.equals(cond.getName(), PARTITION_COLNAME)).findFirst();
		Optional<OlapChartDimension> dimOp = queryChartParameterVO.getDimension().stream().filter(dim -> StringUtils.equals(dim.getName(), PARTITION_COLNAME)).findFirst();
//		OlapChartDimension dim = initPartitionDim(condOp);
//		if(null != dim && !isOverCycle(condOp))  //条件中有时间且时间周期是非跨天
//			dims.add(dim);
//		queryChartParameterVO.setDimension(dims);
		OlapChartDimension partitionDim = null;
		//如果是非单周期，则剔除周期维度
		if(!isSameCycle(condOp, dimOp)) {
			List<OlapChartDimension> dims = queryChartParameterVO.getDimension();
			for(Iterator<OlapChartDimension> it = dims.iterator() ; it.hasNext() ; ) {
				
				partitionDim = it.next();
				if(isPartitionCol(partitionDim)) {
					it.remove();
					break;
				}
			}
		}
		
//		CardChartResultVO resultVO = new CardChartResultVO();
		QueryChartResultVO resultVO = queryChartService.queryChart(queryChartParameterVO);
		
		List<MeasureData> meas = resultVO.getDatas().stream().filter(obj -> obj instanceof MeasureData).map(mea -> (MeasureData)mea).collect(Collectors.toList());
		QueryChartResultForTextLineRollupVO.TextLineRollupData cardVO = null;
		if(CollectionUtil.isNotEmpty(meas)) {
			cardVO = new QueryChartResultForTextLineRollupVO.TextLineRollupData();
			try {
				cardVO.setTextValue(meas.get(0).getData().get(0));
				cardVO.setYoyRateValue(meas.get(1).getData().get(0));
				cardVO.setMomRateValue(meas.get(2).getData().get(0));
			} catch (Exception e) {
			}
		}
		
		String dateValue = initDateCond(condOp);
		
		//上面把卡片数据取出来了，接下来取趋势，再合并
		queryChartParameterVO.getDashboard().stream().filter(cond -> StringUtils.equals(cond.getName(), PARTITION_COLNAME)).findFirst().get().setValue(dateValue);
		if(null != partitionDim) {
			partitionDim.setGroup("day");
			queryChartParameterVO.getDimension().add(partitionDim);
		}
		
		QueryChartResultForTextLineRollupVO _resultVO = new QueryChartResultForTextLineRollupVO();
		queryChartService.queryChart(queryChartParameterVO, _resultVO);
		_resultVO.setTextLineRollupData(cardVO);
		
		return ResultEntity.success(_resultVO);
	}
}
