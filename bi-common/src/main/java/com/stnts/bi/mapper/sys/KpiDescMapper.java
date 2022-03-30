package com.stnts.bi.mapper.sys;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.entity.sys.KpiDescEntity;


@Mapper
public interface KpiDescMapper extends BaseMapper<KpiDescEntity>{

	@Select("<script>"
			+ "select k.*, u.cnname from stbi_kpi_desc k left join stbi_user u on k.created_by = u.user_id where 1=1 "
			+ "<if test = 'name != null and name != \"\"'>"
			+ "and (k.kpi_key like #{name} or k.kpi_name like #{name} or k.kpi_desc like #{name} or u.cnname like #{name} or k.kpi_comment like #{name})"
			+ "</if> "
			+ "order by created_at desc"
			+ "</script>")
	public List<KpiDescEntity> findKpiListByName(@Param("page") Page<KpiDescEntity> page, @Param("name") String name);
	
//	@Insert("insert into stbi_kpi_desc(kpi_key, kpi_desc, kpi_comment, created_by, created_at) "
//			+ "value(#{kpiDesc.kpiKey}, #{kpiDesc.kpiDesc}, #{kpiDesc.kpiComment}, #{createdBy}, NOW())")
//	public Long insertOne(@Param("kpiDesc") KpiDescEntity kpiDesc);
	
	@Select("select k.*, u.cnname from stbi_kpi_desc k left join stbi_user u on k.created_by = u.user_id where k.kpi_id = #{id}")
	public KpiDescEntity findKpiById(@Param("id") Integer id);
	
	@Select("select kpi_key, kpi_desc from stbi_kpi_desc order by kpi_key asc")
	public List<KpiDescEntity> all();
}
