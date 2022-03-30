package com.stnts.bi.mapper.sys;

import org.apache.ibatis.annotations.*;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.entity.sys.RoleEntity;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

/**
 * @author liang.zhang
 * @date 2020
 * @desc TODO
 */
@Mapper
public interface RoleMapper extends BaseMapper<RoleEntity>{

	@Select("select * from stbi_role")
	@Results({
			@Result(id = true, property = "id", column = "role_id"),
			@Result(property = "name", column = "role_name"),
			@Result(property = "roleDesc", column = "role_desc"),
			@Result(property = "status", column = "status"),
			@Result(property = "createdAt", column = "created_at"),
			@Result(property = "updatedAt", column = "updated_at"),
			@Result(property = "coverUserNum", column = "role_id", one = @One(select = "com.stnts.bi.mapper.sys.RoleMapper.coverUserNum", fetchType = FetchType.LAZY))
	})
	List<RoleEntity> selectRoles();

	@Select("select count(1) from stbi_user_role where role_id = #{roleId}")
	Integer coverUserNum(Integer roleId);

	@Select("select role_name from stbi_role where role_id = #{roleId}")
	String getRoleNameById(Integer roleId);
}
