package com.stnts.bi.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.entity.sys.OlapPermEntity;
import com.stnts.bi.vo.OlapPermSubVO;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;
import java.util.Set;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/1/11
 */
@Mapper
public interface OlapPermMapper extends BaseMapper<OlapPermEntity> {

    @Select("<script>" +
            "select perm_id, perm_name, perm_nickname, perm_type, parent_perm_id, status, olap_perm_id, order_num from stbi_perm_olap " +
            "where parent_perm_id in " +
            "<foreach collection='parentIds'  open='(' close=')' separator=',' item='permId' index='i'>" +
            "#{permId}" +
            "</foreach>" +
            "<if test = 'status != null'>" +
            " and status = #{status}" +
            "</if>" +
            " order by perm_type asc, order_num asc, perm_id asc" +
            "</script>")
    List<OlapPermEntity> list(List<String> parentIds, Integer status);

    @Select("<script>" +
            "select perm_id, perm_name, perm_nickname, perm_type, parent_perm_id, status, olap_perm_id, order_num from stbi_perm_olap " +
            "where perm_id in " +
            "<foreach collection='permList'  open='(' close=')' separator=',' item='permId' index='i'>" +
            "#{permId}" +
            "</foreach>" +
            "<if test = 'status != null'>" +
            " and status = #{status}" +
            "</if>" +
            " order by perm_type asc, order_num asc, perm_id asc" +
            "</script>")
    List<OlapPermEntity> selectByPermList(List<String> permList, Integer status);

    @Select("<script>" +
            "select perm_id, perm_name, perm_nickname, perm_type, order_num, status, olap_perm_id, order_num from stbi_perm_olap " +
            "where bi_perm_id in " +
            "<foreach collection='biPermList'  open='(' close=')' separator=',' item='permId' index='i'>" +
            "#{permId}" +
            "</foreach> and product_id in " +
            "<foreach collection='biProductList'  open='(' close=')' separator=',' item='productId' index='i'>" +
            "#{productId}" +
            "</foreach>" +
            " order by perm_type asc, order_num asc, perm_id asc" +
            "</script>")
    List<OlapPermEntity> listByBI(@Param("biPermList") Set<Integer> biPermList, @Param("biProductList") List<String> biProductList);

    @Insert("<script>" +
            "insert into stbi_perm_olap(perm_id, perm_name, parent_perm_id, perm_type, status, olap_perm_id, created_at) values" +
            "<foreach collection='olapPermEntityList' separator=',' item='perm' index='i'>" +
            "(#{perm.permId}, #{perm.permName}, #{perm.parentPermId}, #{perm.permType}, #{perm.status}, #{perm.olapPermId}, NOW()) " +
            "</foreach>" +
            " ON DUPLICATE KEY UPDATE " +
            "perm_name = VALUES(perm_name), status = VALUES(status)" +
            "</script>")
    int publish(@Param("olapPermEntityList") List<OlapPermEntity> olapPermEntityList);

    @Update("<script>" +
            "<foreach collection='vos' item='vo' open='' close='' separator=';'>" +
            "update stbi_perm_olap set perm_nickname = #{vo.permName}, order_num = #{vo.orderNum}, status = #{vo.status}, parent_perm_id = #{vo.parentPermId} where perm_id = #{vo.permId}" +
            "</foreach>" +
            "</script>")
    int updateBatch(@Param("vos") List<OlapPermSubVO> vos);

    /**
     * 这个为了查 关联用户
     * @param permIds
     * @return
     */
    @Select("<script>" +
            "select perm_id, perm_name, perm_nickname, perm_type, parent_perm_id, order_num, created_at from stbi_perm_olap " +
            "where status = 1 and perm_id in " +
            "<foreach collection='permList'  open='(' close=')' separator=',' item='permId' index='i'>" +
            "#{permId}" +
            "</foreach>" +
            " order by perm_type asc, order_num asc, perm_id asc" +
            "</script>")
    @Results({
            @Result(property = "permId", column = "perm_id"),
            @Result(property = "permName", column = "perm_name"),
            @Result(property = "parentPermId", column = "parent_perm_id"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "users", column = "perm_id", many = @Many(select = "com.stnts.bi.mapper.sys.OlapUserPermMapper.listUserByPerm", fetchType = FetchType.LAZY))
    })
    List<OlapPermEntity> selectByPermListSimple(@Param("permList") List<String> permIds);

    @Update("<script>" +
            "update stbi_perm_olap set status = 0 where perm_id = #{permId}" +
            "</script>")
    int updateStatus(String permId);

    @Insert("<script>" +
            "insert into stbi_perm_olap(perm_id, perm_name, parent_perm_id, perm_type, order_num, status, bi_perm_id, olap_perm_id, product_id, created_at) " +
            "values " +
            "<foreach collection='perms' item='perm' separator=',' index='i'>" +
            "(#{perm.permId}, #{perm.permName}, #{perm.parentPermId}, #{perm.permType}, #{perm.orderNum}, #{perm.status}, #{perm.biPermId}, #{perm.olapPermId}, #{perm.productId}, NOW()) " +
            "</foreach>" +
            " on duplicate key update perm_name = VALUES(perm_name)" +
            "</script>")
    int insertBatch(@Param("perms") List<OlapPermEntity> perms);

    @Select("<script>" +
            "select perm_id, perm_name, perm_nickname, perm_type, parent_perm_id, status, olap_perm_id, order_num from stbi_perm_olap " +
            "where status = 1 and perm_id in " +
            "(select distinct perm_id from stbi_user_perm_olap where 1=1 " +
            "<if test='null != userId'>" +
            "and user_id = #{userId} " +
            "</if>" +
            ")" +
            " order by parent_perm_id asc, perm_type asc, order_num asc, perm_id asc" +
            "</script>")
    List<OlapPermEntity> listValid(Integer userId);

    @Select("select perm_id, bi_perm_id, perm_name, status, order_num, product_id from stbi_perm_olap where parent_perm_id = -1")
    List<OlapPermSubVO> listPerm();
}
