package com.stnts.bi.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.entity.sys.OrgEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/5/27
 */
@Mapper
public interface OrgMapper extends BaseMapper<OrgEntity> {

    @Select("select * from stbi_org order by updated_at desc")
    List<OrgEntity> list();

    @Update("update stbi_department set org_id = #{orgId} where id = #{id}")
    int bindOrg(int id, int orgId);

    @Select("select org_id, org_name from stbi_org where org_id in (select distinct org_id from stbi_user)")
    List<OrgEntity> all();

    /**
     * 只看绑定额组织
     * @param userId
     * @return
     */
    @Select("select o.* from stbi_org o where o.org_id in " +
            "(select uo.org_id from stbi_user_org uo where uo.user_id = #{userId})")
    List<OrgEntity> getOrgListByUserId(int userId);
}
