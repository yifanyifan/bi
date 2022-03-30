package com.stnts.bi.mapper.gameop;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.entity.gameop.DimPidUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/8/24
 */
@Mapper
public interface DimPidUserMapper extends BaseMapper<DimPidUser> {

    /**
     * 插入或新增
     * @param dimPidUser
     * @return
     */
    @Insert("<script>" +
            "insert into dim_pid_user(id, user_id, cnname, game_names, channel_names, pids) values (#{dim.id}, #{dim.userId}, #{dim.cnname}, #{dim.gameNames}, #{dim.channelNames}, #{dim.pids}) " +
            "on duplicate key update game_names=#{dim.gameNames}, channel_names=#{dim.channelNames}, pids=#{dim.pids}" +
            "</script>")
    int insertNew(@Param("dim") DimPidUser dimPidUser);
}
