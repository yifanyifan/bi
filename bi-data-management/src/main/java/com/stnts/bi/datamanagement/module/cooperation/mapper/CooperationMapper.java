package com.stnts.bi.datamanagement.module.cooperation.mapper;

import com.stnts.bi.datamanagement.module.cooperation.entity.Cooperation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.datamanagement.module.cooperation.param.CooperationAddApiParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 合作伙伴汇总表 Mapper 接口
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-03
 */
@Mapper
public interface CooperationMapper extends BaseMapper<Cooperation> {

    void updateCompanyName(@Param("params") CooperationAddApiParam cooperationAddApiParam);
}
