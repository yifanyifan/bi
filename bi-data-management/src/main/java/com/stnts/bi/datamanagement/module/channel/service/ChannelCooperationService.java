package com.stnts.bi.datamanagement.module.channel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.module.business.entity.BusinessDict;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelCooperation;
import com.stnts.bi.datamanagement.module.channel.param.ChannelCooperationPageParam;
import com.stnts.bi.datamanagement.module.channel.vo.*;
import com.stnts.bi.datamanagement.module.exportdata.param.ExportDataParam;
import com.stnts.bi.entity.common.PageEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 渠道合作 服务类
 *
 * @author 刘天元
 * @since 2021-02-03
 */
public interface ChannelCooperationService extends IService<ChannelCooperation> {

    /**
     * 保存
     *
     * @param channelCooperation
     * @return
     * @throws Exception
     */
    String saveChannelCooperation(ChannelCooperation channelCooperation, HttpServletRequest request) throws Exception;

    /**
     * 修改
     *
     * @param channelCooperation
     * @return
     * @throws Exception
     */
    boolean updateChannelCooperation(ChannelCooperation channelCooperation, HttpServletRequest request) throws Exception;

    /**
     * 删除
     *
     * @param id
     * @return
     * @throws Exception
     */
    boolean deleteChannelCooperation(Long id) throws Exception;

    /**
     * @param ccid
     * @param request
     * @return
     * @throws Exception
     */
    ChannelCooperation info(String ccid, boolean isCheck, HttpServletRequest request) throws Exception;


    /**
     * 获取分页对象
     *
     * @param channelCooperationPageParam
     * @return
     * @throws Exception
     */
    PageEntity<ChannelCooperation> getChannelCooperationPageList(ChannelCooperationPageParam channelCooperationPageParam, HttpServletRequest request) throws Exception;

    PageEntity<GetAgentVO> getAgentPageList(ChannelCooperationPageParam channelCooperationPageParam) throws Exception;

    List<GetChannelVO> getChannelList(Long agentId, String departmentCode) throws Exception;

    /**
     * 获取列表对象
     *
     * @param channelCooperationPageParam
     * @return
     * @throws Exception
     */
    List<ChannelCooperation> getChannelCooperationList(ChannelCooperationPageParam channelCooperationPageParam, HttpServletRequest request) throws Exception;

    PageEntity<ChannelCooperation> getChannelCooperationPageListExt(ChannelCooperationPageParam channelCooperationPageParam, HttpServletRequest request) throws Exception;

    /**
     * 搜索条件中部门列表
     *
     * @return
     */
    ResultEntity<List<DepartmentVO>> listDepartment(ChannelCooperationPageParam channelCooperationPageParam);

    ResultEntity<List<AgentVO>> listAgent(ChannelCooperationPageParam channelCooperationPageParam);

    ResultEntity<List<ChannelVO>> listChannel(ChannelCooperationPageParam channelCooperationPageParam);

    ChannelCooperation getWithId(Long id, HttpServletRequest request);

    List<ChannelCooperation> selectListByExcel(List<ExportDataParam> channelCooperationByExcel);

    Map<String, Object> searchAll(ChannelCooperationPageParam param, HttpServletRequest request);

    ChannelCooperation saveChannelCooperationGeneral(ChannelCooperation channelCooperation) throws Exception;

    ChannelCooperation getOneCCID(ChannelCooperation channelCooperation, String chargeRule);

    List<ChannelCooperation> getChannelCooperationGeneral(ChannelCooperation channelCooperation);

    void updateBusinessDictBatch(ChannelCooperationPageParam channelCooperationPageParam, HttpServletRequest request);

    BusinessDict handlerBusinessDictId(Integer businessDictId, String departmentCode, String firstLevelBusiness, String secondLevelBusiness, String thirdLevelBusiness);

    Map<String, String> getDepartmentCodeOnly(ChannelCooperationPageParam channelCooperationPageParam);

    void updateDict(BusinessDict businessDict);
}
