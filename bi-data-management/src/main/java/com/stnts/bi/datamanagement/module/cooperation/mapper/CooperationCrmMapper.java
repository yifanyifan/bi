package com.stnts.bi.datamanagement.module.cooperation.mapper;

import com.stnts.bi.datamanagement.module.cooperation.entity.CooperationCrm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.datamanagement.module.cooperation.param.CooperationAddApiParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 合作伙伴（CRM） Mapper 接口
 * </p>
 *
 * @author yifan
 * @since 2021-07-29
 */
@Repository
public interface CooperationCrmMapper extends BaseMapper<CooperationCrm> {

    void updateCompanyName(@Param("params") CooperationAddApiParam cooperationAddApiParam);
}
