package com.stnts.bi.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.entity.sys.SdkDataLevelEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/5/28
 */
@Mapper
public interface SdkDataLevelMapper extends BaseMapper<SdkDataLevelEntity> {

    @Update("<script>" +
            "<foreach collection='levels' item='level' open='' close='' separator=';'>" +
            "update stbi_sdk_data_level set pid = #{level.pid}, path = #{level.path}, idx = #{level.idx} where level_id = #{level.levelId}" +
            "</foreach>" +
            "</script>")
    int updateBatch(@Param("levels") List<SdkDataLevelEntity> levels);
}
