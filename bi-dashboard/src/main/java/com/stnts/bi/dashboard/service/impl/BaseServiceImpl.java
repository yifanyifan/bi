package com.stnts.bi.dashboard.service.impl;

import cn.hutool.core.lang.Assert;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.dashboard.config.Constants;
import com.stnts.bi.dashboard.handlers.Handler;
import com.stnts.bi.dashboard.handlers.Handler001;
import com.stnts.bi.dashboard.handlers.Handler002;
import com.stnts.bi.dashboard.handlers.HandlerFactory;
import com.stnts.bi.dashboard.service.BaseService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2020/10/22
 */
@Slf4j
@Service
public class BaseServiceImpl implements BaseService, Constants {

    @Autowired
    private ExportChartService exportChartService;

    @Autowired
    private QueryChartService queryChartService;

    @Override
    public ResultEntity<QueryChartResultVO> getChart(String data) {
        return getChart(data, null);
    }

    @Override
    public ResultEntity<QueryChartResultVO> getChart(QueryChartParameterVO queryChartParameterVO, Integer sankeyNodeNumberPerLayer) {
        try {

//            QueryChartParameterVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterVO.class);
            queryChartParameterVO.setDatabaseName("bi_dashboard");
            setAggDimCond(queryChartParameterVO);
            String id = queryChartParameterVO.getId();
            //如果ID不为空,则特殊处理 特殊处理 特殊处理
            if(StringUtils.isNotBlank(id)) {
                Handler handler = HandlerFactory.getHandler(id);
                if(null != handler) {
                    if(null == sankeyNodeNumberPerLayer){
                        return handler.handler(queryChartService, queryChartParameterVO);
                    }else{
                        return handler.handler(queryChartService, queryChartParameterVO, sankeyNodeNumberPerLayer);
                    }
                }
            }
            QueryChartResultVO queryChartResultVO = queryChartService.queryChart(queryChartParameterVO);
            return ResultEntity.success(queryChartResultVO);
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("[ENGINE]: {}", e.getMessage());
            return ResultEntity.exception(e.getMessage());
        }
    }

    @Override
    public ResultEntity<QueryChartResultVO> getChart(String data, Integer sankeyNodeNumberPerLayer) {
        try {

            QueryChartParameterVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterVO.class);
            return getChart(queryChartParameterVO, sankeyNodeNumberPerLayer);
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("[ENGINE]: {}", e.getMessage());
            return ResultEntity.exception(e.getMessage());
        }
    }

    @Override
    public void export(String data, HttpServletResponse response) {

        export(data, null, response);
//        try {
//
//            QueryChartParameterVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterVO.class);
//            queryChartParameterVO.setDatabaseName("bi_dashboard");
//            setAggDimCond(queryChartParameterVO);
//            String id = queryChartParameterVO.getId();
//            QueryChartResultVO queryChartResultVO = null;
//            //如果ID不为空,则特殊处理 特殊处理 特殊处理
//            if(StringUtils.isNotBlank(id)) {
//                Handler handler = HandlerFactory.getHandler(id);
//                if(null != handler) {
//                    ResultEntity<QueryChartResultVO> _resultVO = handler.handler(queryChartService, queryChartParameterVO);
//                    queryChartResultVO = _resultVO.getData();
//                }
//            }
//            exportChartService.exportChart(queryChartParameterVO, queryChartResultVO, response);
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.warn("[EXPORT]: {}", e.getMessage());
//        }
    }

    @Override
    public void export(String data, Integer sankeyNodeNumberPerLayer, HttpServletResponse response) {

        try {

            QueryChartParameterVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterVO.class);
            ResultEntity<QueryChartResultVO> chart = getChart(queryChartParameterVO, sankeyNodeNumberPerLayer);
            if(StringUtils.equals(queryChartParameterVO.getId(), "dashboard-dd-305")){
                try {
                    chart.getData().getDatas().removeIf(o-> {
                        if(o instanceof QueryChartResultVO.DimensionData){

                            QueryChartResultVO.DimensionData dim = (QueryChartResultVO.DimensionData)o;
                            return StringUtils.equals("channel_id", dim.getName());
                        }
                        return false;
                    });
                }catch(Exception e){
                }
            }
            exportChartService.exportChart(queryChartParameterVO, chart.getData(), response);
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("[EXPORT]: {}", e.getMessage());
        }
    }

    public void setAggDimCond(QueryChartParameterVO queryChartParameterVO){

        List<QueryChartParameterVO.ConditionVO> conds = queryChartParameterVO.getDashboard();
        String handlerId = queryChartParameterVO.getId();
        if(StringUtils.isEmpty(handlerId)){
            return;
        }
        QueryChartParameterVO.ConditionVO aggDimCond = initAggDimCond(handlerId, conds);
        conds.add(aggDimCond);
    }

    /**
     * 组装agg_dim条件
     * @param handlerId
     * @return
     */
    public QueryChartParameterVO.ConditionVO initAggDimCond(String handlerId, List<QueryChartParameterVO.ConditionVO> conds){

        Map<String, String> collect = conds.stream().collect(Collectors.toMap(QueryChartParameterVO.ConditionVO::getName, QueryChartParameterVO.ConditionVO::getValue));
        String version = collect.getOrDefault("channel_sea", KEY_VERSION_ALL);
        String channel = collect.getOrDefault("channel_id", KEY_CHANNEL_ALL);
        QueryChartParameterVO.ConditionVO conditionVO = new QueryChartParameterVO.ConditionVO();
        conditionVO.setName(COL_AGG_DIM);
        conditionVO.setLogic(FilterLogicConstant.EQ);
        String value = null;
        switch (handlerId){

            /**
             * TODO 把默认情况的case去掉 走default
             */

            //目标完成度
            case Handler001.HANDLER_ID : value = toAggDim(KEY_CYCLE_D, version, channel, null); break;
            //充值构成
            case Handler002.HANDLER_ID : value = toAggDim(KEY_CYCLE_D, version, channel, "rt") ; break;
            //现金充值明细
            case "dashboard-dd-003" : value = toAggDim(KEY_CYCLE_D, version, channel, "ct"); break;
            //成本构成
            case "dashboard-dd-004" : value = toAggDim(KEY_CYCLE_D, version, channel, "cp"); break;
            //付费构成
            case "dashboard-dd-005" : value = toAggDim(KEY_CYCLE_D, version, channel, "pt") ; break;
            //平台钻石消费情况
            case "dashboard-dd-006" : value = toAggDim(KEY_CYCLE_D, version, channel, null); break;
            case "dashboard-dd-007" : value = toAggDim(KEY_CYCLE_D, version, channel, null); break;
            //利润趋势图
            case "dashboard-dd-008" : value = toAggDim(KEY_CYCLE_D, version, channel, null); break;
            case "dashboard-dd-009" : value = toAggDim(KEY_CYCLE_D, version, channel, null); break;
            case "dashboard-dd-010" : value = toAggDim(KEY_CYCLE_D, version, channel, null); break;

            //[用户分析]
            //用户DAU 全部
            case "dashboard-dd-101" : value = toAggDim(KEY_CYCLE_D, version, channel, null); break;
            //用户DAU终端
            case "dashboard-dd-102" : value = toAggDim(KEY_CYCLE_D, version, channel, "os"); break;
            //新增用户 全部
            case "dashboard-dd-103" : value = toAggDim(KEY_CYCLE_D, version, channel, null); break;
            //新增用户终端
            case "dashboard-dd-104" : value = toAggDim(KEY_CYCLE_D, version, channel, "os"); break;
            //用户MAU
            case "dashboard-dd-105" : value = toAggDim(KEY_CYCLE_M, version, channel, null); break;
            //用户次月留存
            case "dashboard-dd-106" : value = toAggDim(KEY_CYCLE_M, version, channel, null); break;
            //用户充值排名
            case "dashboard-dd-107" : value = toAggDim(KEY_CYCLE_D, null, null, "uid"); break;
            //用户明细
            //新增
            case "dashboard-dd-108" : value = toAggDim(KEY_CYCLE_D, version, channel, null); break;
            //充值
            case "dashboard-dd-109" : value = toAggDim(KEY_CYCLE_D, version, channel, "rt"); break;
            //付费
            case "dashboard-dd-110" : value = toAggDim(KEY_CYCLE_D, version, channel, "pt"); break;
            //转化
            case "dashboard-dd-111" : value = toAggDim(KEY_CYCLE_D, version, channel, null); break;

            //[大神分析]
            //大神日均DAU
            case "dashboard-dd-201" : value = toAggDim(KEY_CYCLE_D, version, null, "god"); break;
            //大神日均新增-总
            case "dashboard-dd-202" : value = toAggDim(KEY_CYCLE_D, version, null, "god"); break;
            //大神日均新增-终端
            case "dashboard-dd-203" : value = toAggDim(KEY_CYCLE_D, version, null, "os:god"); break;
            //大神MAU
            case "dashboard-dd-204" : value = toAggDim(KEY_CYCLE_M, version, null, "god"); break;
            //大神热度排行榜
            case "dashboard-dd-205" : value = toAggDim(KEY_CYCLE_D, null, null, "god:uid"); break;
            //陪玩订单分布-总
            case "dashboard-dd-206" : value = toAggDim(KEY_CYCLE_D, version, null, "oc:god"); break;
            //陪玩订单分布-分
            case "dashboard-dd-207" : value = toAggDim(KEY_CYCLE_D, version, null, "oc:god"); break;
            //大神数据明细
            case "dashboard-dd-208" : value = toAggDim(KEY_CYCLE_D, version, null, "god"); break;

            //[渠道分析]
            //301特殊处理
            case "dashboard-dd-301" : value = toAggDim("d:t", "sea", "ch", null); break;
            case "dashboard-dd-302" : value = toAggDim(KEY_CYCLE_D, "sea", "ch", null); break;
            case "dashboard-dd-303" : value = toAggDim(KEY_CYCLE_D, "sea", "ch", "sch:cid"); break;
            case "dashboard-dd-304" : value = toAggDim(KEY_CYCLE_D, "sea", "ch", "sch:cid"); break;
            case "dashboard-dd-305" : value = toAggDim(KEY_CYCLE_D, "sea", "ch", null); break;

            default: value = toAggDim(KEY_CYCLE_D, version, channel, null);
        }
        Assert.notNull(value);
        conditionVO.setValue(value);
        return conditionVO;
    }

    private String toAggDim(String cycle, String version, String channel, String module){

        List<String> aggDimList = new ArrayList<>();
        aggDimList.add(cycle);
        aggDimList.add(StringUtils.equalsAny(version, null, KEY_VERSION_ALL) ? null : KEY_VERSION);
        aggDimList.add(StringUtils.equalsAny(channel, null, KEY_CHANNEL_ALL) ? null : KEY_CHANNEL);
        aggDimList.add(module);
        aggDimList.removeIf(Objects::isNull);
        return StringUtils.join(aggDimList, KEY_SEPARATOR);
    }
}
