package com.stnts.bi.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.entity.sys.OlapLogOpEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author liang.zhang
 * @date 2020
 * @desc TODO
 */
@Mapper
public interface OlapLogOpMapper extends BaseMapper<OlapLogOpEntity>{

	@Select("<script>"
			+ "select l.*, u.cnname from stbi_olap_log_op l left join stbi_user u on l.created_by = u.user_id where 1=1 "
			+ "<if test='null != cnname and cnname != \"\"'>"
			+ " and u.cnname like #{cnname}"
			+ "</if>"
			+ "<if test='null != beginDate and null != endDate'>"
			+ " and l.created_at BETWEEN #{beginDate} and #{endDate} "
			+ "</if>"
			+ "<if test='null != cond and cond != \"\"'>"
			+ " and (l.req_url like #{cond} or l.log_type like #{cond} or l.log_ip like #{cond})"
			+ "</if> "
			+ " order by l.created_at desc" 
			+ "</script>")
	List<OlapLogOpEntity> findLogListByCond(@Param("page") Page<OlapLogOpEntity> page, @Param("cond") String cond, @Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("cnname") String cnname);
}
