package com.stnts.bi.datamanagement.module.cooperation.service;

import com.stnts.bi.datamanagement.module.cooperation.entity.Cooperation;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 合作伙伴汇总表 服务类
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-03
 */
public interface CooperationService extends IService<Cooperation> {

    void switchStatus(Long id, Integer status, String remark, Integer userId, String userName);

}
