package com.stnts.bi.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.entity.sys.UserOrgEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;

import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/5/31
 */
public interface UserOrgMapper extends BaseMapper<UserOrgEntity> {

    @Delete("delete from stbi_user_org where user_id = #{userId}")
    int deleteByUserId(Integer userId);

    @Insert("<script>" +
            "insert into stbi_user_org(user_id, org_id) values " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.userId}, #{item.orgId})" +
            "</foreach>" +
            "</script>")
    int insertBatch(List<UserOrgEntity> list);
}
