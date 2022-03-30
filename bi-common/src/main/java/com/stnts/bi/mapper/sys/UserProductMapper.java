package com.stnts.bi.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.entity.sys.ProductEntity;
import com.stnts.bi.entity.sys.UserProductEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/6/2
 */
public interface UserProductMapper extends BaseMapper<UserProductEntity> {

    @Delete("delete from stbi_user_product where user_id = #{userId}")
    int deleteByUserId(Integer userId);

    @Insert("<script>" +
            "insert into stbi_user_product(user_id, product_id) values " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.userId}, #{item.productId})" +
            "</foreach>" +
            "</script>")
    int insertBatch(List<UserProductEntity> list);


    @Delete("<script>" +
            "delete from stbi_user_product where product_id = #{productId} " +
            "<if test='null != userIds and userIds.size() > 0'>" +
            " and user_id in " +
            "<foreach collection='userIds' item='userId' open='(' close=')' separator=','>" +
            "#{userId}" +
            "</foreach>" +
            "</if>" +
            "</script>")
    int deleteBy(@Param("productId") Integer productId, @Param("userIds") List<Integer> userIds);

    @Select("select * from stbi_product where product_id in (select product_id from stbi_user_product where user_id = #{userId})")
    List<ProductEntity> listProductByUser(Integer userId);
}
