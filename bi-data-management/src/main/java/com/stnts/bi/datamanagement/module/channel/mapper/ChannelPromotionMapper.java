package com.stnts.bi.datamanagement.module.channel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.datamanagement.module.business.entity.BusinessDict;
import com.stnts.bi.datamanagement.module.channel.entity.Channel;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelCooperation;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelProduct;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelPromotion;
import com.stnts.bi.datamanagement.module.channel.param.ChannelCooperationPageParam;
import com.stnts.bi.datamanagement.module.channel.param.ChannelMediumPageParam;
import com.stnts.bi.datamanagement.module.channel.param.ChannelProductPageParam;
import com.stnts.bi.datamanagement.module.channel.param.ChannelPromotionPageParam;
import com.stnts.bi.datamanagement.module.channel.vo.*;
import com.stnts.bi.entity.sys.UserEntity;
import com.stnts.bi.vo.DmVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 渠道推广 Mapper 接口
 *
 * @author 刘天元
 * @since 2021-02-04
 */
@Repository
public interface ChannelPromotionMapper extends BaseMapper<ChannelPromotion> {
    /**
     * 通过条件查询列表  多表关联
     *
     * @param page
     * @param params
     * @return
     */
    List<ChannelPromotionVO> getListByCond(@Param("page") Page<ChannelPromotion> page, @Param("params") ChannelPromotionPageParam params, @Param("user") UserEntity user);

    @Select("select count(distinct ccid) from dm_channel_promotion where product_code = #{productCode}")
    Long countCcid(long productId);

    List<ChannelCooperation> getCCIDListByProduct(@Param("params") ChannelProductPageParam params, @Param("user") UserEntity user);

    List<Map<String, String>> countCCID(@Param("productList") List<String> productCode, @Param("params") ChannelPromotionPageParam params, @Param("user") UserEntity user);

    List<Map<String, String>> countPid(@Param("productList") List<ChannelProduct> channelProductList, @Param("params") ChannelPromotionPageParam params, @Param("user") UserEntity user);

    @Select("select count(distinct pid) from dm_channel_promotion where ccid = #{ccid}")
    long countPidByCcid(String ccid);

    @Select("select count(distinct product_code, COALESCE(application_id,'NULL')) from dm_channel_promotion where ccid = #{ccid}")
    long countApp(String ccid);

    @Select("select count(pid) from dm_channel_promotion where sub_channel_id in " +
            "(select sub_channel_id from dm_channel_child where channel_id = #{channelId})")
    long countByChannelId(long channelId);

    List<AppVO> getProductAndAppByCcid(Page<AppVO> page, @Param("params") ChannelPromotionPageParam channelPromotionPageParam, @Param("user") UserEntity user);

    @Select("<script>" +
            "SELECT DISTINCT t.department_name, t.department_code FROM " +
            "( SELECT p.ccid, p.sub_channel_id, ch.sub_channel_name, c.department_code, c.department_name , c.agent_id, c.agent_name, c.channel_id, c.channel_name " +
            "FROM dm_channel_promotion p JOIN dm_channel_cooperation c ON p.ccid = c.ccid JOIN dm_channel_child ch ON p.sub_channel_id = ch.sub_channel_id ) t where 1=1 " +
            "<if test='null != agentId'>" +
            " and t.agent_id = #{agentId}" +
            "</if>" +
            "<if test='null != channelId'>" +
            " and t.channel_id = #{channelId}" +
            "</if>" +
            "<if test='null != subChannelId'>" +
            " and t.sub_channel_id = #{subChannelId}" +
            "</if>" +
            "</script>")
    List<DepartmentVO> listDepartment(ChannelPromotionPageParam params);

    @Select("<script>" +
            "SELECT DISTINCT t.agent_id, t.agent_name FROM " +
            "( SELECT p.ccid, p.sub_channel_id, ch.sub_channel_name, c.department_code, c.department_name , c.agent_id, c.agent_name, c.channel_id, c.channel_name " +
            "FROM dm_channel_promotion p JOIN dm_channel_cooperation c ON p.ccid = c.ccid JOIN dm_channel_child ch ON p.sub_channel_id = ch.sub_channel_id ) t where 1=1 " +
            "<if test='null != departmentCode'>" +
            " and t.department_code = #{departmentCode}" +
            "</if>" +
            "<if test='null != channelId'>" +
            " and t.channel_id = #{channelId}" +
            "</if>" +
            "<if test='null != subChannelId'>" +
            " and t.sub_channel_id = #{subChannelId}" +
            "</if>" +
            "</script>")
    List<AgentVO> listAgent(ChannelPromotionPageParam params);

    @Select("<script>" +
            "select p.pid, p.ccid, c.channel_id, p.sub_channel_id, p.pp_id as promoteId, pc.id as productId, p.create_time from dm_channel_promotion p " +
            "left join dm_channel_product pc on p.product_code = pc.product_code " +
            "left join dm_channel_child c on p.sub_channel_id = c.sub_channel_id where 1=1 " +
            "<if test='null != channelId and \"\" != channelId'>" +
            " and c.channel_id = #{channelId}" +
            "</if>" +
            "<if test='null != subChannelId and \"\" != subChannelId'>" +
            " and p.sub_channel_id = #{subChannelId}" +
            "</if>" +
            "<if test='null != promoteIdList and promoteIdList.size() > 0'>" +
            " and p.pp_id in " +
            "<foreach collection='promoteIdList' item='pidItem' open='(' close=')' separator=','>" +
            "#{pidItem}" +
            "</foreach>" +
            "</if>" +
            "</script>"
    )
    List<ChannelPromotionZaVO> getChannelPromotionListByZa(ChannelPromotionPageParam params);

    @Select("<script>" +
            "SELECT DISTINCT t.channel_id, t.channel_name FROM " +
            "( SELECT p.ccid, p.sub_channel_id, ch.sub_channel_name, c.department_code, c.department_name , c.agent_id, c.agent_name, c.channel_id, c.channel_name " +
            "FROM dm_channel_promotion p JOIN dm_channel_cooperation c ON p.ccid = c.ccid JOIN dm_channel_child ch ON p.sub_channel_id = ch.sub_channel_id ) t where 1=1 " +
            "<if test='null != departmentCode'>" +
            " and t.department_code = #{departmentCode}" +
            "</if>" +
            "<if test='null != agentId'>" +
            " and t.agent_id = #{agentId}" +
            "</if>" +
            "<if test='null != subChannelId'>" +
            " and t.sub_channel_id = #{subChannelId}" +
            "</if>" +
            "</script>")
    List<ChannelVO> listChannel(ChannelPromotionPageParam params);

    @Select("<script>" +
            "SELECT DISTINCT t.sub_channel_id, t.sub_channel_name FROM " +
            "( SELECT p.ccid, p.sub_channel_id, ch.sub_channel_name, c.department_code, c.department_name , c.agent_id, c.agent_name, c.channel_id, c.channel_name " +
            "FROM dm_channel_promotion p JOIN dm_channel_cooperation c ON p.ccid = c.ccid JOIN dm_channel_child ch ON p.sub_channel_id = ch.sub_channel_id ) t where 1=1 " +
            "<if test='null != departmentCode'>" +
            " and t.department_code = #{departmentCode}" +
            "</if>" +
            "<if test='null != agentId'>" +
            " and t.agent_id = #{agentId}" +
            "</if>" +
            "<if test='null != channelId'>" +
            " and t.channel_id = #{channelId}" +
            "</if>" +
            "</script>")
    List<SubChannelVO> listSubChannel(ChannelPromotionPageParam params);


    @Select("select count(distinct sub_channel_id) from dm_channel_promotion where ccid = #{ccid}")
    long countSubChannel(String ccid);

    /*@Select("<script>" +
            "SELECT c.ccid, c.department_code, c.department_name, c.agent_name, c.channel_name , p.userid, p.username, p.ccid FROM " +
            "( SELECT DISTINCT userid, username, ccid FROM dm_channel_promotion ) p " +
            "LEFT JOIN dm_channel_cooperation c ON c.ccid = p.ccid where 1=1 and c.department_code in " +
            "<foreach collection='departmentCodes' item='item' open='(' close=')' separator=','>" +
            "#{item}" +
            "</foreach>" +
            "<if test='null != keyword and keyword != \"\"'>" +
            "<bind name='key' value=\"'%' + keyword + '%'\" />" +
            " and (c.ccid like #{key} or c.agent_name like #{key} or c.channel_name like #{key} or p.username like #{key} or c.department_name like #{key})" +
            "</if>" +
            "</script>")*/
    @Select("<script>" +
            "SELECT c.ccid, ch.settlement_type, c.department_code, c.department_name, c.agent_name, c.channel_name , p.userid, p.username FROM " +
            "( SELECT DISTINCT userid, username, ccid FROM dm_channel_promotion ) p " +
            "LEFT JOIN dm_channel_cooperation c ON c.ccid = p.ccid " +
            "LEFT JOIN dm_channel ch ON c.channel_id = ch.channel_id " +
            "where 1=1 and c.department_code in " +
            "<foreach collection='departmentCodes' item='item' open='(' close=')' separator=','>" +
            "#{item}" +
            "</foreach>" +
            "<if test='null != keyword and keyword != \"\"'>" +
            "<bind name='key' value=\"'%' + keyword + '%'\" />" +
            " and (c.ccid like #{key} or c.agent_name like #{key} or c.channel_name like #{key} or p.username like #{key} or c.department_name like #{key})" +
            "</if>" +
            "</script>")
    List<DmVO> listDmVOList(@Param("keyword") String keyword, @Param("departmentCodes") List<String> departmentCodes);


    @Select("<script>select pid_alias from dm_channel_promotion where (pid_alias like '${pidAlias}\\_%' or pid_alias = #{pidAlias}) " +
            " and !(SUBSTR(pid_alias, char_length(#{pidAlias}) + 2) REGEXP '[^0-9]') " +
            " order by CAST(case when pid_alias = #{pidAlias} then '0' else SUBSTRING_INDEX(pid_alias,'_',-1) end as SIGNED) desc limit 1</script>")
    String getNumByPidAlias(@Param("pidAlias") String pidAlias);

    List<ChannelPromotionVO> searchList(@Param("params") ChannelPromotionPageParam param, @Param("user") UserEntity user);

    void updateApplicationIdToBlank(@Param("param") List<Long> applicationDeleteIdList);

    List<ChannelCooperation> countSubChannelBatch(@Param("params") ChannelCooperationPageParam params, @Param("user") UserEntity user);

    List<ChannelCooperation> countAppBatch(@Param("params") ChannelCooperationPageParam params, @Param("user") UserEntity user);

    List<ChannelCooperation> countPidByCcidBatch(@Param("params") ChannelCooperationPageParam channelCooperationPageParam, @Param("user") UserEntity user);

    List<ChannelCooperation> countPidHistoryByCcidBatch(@Param("params") ChannelCooperationPageParam channelCooperationPageParam, @Param("user") UserEntity user);

    List<ChannelPromotion> getChannelPromotionList(@Param("params") ChannelPromotionPageParam channelPromotionPageParam, @Param("user") UserEntity user);

    void updateByIdSql(@Param("params") ChannelPromotion channelPromotion);

    Long countByCCID(@Param("params") ChannelPromotionPageParam channelPromotionPageParam, @Param("user") UserEntity user);

    List<ChannelPromotion> getChannelPromotionGeneral(@Param("params") ChannelPromotionPageParam channelPromotionPageParam);

    Integer countByDict(@Param("params") BusinessDict businessDictDB);

    List<Map<String, String>> countPidByMedium(@Param("params") ChannelMediumPageParam channelMediumPageParam, @Param("user") UserEntity user);

    List<ChannelPromotion> getAssociatedToSubChannel(@Param("departmentCode") String departmentCode);

    List<Channel> settlementChannelList(@Param("params") ChannelPromotionPageParam channelPromotionPageParam);

    List<ChannelCooperation> settlementCCIDList(@Param("params") ChannelPromotionPageParam channelPromotionPageParam);

    void updateApplicationIdToBlankHistory(@Param("param") List<Long> applicationDeleteIdList);

    List<ChannelPromotion> selectSubReplace(@Param("param") ChannelPromotion channelPromotion);

    void updateSubReplace(@Param("param") ChannelPromotion channelPromotion);

    List<String> selectOkSettlement(@Param("params") ChannelPromotionPageParam channelPromotionPageParam);

    List<ChannelPromotion> checkByProDepartmentCode(@Param("params") ChannelCooperation channelCooperationParam);
}
