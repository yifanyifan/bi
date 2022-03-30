package com.stnts.bi.mapper.gameop;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.entity.gameop.DimCost;
import com.stnts.bi.entity.gameop.DimCostOp;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/8/24
 */
@Mapper
public interface DimCostOpMapper extends BaseMapper<DimCostOp> {

    @Insert("<script>"
            + "insert into dim_cost_op(cost_date, pid, real_cost) values "
            + "<foreach collection='costs' item='cost' index='i' separator=','>"
            + "(#{cost.costDate},#{cost.pid},#{cost.realCost})"
            + "</foreach> "
            + "ON DUPLICATE KEY UPDATE "
            + "real_cost=VALUES(real_cost)"
            + "</script>")
    void insertBatch(@Param("costs") List<DimCostOp> dimCostOps);

    /**
     *
     * @param dimCostOp
     */
    @Insert("<script>" +
            "insert into dim_cost_op(cost_date, pid, real_cost) values " +
            "(#{cost.costDate},#{cost.pid},#{cost.realCost}) " +
            "ON DUPLICATE KEY UPDATE " +
            "real_cost=VALUES(real_cost)" +
            "</script>")
    int insertOne(@Param("cost") DimCostOp dimCostOp);
}
