package com.stnts.bi.datamanagement.module.cooperation.service;

import com.stnts.bi.datamanagement.module.cooperation.entity.CooperationCrm;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stnts.bi.datamanagement.module.cooperation.param.CooperationAddApiParam;

import java.util.Map;

/**
 * <p>
 * 合作伙伴（CRM） 服务类
 * </p>
 *
 * @author yifan
 * @since 2021-07-29
 */
public interface CooperationCrmService extends IService<CooperationCrm> {
    Map<String,String> addCompany(CooperationAddApiParam cooperationAddApiParam) throws Exception;

    void updateCompany(CooperationAddApiParam cooperationAddApiParam);

    void updateChannel(CooperationAddApiParam cooperationAddApiParam);
}
