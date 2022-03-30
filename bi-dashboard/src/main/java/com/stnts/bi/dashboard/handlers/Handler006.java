package com.stnts.bi.dashboard.handlers;

import cn.hutool.core.collection.CollectionUtil;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @author: liang.zhang
 * @description:
 * @date: 2020/11/13
 *
 * 带带业务大盘-收入分析-累计消费
 */
public class Handler006 extends BaseHandler{

    public static final String HANDLER_ID = "dashboard-dd-006";

    @Override
    public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO) {

        QueryChartResultVO queryChartResultVO = queryChartService.queryChart(queryChartParameterVO);
        if(CollectionUtil.isNotEmpty(queryChartResultVO.getDatas())){

            queryChartResultVO.getDatas().stream().forEach(data -> {
                QueryChartResultVO.MeasureData meaData = (QueryChartResultVO.MeasureData) data;
                // && !StringUtils.equals(e, "0")
                List<String> collect = meaData.getData().stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
                int size = collect.size();
                int index = size > 0 ? size - 1 : size;
                List<String> result = collect.stream().skip(index).collect(Collectors.toList());
                meaData.setData(result);
            });
        }
        return ResultEntity.success(queryChartResultVO);
    }
}
