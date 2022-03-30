package com.stnts.bi.plugin.special.handler;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartParameterVO.ConditionVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import org.apache.commons.lang3.StringUtils;

/**
 * @author liang.zhang
 * @date 2020年8月6日
 * @desc TODO
 *   异常分析 - 插件详情 - 异常明细
 */
public class PluginErrHandler extends BaseHandler {

    public static final String HANDLER_ID = "p008";

    @Override
    public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService,
                                                    QueryChartParameterVO queryChartParameterVO) {

        boolean hasErrCode = queryChartParameterVO.getDashboard().stream().map(ConditionVO::getName).anyMatch(name -> StringUtils.equals(name, "err_code"));
//		String tableName = hasErrCode ? "plugin_all_dwb_realtime_view_111" : "plugin_all_dwb_realtime_view_110";
        String tableName = hasErrCode ? "plugin_all_dwb_realtime_daily_111" : "plugin_all_dwb_realtime_daily_110";
        queryChartParameterVO.setTableName(tableName);
        System.out.println(">>>>>Choice table: " + queryChartParameterVO.getTableName());
        QueryChartResultVO resultVO = queryChartService.queryChart(queryChartParameterVO);
        //追加对比上周  对比上月
        appendCompNew(queryChartService, queryChartParameterVO, resultVO);

        return ResultEntity.success(resultVO);
    }
}
