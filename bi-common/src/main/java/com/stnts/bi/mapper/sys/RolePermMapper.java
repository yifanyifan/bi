package com.stnts.bi.mapper.sys;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.entity.sys.RolePermEntity;

/**
 * @author liang.zhang
 * @date 2020
 * @desc TODO
 */
@Mapper
public interface RolePermMapper extends BaseMapper<RolePermEntity>{
	
	@Delete("<script>"
			+ "delete from stbi_role_perm where role_id = #{roleId} and perm_id in "
			+ "<foreach collection='permIds' open='(' close=')' separator=',' item='permId' index='i'>"
			+ "#{permId}"
			+ "</foreach>"
			+ "</script>")
	public int deleteByPermIds(Integer roleId, List<Integer> permIds);
	
	@Insert("<script>"
			+ "insert into stbi_role_perm(role_id, perm_id) values "
			+ "<foreach collection='rolePerms' item='rolePerm' index='i' separator=','>"
			+ "("
			+ "#{rolePerm.roleId},"
			+ "#{rolePerm.permId}"
			+ ")"
			+ "</foreach>"
			+ "</script>")
	public int insertBatch(@Param("rolePerms") List<RolePermEntity> rolePerms);
}
