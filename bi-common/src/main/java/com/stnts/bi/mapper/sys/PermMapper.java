package com.stnts.bi.mapper.sys;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.entity.sys.PermEntity;
import com.stnts.bi.vo.SimplePermVO;

/**
 * @author liang.zhang
 * @date 2020
 * @desc TODO
 */
@Mapper
public interface PermMapper extends BaseMapper<PermEntity>{
	
	@Select("select * from stbi_perm where perm_id not in (select distinct parent_perm_id from stbi_perm where perm_type = 1) and perm_type = 1")
	public List<PermEntity> selectListMenuForLeaf();
	
	/**
	 * 这里只有操作权限
	 * @param roleIds
	 * @return
	 */
	@Select("<script>"
			+ "select p.perm_id as id, p.perm_name as name, p.perm_code as code from stbi.stbi_perm p  "
			+ "where p.perm_id in (select distinct rp.perm_id from stbi.stbi_role_perm rp where rp.role_id in "
			+ "<foreach collection='roleIds' open='(' close=')' separator=',' item='roleId' index='i'>"
			+ "#{roleId}"
			+ "</foreach>"
			+ ")"
			+ "</script>")
	public List<SimplePermVO> selectListSimplePermByRoleIds(@Param("roleIds") List<Integer> roleIds);
	
	/**
	 * 这里包含所有权限
	 * @return
	 */
	@Select("select p.perm_id as id, p.perm_name as name, p.perm_code as code from stbi.stbi_perm p")
	public List<SimplePermVO> selectListSimplePerm();
}
