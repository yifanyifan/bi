package com.stnts.bi.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.entity.sys.UserDmEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;

import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/6/2
 */
public interface UserDmMapper extends BaseMapper<UserDmEntity> {

    @Delete("delete from stbi_user_dm where user_id = #{userId}")
    int deleteByUserId(Integer userId);

    @Insert("<script>" +
            "insert into stbi_user_dm(user_id, dm_id, dm_type, dm_pid) values " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.userId}, #{item.dmId}, #{item.dmType}, #{item.dmPid})" +
            "</foreach>" +
            "</script>")
    int insertBatch(List<UserDmEntity> list);
}
