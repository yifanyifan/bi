package com.stnts.bi.mapper.sys;

import java.util.List;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.entity.sys.UserRoleEntity;

/**
 * @author liang.zhang
 * @date 2020
 * @desc TODO
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRoleEntity> {

    @Select("select * from stbi_user_role where user_id = #{userId}")
    @Results({
            @Result(column = "role_id", property = "roleId"),
            @Result(column = "product_ids", property = "productIds"),
            @Result(column = "product_names", property = "productNames"),
            @Result(column = "role_id", property = "roleName", one = @One(select = "com.stnts.bi.mapper.sys.RoleMapper.getRoleNameById", fetchType = FetchType.DEFAULT)),
            @Result(column = "product_ids", property = "products", many = @Many(select = "com.stnts.bi.mapper.sys.ProductMapper.getProductByIds", fetchType = FetchType.EAGER))
    })
    List<UserRoleEntity> getRoleListByUserId(Integer userId);

    @Delete("delete from stbi_user_role where user_id = #{userId}")
    int deleteByUserId(Integer userId);

    @Insert("<script>"
            + "insert into stbi_user_role(user_id, role_id, product_ids, product_names) values "
            + "<foreach collection='roles' item='role' index='role' separator=','>"
            + "(#{role.userId}, #{role.roleId}, #{role.productIds}, #{role.productNames})"
            + "</foreach>"
            + "</script>")
    int insertBatch(@Param("roles") List<UserRoleEntity> roles);

    @Update("<script>" +
            "<foreach collection='roles' item='role' open='' close='' separator=';'>" +
            "update stbi_user_role set product_ids = #{role.productIds}, product_names = #{role.productNames} where user_id = #{role.userId} and role_id = #{role.roleId}" +
            "</foreach>" +
            "</script>")
    int updateBatch(@Param("roles") List<UserRoleEntity> roles);
}
