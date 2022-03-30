package com.stnts.bi.mapper.gameop;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.entity.gameop.DimCost;
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
public interface DimCostMapper extends BaseMapper<DimCost> {

    @Insert("<script>"
            + "insert into dim_cost(cost_date, pid, charge_model, source, book_cost, real_cost) values "
            + "<foreach collection='costs' item='cost' index='i' separator=','>"
            + "(#{cost.costDate},#{cost.pid},#{cost.chargeModel},#{cost.source},#{cost.bookCost},#{cost.realCost})"
            + "</foreach> "
            + "ON DUPLICATE KEY UPDATE "
            + "charge_model=VALUES(charge_model), source=VALUES(source), book_cost=VALUES(book_cost), real_cost=VALUES(real_cost)"
            + "</script>")
    void insertBatch(@Param("costs") List<DimCost> dimCosts);

    /**
     *
     * @param dimCost
     */
    @Insert("<script>" +
            "insert into dim_cost(cost_date, pid, charge_model, source, book_cost, real_cost) values " +
            "(#{cost.costDate},#{cost.pid},#{cost.chargeModel},#{cost.source},#{cost.bookCost},#{cost.realCost}) " +
            "ON DUPLICATE KEY UPDATE " +
            "charge_model=VALUES(charge_model), source=VALUES(source), book_cost=VALUES(book_cost), real_cost=VALUES(real_cost)" +
            "</script>")
    int insertOne(@Param("cost") DimCost dimCost);
}
