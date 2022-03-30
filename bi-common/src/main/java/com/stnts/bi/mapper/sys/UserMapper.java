package com.stnts.bi.mapper.sys;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.FetchType;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.entity.sys.UserEntity;

/**
 * @author liang.zhang
 * @date 2020
 * @desc TODO
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {


    @Select("<script>"
            + "SELECT u.*, GROUP_CONCAT(r.role_id) AS role_id, GROUP_CONCAT(r.product_ids) AS product_ids FROM stbi_user_view u LEFT JOIN stbi_user_role r ON u.user_id = r.user_id GROUP BY u.user_id having 1=1 "
            + "<if test='userId != null'>"
            + " and u.user_id = #{userId} "
            + "</if>"
            + "<if test='roleIds != null and roleIds.size() > 0'>"
            + " and "
            + "<foreach collection='roleIds' item='roleId' open='(' close=')' separator='or'>"
            + "FIND_IN_SET(#{roleId},role_id) > 0"
            + "</foreach>"
            + "</if>"
            + "<if test='productIds != null and productIds.size() > 0'>"
            + " and (FIND_IN_SET('-9',product_ids) > 0 or "
            + "<foreach collection='productIds' item='productId' open='(' close=')' separator='or'>"
            + "FIND_IN_SET(#{productId},product_ids) > 0"
            + "</foreach>"
            + ")</if>"
            + "</script>")
    @Results({
            @Result(id = true, column = "user_id", property = "id"),
            @Result(column = "cnname", property = "cnname"),
            @Result(column = "mobile", property = "mobile"),
            @Result(column = "card_number", property = "cardNumber"),
            @Result(column = "email", property = "email"),
            @Result(column = "person_id", property = "personId"),
            @Result(column = "code", property = "code"),
            @Result(column = "user_id", property = "roles", many = @Many(select = "com.stnts.bi.mapper.sys.UserRoleMapper.getRoleListByUserId", fetchType = FetchType.LAZY))
    })
    List<UserEntity> findUserListByName(@Param("page") Page<UserEntity> page, @Param("userId") Integer userId, @Param("roleIds") List<Integer> roleIds, @Param("productIds") List<Integer> productIds);

    @Select("<script>"
            + "select u.*, t.role_id, t.org_ids, t.product_ids from stbi_user_view u left join (select u.user_id, GROUP_CONCAT(r.role_id) AS role_id, GROUP_CONCAT(p.product_id) AS product_ids, GROUP_CONCAT(o.org_id) as org_ids from stbi_user_view u LEFT JOIN stbi_user_role r ON u.user_id = r.user_id LEFT JOIN stbi_user_org o ON u.user_id = o.user_id LEFT JOIN stbi_user_product p on u.user_id = p.user_id group by u.user_id) t on u.user_id = t.user_id where 1=1 "
            + "<if test='userId != null'>"
            + " and u.user_id = #{userId} "
            + "</if>"
            + "<if test='roleIds != null and roleIds.size() > 0'>"
            + " and "
            + "<foreach collection='roleIds' item='roleId' open='(' close=')' separator='or'>"
            + "FIND_IN_SET(#{roleId},role_id) > 0"
            + "</foreach>"
            + "</if>"
            + "<if test='productIds != null and productIds.size() > 0'>"
            + " and (FIND_IN_SET('-9',product_ids) > 0 or "
            + "<foreach collection='productIds' item='productId' open='(' close=')' separator='or'>"
            + "FIND_IN_SET(#{productId},product_ids) > 0"
            + "</foreach>"
            + ")</if>" +
            "<if test='null != departmentCode'>" +
            " and code = #{departmentCode}" +
            "</if>" +
            "<if test='null != orgId'>" +
            " and (org_id = #{orgId} or FIND_IN_SET(#{orgId},org_ids) > 0)" +
            "</if>"
            + "</script>")
    @Results({
            @Result(id = true, column = "user_id", property = "id"),
            @Result(column = "cnname", property = "cnname"),
            @Result(column = "mobile", property = "mobile"),
            @Result(column = "card_number", property = "cardNumber"),
            @Result(column = "email", property = "email"),
            @Result(column = "person_id", property = "personId"),
            @Result(column = "code", property = "code"),
            @Result(column = "user_id", property = "roles", many = @Many(select = "com.stnts.bi.mapper.sys.UserRoleMapper.getRoleListByUserId", fetchType = FetchType.LAZY)),
            @Result(column = "user_id", property = "orgs", many = @Many(select = "com.stnts.bi.mapper.sys.OrgMapper.getOrgListByUserId", fetchType = FetchType.LAZY)),
            @Result(column = "user_id", property = "products", many = @Many(select = "com.stnts.bi.mapper.sys.UserProductMapper.listProductByUser", fetchType = FetchType.LAZY))
    })
    List<UserEntity> findUserListBySearch(@Param("page") Page<UserEntity> page, @Param("userId") Integer userId, @Param("roleIds") List<Integer> roleIds, @Param("departmentCode") String departmentCode, @Param("orgId") Integer orgId, @Param("productIds") List<Integer> productIds);

    @Insert("<script>"
            + "insert into stbi_user(user_id, cnname, oa_status, mobile, card_number, email, person_id, code) values "
            + "<foreach collection='users' item='user' index='i' separator=','>"
            + "(#{user.id},#{user.cnname},#{user.oaStatus},#{user.mobile},#{user.cardNumber},#{user.email},#{user.personId},#{user.code})"
            + "</foreach> "
            + "ON DUPLICATE KEY UPDATE "
            + "cnname=VALUES(cnname), oa_status=VALUES(oa_status), mobile=VALUES(mobile), card_number=VALUES(card_number), email=VALUES(email), person_id=VALUES(person_id), code=VALUES(code)"
            + "</script>")
    int insertUsers(@Param("users") List<UserEntity> users);

    @Select("select * from stbi_user_view where user_id = #{userId}")
    @Results({
            @Result(id = true, column = "user_id", property = "id"),
            @Result(column = "cnname", property = "cnname"),
            @Result(column = "mobile", property = "mobile"),
            @Result(column = "card_number", property = "cardNumber"),
            @Result(column = "email", property = "email"),
            @Result(column = "person_id", property = "personId"),
            @Result(column = "is_admin", property = "admin"),
            @Result(column = "code", property = "code"),
            @Result(column = "department_name", property = "departmentName"),
            @Result(column = "user_id", property = "roles", many = @Many(select = "com.stnts.bi.mapper.sys.UserRoleMapper.getRoleListByUserId", fetchType = FetchType.LAZY)),
            @Result(column = "user_id", property = "orgs", many = @Many(select = "com.stnts.bi.mapper.sys.OrgMapper.getOrgListByUserId", fetchType = FetchType.LAZY))
    })
    UserEntity findUserById(@Param("userId") Integer userId);
}
