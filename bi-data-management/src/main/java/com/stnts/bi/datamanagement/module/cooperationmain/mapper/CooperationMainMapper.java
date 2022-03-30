package com.stnts.bi.datamanagement.module.cooperationmain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.datamanagement.module.cooperationmain.entity.CooperationMain;
import com.stnts.bi.datamanagement.module.cooperationmain.param.CooperationMainPageParam;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import java.io.Serializable;

/**
 * 公司主体 Mapper 接口
 *
 * @author 易樊
 * @since 2021-09-17
 */
@Repository
public interface CooperationMainMapper extends BaseMapper<CooperationMain> {


}
