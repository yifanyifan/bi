package com.stnts.bi.datamanagement.module.cooperation.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stnts.bi.datamanagement.module.cooperation.entity.Cooperation;
import com.stnts.bi.datamanagement.module.cooperation.entity.CooperationStatusSwitchHistory;
import com.stnts.bi.datamanagement.module.cooperation.mapper.CooperationMapper;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationService;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationStatusSwitchHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 合作伙伴汇总表 服务实现类
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-03
 */
@Service
public class CooperationServiceImpl extends ServiceImpl<CooperationMapper, Cooperation> implements CooperationService {

    private final CooperationStatusSwitchHistoryService cooperationStatusSwitchHistoryService;

    public CooperationServiceImpl(CooperationStatusSwitchHistoryService cooperationStatusSwitchHistoryService) {
        this.cooperationStatusSwitchHistoryService = cooperationStatusSwitchHistoryService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void switchStatus(Long id, Integer status, String remark, Integer userId, String userName) {
        Cooperation cooperation = new Cooperation();
        cooperation.setId(id);
        cooperation.setLastStatus(status);
        cooperation.setLastRemark(remark);
        super.updateById(cooperation);

        CooperationStatusSwitchHistory cooperationStatusSwitchHistory = new CooperationStatusSwitchHistory();
        cooperationStatusSwitchHistory.setCooperationId(id);
        cooperationStatusSwitchHistory.setStatus(status);
        cooperationStatusSwitchHistory.setRemark(remark);
        cooperationStatusSwitchHistory.setCreateUserId(StrUtil.toString(userId));
        cooperationStatusSwitchHistory.setCreateUserName(userName);
        cooperationStatusSwitchHistoryService.save(cooperationStatusSwitchHistory);
    }

}
