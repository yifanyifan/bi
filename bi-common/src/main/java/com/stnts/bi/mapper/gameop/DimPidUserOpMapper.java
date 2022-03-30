package com.stnts.bi.mapper.gameop;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.entity.gameop.DimPidUserOp;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author chenchen
 * @since 2021/11/4.
 */
@Mapper
public interface DimPidUserOpMapper extends BaseMapper<DimPidUserOp> {

    @Insert("<script>" +
            "insert into dim_pid_user_op(pid, game_code, game_name, channel_id, channel_name, user_id, user_name) values " +
            "(#{pidUser.pid},#{pidUser.gameCode},#{pidUser.gameName},#{pidUser.channelId},#{pidUser.channelName},#{pidUser.userId},#{pidUser.userName}) " +
            "ON DUPLICATE KEY UPDATE " +
            "pid=VALUES(pid), user_id=VALUES(user_id)" +
            "</script>")
    int insertOne(@Param("pidUser") DimPidUserOp dimPidUserOp);

    @Update("<script>" +
            "update dim_pid_user_op set game_code = #{pidUser.gameCode}, game_name = #{pidUser.gameName}, channel_id = #{pidUser.channelId}, channel_name = #{pidUser.channelName}, user_name = #{pidUser.userName} where pid = #{pidUser.gameCode} and user_id = #{pidUser.userId}" +
            "</script>")
    int updateOne(@Param("pidUser") DimPidUserOp dimPidUserOp);
}
