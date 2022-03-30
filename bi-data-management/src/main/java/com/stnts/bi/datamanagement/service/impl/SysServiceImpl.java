package com.stnts.bi.datamanagement.service.impl;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelPromotionMapper;
import com.stnts.bi.datamanagement.service.SysService;
import com.stnts.bi.vo.DmVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/6/2
 */
@Service
public class SysServiceImpl implements SysService {

    @Autowired
    private ChannelPromotionMapper channelPromotionMapper;

    @Override
    public ResultEntity<List<DmVO>> listDmVOList(String keyword, List<String> departmentCodes) {
        try {

            List<DmVO> dmVOList = channelPromotionMapper.listDmVOList(keyword, departmentCodes);
            return ResultEntity.success(dmVOList);
        } catch (Exception e) {
            throw new BusinessException("系统管理查询接口异常, 异常信息: " + e.getMessage());
        }
    }
}
