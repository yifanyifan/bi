package com.stnts.bi.datamanagement.module.cooperation.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.cooperation.entity.Cooperation;
import com.stnts.bi.datamanagement.module.cooperation.entity.CooperationBi;
import com.stnts.bi.datamanagement.module.cooperation.entity.CooperationBiHistory;
import com.stnts.bi.datamanagement.module.cooperation.entity.CooperationEasHistory;
import com.stnts.bi.datamanagement.module.cooperation.mapper.CooperationEasHistoryMapper;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationBiHistoryService;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationEasHistoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationService;
import com.stnts.bi.datamanagement.util.CompareMapUtil;
import com.stnts.bi.datamanagement.util.DozerUtil;
import com.stnts.bi.datamanagement.util.JacksonUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * eas合作伙伴信息变更记录 服务实现类
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-13
 */
@Service
public class CooperationEasHistoryServiceImpl extends ServiceImpl<CooperationEasHistoryMapper, CooperationEasHistory> implements CooperationEasHistoryService {



}
