package com.stnts.bi.datamanagement.module.channel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.module.channel.entity.Channel;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelCooperation;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelPromotion;
import com.stnts.bi.datamanagement.module.channel.param.ChannelMediumPageParam;
import com.stnts.bi.datamanagement.module.channel.param.ChannelPromotionGeneral;
import com.stnts.bi.datamanagement.module.channel.param.ChannelPromotionPageParam;
import com.stnts.bi.datamanagement.module.channel.vo.*;
import com.stnts.bi.entity.common.PageEntity;
import com.stnts.bi.entity.sys.UserEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 渠道推广 服务类
 *
 * @author 刘天元
 * @since 2021-02-04
 */
public interface ChannelPromotionService extends IService<ChannelPromotion> {

    /**
     * 保存
     *
     * @param channelPromotion
     * @return
     * @throws Exception
     */
    Map<Object, Object> saveChannelPromotion(ChannelPromotion channelPromotion) throws Exception;

    /**
     * 获取根据计费别名获取数据库中最新编码
     *
     * @param pidAlias
     * @return
     */
    Integer getLastNum(String pidAlias);

    Map<String, Object> saveChannelPromotionGeneral(ChannelPromotion channelPromotion) throws Exception;

    /**
     * 保存
     *
     * @param channelPromotion
     * @return
     * @throws Exception
     */
    Boolean saveChannelPromotionBatch(List<ChannelPromotion> channelPromotionList, HttpServletRequest request) throws Exception;

    /**
     * 修改
     *
     * @param channelPromotion
     * @return
     * @throws Exception
     */
    boolean updateChannelPromotion(ChannelPromotion channelPromotion) throws Exception;

    /**
     * 删除
     *
     * @param id
     * @return
     * @throws Exception
     */
    boolean deleteChannelPromotion(Long id) throws Exception;

    PageEntity<ChannelPromotionVO> getPidPageList(ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest request) throws Exception;


    /**
     * 获取列表对象
     *
     * @param channelPromotionPageParam
     * @return
     * @throws Exception
     */
    List<ChannelPromotion> getChannelPromotionList(ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest request) throws Exception;

    /**
     * 获取对象列表ByZA
     *
     * @param channelPromotionPageParam
     * @return
     * @throws Exception
     */
    List<ChannelPromotionZaVO> getChannelPromotionListByZa(ChannelPromotionPageParam channelPromotionPageParam) throws Exception;

    /**
     * 通过ccid查找产品信息列表
     *
     * @param ccid
     * @return
     * @throws Exception
     */
    PageEntity<AppVO> getProductAndAppByCcid(ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest request) throws Exception;

    /**
     * 查找推广位详情
     *
     * @param id
     * @return
     * @throws Exception
     */
    ChannelPromotionVO info(Long id) throws Exception;


    ResultEntity<List<DepartmentVO>> listDepartment(ChannelPromotionPageParam channelPromotionPageParam);

    ResultEntity<List<AgentVO>> listCompany(ChannelPromotionPageParam channelPromotionPageParam);

    ResultEntity<List<ChannelVO>> listChannel(ChannelPromotionPageParam channelPromotionPageParam);

    ResultEntity<List<SubChannelVO>> listSubChannel(ChannelPromotionPageParam channelPromotionPageParam);

    ResultEntity<PidVO> getPidBusinessInfo(String pid);

    void migration(ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest request) throws Exception;

    Map<String, Object> searchList(ChannelPromotionPageParam param, HttpServletRequest request);

    List<Channel> migrationChannelList(ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest request);

    List<ChannelPromotionVO> getPidPageListToExcel(ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest request);

    boolean updateChannelPromotionSub(ChannelPromotion channelPromotion);

    boolean updateSubReplace(ChannelPromotion channelPromotion);

    String getNumByPidAlias(String pidAlias);

    List<ChannelCooperation> migrationCCIDList(ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest request);

    ChannelPromotionGeneral saveCCPGeneral(ChannelPromotionGeneral channelPromotionGeneral) throws Exception;

    List<ChannelPromotion> getChannelPromotionGeneral(ChannelPromotionPageParam channelPromotionPageParam);

    List<Map<String, String>> countPidByMedium(ChannelMediumPageParam channelMediumPageParam, UserEntity user);

    List<Channel> settlementChannelList(ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest httpServletRequest);

    List<ChannelCooperation> settlementCCIDList(ChannelPromotionPageParam channelPromotionPageParam);

    List<AppVO> getAppPageListNoPage(ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest request);

    List<Channel> settlementChannelListByBatch(ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest request);

    List<ChannelCooperation> settlementCCIDListByBatch(ChannelPromotionPageParam channelPromotionPageParam);

    Map settlementUpdateBatch(ChannelPromotionPageParam channelPromotionPageParam);

    Channel getChannelGeneral(Long channelId, String channelName, Long companyId, String departmentCode);
}
