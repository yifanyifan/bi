package com.stnts.bi.gameop.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.gameop.handlers.Handler;
import com.stnts.bi.gameop.handlers.HandlerFactory;
import com.stnts.bi.gameop.service.BaseService;
import com.stnts.bi.sql.constant.FilterLogicConstant;
import com.stnts.bi.sql.service.ExportChartService;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.util.JacksonUtil;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2020/10/22
 */
@Slf4j
@Service
public class BaseServiceImpl implements BaseService {

    @Autowired
    private ExportChartService exportChartService;

    @Autowired
    private QueryChartService queryChartService;

    @Override
    public ResultEntity<QueryChartResultVO> getChart(String data) {
        try {

            QueryChartParameterVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterVO.class);
            if(StrUtil.containsAny(queryChartParameterVO.getTableName(), "retention_agg_view", "retention_agg_week_view", "retention_agg_month_view", "retention_pay_agg_view", "retention_pay_agg_week_view", "retention_pay_agg_month_view")){
                QueryChartParameterVO.ConditionVO conditionVO = new QueryChartParameterVO.ConditionVO();
                if(StringUtils.containsAny(queryChartParameterVO.getTableName(), "retention_agg_view", "retention_pay_agg_view")) {
                    conditionVO.setName("addDays(`new_date`, `date_gap`)");
                    conditionVO.setFunc("");
                } else if(StringUtils.containsAny(queryChartParameterVO.getTableName(), "retention_agg_week_view", "retention_pay_agg_week_view")) {
                    conditionVO.setName("addWeeks(`new_date`, `date_gap`)");
                    conditionVO.setFunc("");
                } else if(StringUtils.containsAny(queryChartParameterVO.getTableName(), "retention_agg_month_view", "retention_pay_agg_month_view")) {
                    conditionVO.setName("addMonths(`new_date`, `date_gap`)");
                    conditionVO.setFunc("");
                }
                conditionVO.setLogic(FilterLogicConstant.LTE);
                conditionVO.setValue(DateUtil.today());
                queryChartParameterVO.getDashboard().add(conditionVO);
            }
            queryChartParameterVO.setDatabaseName("bi_gameop");
            String id = queryChartParameterVO.getId();
            if(StringUtils.isNotEmpty(id)){
                Handler handler = HandlerFactory.getHandler(id);
                if(null != handler){
                    return handler.handler(queryChartService, queryChartParameterVO);
                }
            }
            QueryChartResultVO queryChartResultVO = queryChartService.queryChart(queryChartParameterVO);
            return ResultEntity.success(queryChartResultVO);
        } catch (Exception e) {
            log.warn("[ENGINE]异常.", e);
            return ResultEntity.exception(e.getMessage());
        }
    }

    @Override
    public void export(String data, HttpServletResponse response) {

        try {
            QueryChartParameterVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterVO.class);
            if(StrUtil.containsAny(queryChartParameterVO.getTableName(), "retention_agg_view", "retention_agg_week_view", "retention_agg_month_view", "retention_pay_agg_view", "retention_pay_agg_week_view", "retention_pay_agg_month_view")){
                QueryChartParameterVO.ConditionVO conditionVO = new QueryChartParameterVO.ConditionVO();
                if(StringUtils.containsAny(queryChartParameterVO.getTableName(), "retention_agg_view", "retention_pay_agg_view")) {
                    conditionVO.setName("addDays(`new_date`, `date_gap`)");
                    conditionVO.setFunc("");
                } else if(StringUtils.containsAny(queryChartParameterVO.getTableName(), "retention_agg_week_view", "retention_pay_agg_week_view")) {
                    conditionVO.setName("addWeeks(`new_date`, `date_gap`)");
                    conditionVO.setFunc("");
                } else if(StringUtils.containsAny(queryChartParameterVO.getTableName(), "retention_agg_month_view", "retention_pay_agg_month_view")) {
                    conditionVO.setName("addMonths(`new_date`, `date_gap`)");
                    conditionVO.setFunc("");
                }
                conditionVO.setLogic(FilterLogicConstant.LTE);
                conditionVO.setValue(DateUtil.today());
                queryChartParameterVO.getDashboard().add(conditionVO);
            }
            queryChartParameterVO.setDatabaseName("bi_gameop");
            QueryChartResultVO queryChartResultVO = null;
            String id = queryChartParameterVO.getId();
            //如果ID不为空,则特殊处理 特殊处理 特殊处理
            if(StringUtils.isNotBlank(id)) {
                Handler handler = HandlerFactory.getHandler(id);
                if(null != handler) {
                    ResultEntity<QueryChartResultVO> _resultVO = handler.handler(queryChartService, queryChartParameterVO);
                    queryChartResultVO = _resultVO.getData();
                }
            }else {
                queryChartResultVO = queryChartService.queryChart(queryChartParameterVO);
            }
            exportChartService.exportChart(queryChartParameterVO, queryChartResultVO, response);
        } catch (Exception e) {
            log.warn("[EXPORT]: {}", e);
        }
    }
}
