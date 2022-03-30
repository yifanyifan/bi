package com.stnts.bi.datamanagement.module.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.datamanagement.module.business.entity.BusinessCheck;
import com.stnts.bi.datamanagement.module.business.entity.BusinessDict;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * <p>
 * 业务考核 Mapper 接口
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-08
 */
@Mapper
public interface BusinessCheckMapper extends BaseMapper<BusinessCheck> {

    void updateDict(@Param("params") BusinessDict businessDict);
}
