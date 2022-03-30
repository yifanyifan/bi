package com.stnts.bi.dashboard.handlers;

import cn.hutool.core.collection.CollectionUtil;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import org.apache.commons.lang.math.IntRange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2020/11/17
 * 大神排行榜占比除以榜首
 */
public class Handler205 extends BaseHandler{

    public static final String HANDLER_ID = "dashboard-dd-205";

    @Override
    public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO) {

        rmConds(queryChartParameterVO);
        QueryChartResultVO queryChartResultVO = queryChartService.queryChart(queryChartParameterVO);

        List<Object> objects = Optional.ofNullable(queryChartResultVO.getDatas()).orElse(Collections.emptyList());
        Optional<QueryChartResultVO.MeasureData> first = objects.stream().filter(obj -> obj instanceof QueryChartResultVO.MeasureData).map(mea -> (QueryChartResultVO.MeasureData) mea).findFirst();
        appendRelativeRatio(first, queryChartResultVO);
//        if(first.isPresent()){
//            List<String> data = first.get().getData();
//            if(CollectionUtil.isNotEmpty(data)){
//                String max = data.get(0);
//                int size = data.size();
//                List<String> compData = new ArrayList<>(size);
//                IntStream.range(0, size).forEach(i -> {
//                    compData.add(ratio(data.get(i), max));
//                });
//                QueryChartResultVO.MeasureData compMea = new QueryChartResultVO.MeasureData();
//                compMea.setData(compData);
//                compMea.setDisplayName("完成订单占比");
//                queryChartResultVO.getDatas().add(compMea);
//
//                List<List<String>> rowFormatDataList = queryChartResultVO.getRowFormatDataList();
//                IntStream.range(0, rowFormatDataList.size()).forEach(i -> {
//                    rowFormatDataList.get(i).add(compData.get(i));
//                });
//            }
//        }

        return ResultEntity.success(queryChartResultVO);
    }
}
