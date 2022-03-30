package com.stnts.bi.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.entity.sys.OlapUserPermEntity;
import com.stnts.bi.entity.sys.UserEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/1/12
 */
@Mapper
public interface OlapUserPermMapper extends BaseMapper<OlapUserPermEntity> {

    @Insert("<script>" +
            "insert into stbi_user_perm_olap(user_id, perm_id) values " +
            "<foreach collection='userPermEntityList' item='item' separator=','>" +
            "(#{item.userId}, #{item.permId})" +
            "</foreach>" +
            "</script>")
    int insertBatch(@Param("userPermEntityList") List<OlapUserPermEntity> userPermEntityList);

    @Insert("<script>" +
            "insert ignore into stbi_user_perm_olap(user_id, perm_id) values " +
            "<foreach collection='userPermEntityList' item='item' separator=','>" +
            "(#{item.userId}, #{item.permId})" +
            "</foreach>" +
            "</script>")
    int insertBatchIgnore(@Param("userPermEntityList") List<OlapUserPermEntity> userPermEntityList);

    @Select("<script>" +
            "select user_id, cnname from stbi_user where user_id in " +
            "(select user_id from stbi_user_perm_olap where perm_id = #{permId})" +
            "</script>")
    List<UserEntity> listUserByPerm(String permId);
}
