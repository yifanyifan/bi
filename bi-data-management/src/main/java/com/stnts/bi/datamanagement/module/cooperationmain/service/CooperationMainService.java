package com.stnts.bi.datamanagement.module.cooperationmain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stnts.bi.datamanagement.module.cooperationmain.entity.CooperationMain;
import com.stnts.bi.datamanagement.module.cooperationmain.param.CooperationMainPageParam;
import com.stnts.bi.entity.common.PageEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 公司主体 服务类
 *
 * @author 易樊
 * @since 2021-09-17
 */
public interface CooperationMainService extends IService<CooperationMain> {

    /**
     * 保存
     *
     * @param cooperationMain
     * @return
     * @throws Exception
     */
    boolean saveCooperationMain(CooperationMain cooperationMain, HttpServletRequest request) throws Exception;

    /**
     * 修改
     *
     * @param cooperationMain
     * @return
     * @throws Exception
     */
    boolean updateCooperationMain(CooperationMain cooperationMain) throws Exception;

    /**
     * 删除
     *
     * @param id
     * @return
     * @throws Exception
     */
    boolean deleteCooperationMain(Long id) throws Exception;


    /**
     * 获取分页对象
     *
     * @param cooperationMainPageParam
     * @return
     * @throws Exception
     */
    PageEntity<CooperationMain> getCooperationMainPageList(CooperationMainPageParam cooperationMainPageParam, HttpServletRequest request) throws Exception;

    /**
     * 获取列表对象
     *
     * @param cooperationMainPageParam
     * @return
     * @throws Exception
     */
    List<CooperationMain> getCooperationMainList(CooperationMainPageParam cooperationMainPageParam, HttpServletRequest request) throws Exception;

    List<CooperationMain> getCooperationMainListGeneral(CooperationMainPageParam cooperationMainPageParam) throws Exception;

    Map<String, Object> searchList(String departmentCode, String cooperationMainId, HttpServletRequest request) throws Exception;
}
