package com.stnts.bi.datamanagement.module.business.mapper;

import com.stnts.bi.datamanagement.module.business.entity.BusinessDict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.datamanagement.module.exportdata.param.ExportDataParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 业务分类 Mapper 接口
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-08
 */
@Mapper
public interface BusinessDictMapper extends BaseMapper<BusinessDict> {

    List<BusinessDict> selectListByDict(@Param("paramList") List<ExportDataParam> paramList);
}
