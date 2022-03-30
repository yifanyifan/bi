package com.stnts.bi.datamanagement.module.channel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.datamanagement.module.business.entity.BusinessDict;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelApplication;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelProduct;
import com.stnts.bi.datamanagement.module.channel.param.ChannelProductPageParam;
import com.stnts.bi.datamanagement.module.cooperationmain.entity.CooperationMain;
import com.stnts.bi.datamanagement.module.exportdata.param.ExportDataParam;
import com.stnts.bi.entity.sys.UserEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 产品信息 Mapper 接口
 *
 * @author 刘天元
 * @since 2021-02-04
 */
@Repository
public interface ChannelProductMapper extends BaseMapper<ChannelProduct> {
    // 供通用接口适用，无权限
    List<ChannelProduct> listProduct(Page<ChannelProduct> page, @Param("params") ChannelProductPageParam params);

    // 供页面使用，有权限及排序
    List<ChannelProduct> listProductToOrder(Page<ChannelProduct> page, @Param("params") ChannelProductPageParam params, @Param("user") UserEntity user);

    @Update("update dm_channel_product set cooperation_main_name = #{cooperationMainName}, " +
            "product_name = #{productName}, department_code = #{departmentCode}, " +
            "department_name = #{departmentName}, userid = #{userid}, " +
            "username = #{username} where product_code = #{productCode}")
    int updateProduct(ChannelProduct channelProduct);

    @Select("<script>" +
            "select * from dm_channel_product where 1 = 1 " +
            "<if test='null != params.cooperationMainName and \"\" != params.cooperationMainName'>" +
            " and cooperation_main_name = #{params.cooperationMainName}" +
            "</if>" +
            "<if test='null != params.departmentCode and \"\" != params.departmentCode'>" +
            " and department_code = #{params.departmentCode}" +
            "</if>" +
            "<if test='null != params.keyword and params.keyword != \"\"'>" +
            "<bind name='key' value=\"'%' + params.keyword + '%'\" />" +
            " and (department_code like #{key} or department_name like #{key} or cooperation_main_id like #{key} or cooperation_main_name like #{key} or product_name like #{key} or product_code like #{key} or username = #{key})" +
            "</if>" +
            " order by product_code, update_time desc" +
            "</script>")
    List<ChannelProduct> listProductToDept(@Param("params") ChannelProductPageParam params);

    List<ChannelApplication> selectListByProduct(@Param("paramList") List<ExportDataParam> productApplicationByExcel);

    List<ChannelProduct> selectListProduct(@Param("params") ChannelProductPageParam channelProductPageParam);

    List<ChannelProduct> selectListByAppId(@Param("params") String applicationId);

    ChannelProduct getOneByParam(@Param("departmentCode") String departmentCode, @Param("productCode") String productCode);

    List<ChannelProduct> getOneByParamList(@Param("departmentCode") String departmentCode, @Param("productCodeList") List<String> productCode);

    String getDeleteSaleDepartCount(@Param("channelProductDB") ChannelProduct channelProductDB, @Param("saleDepartmentCodeDBList") List<String> saleDepartmentCodeDBList);

    void updateMain(@Param("params") CooperationMain cooperationMain);

    void updateDict(@Param("params") BusinessDict businessDict);
}
