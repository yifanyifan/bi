package com.stnts.bi.mapper.sys;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.entity.sys.DepartmentEntity;
import org.apache.ibatis.annotations.Select;

/**
 * @author liang.zhang
 * @date 2020年7月22日
 * @desc TODO
 */
@Mapper
public interface DepartmentMapper extends BaseMapper<DepartmentEntity>{

	@Insert("<script>"
			+ "insert into stbi_department(id, name, pid, code, pcode, created_at) values "
			+ "<foreach collection='departments' item='department' index='i' separator=','>"
			+ "(#{department.id},#{department.name},#{department.pid},#{department.code},#{department.pcode},NOW())"
			+ "</foreach> "
			+ "ON DUPLICATE KEY UPDATE "
			+ "name=VALUES(name), pid=VALUES(pid), code=VALUES(code), pcode=VALUES(pcode)"
			+ "</script>")
	public int insertDepartments(@Param("departments") List<DepartmentEntity> departments);

	@Select("<script>" +
			"select d.*, o.org_name from (select * from stbi_department where pid = 1) d left join stbi_org o on d.org_id = o.org_id" +
			"<if test='null != keyword and keyword != \"\"'>" +
			"<bind name='key' value=\"'%' + keyword + '%'\" />" +
			" where d.name like #{key} or o.org_name like #{key}" +
			"</if>" +
			" order by d.id asc" +
			"</script>")
	List<DepartmentEntity> listDepartment(String keyword, Page<DepartmentEntity> page);
}
