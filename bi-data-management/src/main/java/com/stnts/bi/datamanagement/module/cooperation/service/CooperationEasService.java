package com.stnts.bi.datamanagement.module.cooperation.service;

import com.stnts.bi.datamanagement.module.cooperation.entity.CooperationBi;
import com.stnts.bi.datamanagement.module.cooperation.entity.CooperationEas;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 合作伙伴 源表（EAS金蝶） 服务类
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-03
 */
public interface CooperationEasService extends IService<CooperationEas> {
    void saveCooperation(CooperationEas cooperationEas);

    void updateCooperation(CooperationEas cooperationEas);
}
