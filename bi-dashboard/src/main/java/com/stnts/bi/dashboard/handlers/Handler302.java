package com.stnts.bi.dashboard.handlers;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.dfp.DfpField;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2020/11/3
 */
public class Handler302 extends BaseHandler {

    public static final String HANDLER_ID = "dashboard-dd-302";

    @Override
    public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO) {

        rmConds(queryChartParameterVO);

        QueryChartResultVO queryChartResultVO = queryChartService.queryChart(queryChartParameterVO);
        List<Object> objects = Optional.ofNullable(queryChartResultVO.getDatas()).orElse(Collections.emptyList());
        //
        Optional<QueryChartResultVO.MeasureData> vlist = objects.stream()
                .filter(obj -> {
                    boolean flag = false;
                    if (obj instanceof QueryChartResultVO.MeasureData) {

                        QueryChartResultVO.MeasureData mea = (QueryChartResultVO.MeasureData) obj;
                        flag = null == mea.getProportion();
                    }
                    return flag;
                })
                .map(obj -> (QueryChartResultVO.MeasureData) obj).findFirst();
        appendRelativeRatio(vlist, queryChartResultVO);
        Optional<QueryChartResultVO.MeasureData> first = objects.stream()
                .filter(obj -> {
                    boolean flag = false;
                    if (obj instanceof QueryChartResultVO.MeasureData) {

                        QueryChartResultVO.MeasureData mea = (QueryChartResultVO.MeasureData) obj;
                        flag = Optional.ofNullable(mea.getProportion()).orElse(false);
                    }
                    return flag;
                })
                .map(obj -> (QueryChartResultVO.MeasureData) obj).findFirst();
//                .filter(QueryChartResultVO.MeasureData::getProportion).findFirst();
        //这里first是占比
        if (first.isPresent()) {
            QueryChartResultVO.MeasureData mea = first.get();
            List<String> totalList = new ArrayList<>();
            BigDecimal sumV = BigDecimal.ZERO;
            for (String item : mea.getData()) {
                try{
                    //这里值可能为 -- 或其它
                    BigDecimal v = new BigDecimal(item);
                    sumV = sumV.add(v);
                }catch(Exception e){
                }
                totalList.add(sumV.toString());
            }
//            totalList.forEach(System.out::println);

            QueryChartResultVO.MeasureData ratioMea = new QueryChartResultVO.MeasureData();
            ratioMea.setData(totalList);
            ratioMea.setDisplayName("累加占比");

            List<List<String>> rowFormatDataList = queryChartResultVO.getRowFormatDataList();
            IntStream.range(0, rowFormatDataList.size()).forEach(i -> {
                rowFormatDataList.get(i).add(totalList.get(i));
            });
//            ratioMea.setDecimal(4);
        }

        return ResultEntity.success(queryChartResultVO);
    }
}
