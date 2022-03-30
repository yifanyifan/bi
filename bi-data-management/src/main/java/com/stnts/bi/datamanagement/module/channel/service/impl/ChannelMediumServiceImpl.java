package com.stnts.bi.datamanagement.module.channel.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stnts.bi.common.BiSessionUtil;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.business.service.BusinessDictService;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelMedium;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelPromotion;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelMediumMapper;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelPromotionMapper;
import com.stnts.bi.datamanagement.module.channel.param.ChannelMediumPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelMediumService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelPromotionAllService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelPromotionService;
import com.stnts.bi.datamanagement.module.channel.vo.DepartmentVO;
import com.stnts.bi.entity.common.PageEntity;
import com.stnts.bi.entity.sys.UserDmEntity;
import com.stnts.bi.entity.sys.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 媒介信息 服务实现类
 *
 * @author 刘天元
 * @since 2021-02-04
 */
@Slf4j
@Service
public class ChannelMediumServiceImpl extends ServiceImpl<ChannelMediumMapper, ChannelMedium> implements ChannelMediumService {
    @Autowired
    private ChannelMediumMapper channelMediumMapper;
    @Autowired
    private ChannelPromotionService channelPromotionService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private BusinessDictService businessDictService;
    @Autowired
    private ChannelPromotionAllService channelPromotionAllService;
    @Autowired
    private ChannelPromotionMapper channelPromotionMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ChannelMedium saveChannelMedium(ChannelMedium channelMedium) throws Exception {
        try {
            super.save(channelMedium);

            return channelMedium;
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException) {
                throw new BusinessException("同一部门下媒介名称必须唯一");
            } else {
                throw e;
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateChannelMedium(ChannelMedium channelMedium) throws Exception {
        try {
            ChannelMedium channelMediumDB = channelMediumMapper.selectOne(new LambdaQueryWrapper<ChannelMedium>().eq(ChannelMedium::getId, channelMedium.getId()));
            if (!channelMediumDB.getDepartmentCode().equals(channelMedium.getDepartmentCode())) {
                Integer count = channelPromotionService.count(new LambdaQueryWrapper<ChannelPromotion>().eq(ChannelPromotion::getMediumId, channelMedium.getId()));
                if (count > 0) {
                    throw new BusinessException("媒介信息被PID使用，不能变更部门");
                }
            }

            super.updateById(channelMedium);

            //存宽表
            channelPromotionAllService.updateChannelMediumThread(channelMedium);

            return true;
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException) {
                throw new BusinessException("同一部门下媒介名称必须唯一");
            } else {
                throw e;
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteChannelMedium(Long id) throws Exception {

        List<ChannelPromotion> list = channelPromotionService.list(new QueryWrapper<ChannelPromotion>().select("id")
                .lambda().eq(ChannelPromotion::getMediumId, id));
        if (CollectionUtil.isNotEmpty(list)) {
            throw new BusinessException("关联PID，不可删除");
        }
        return super.removeById(id);
    }


    @Override
    public PageEntity<ChannelMedium> getChannelMediumPageList(ChannelMediumPageParam channelMediumPageParam, HttpServletRequest request) throws Exception {
        // 权限 By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        channelMediumPageParam.setDepartmentCodeAllList(departmentCodeAllList);
        Map<Integer, List<UserDmEntity>> mapAll = businessDictService.getCCIDAndPidByPermissions(user);
        channelMediumPageParam.setMapAll(mapAll);

        Page<ChannelMedium> page = new Page<>(channelMediumPageParam.getPageIndex(), channelMediumPageParam.getPageSize());
        LambdaQueryWrapper<ChannelMedium> wrapper = getLambdaQueryWrapper(channelMediumPageParam);
        IPage<ChannelMedium> iPage = channelMediumMapper.selectPage(page, wrapper);

        List<ChannelMedium> channelMediumList = iPage.getRecords();
        String mediumIdLists = channelMediumList.stream().map(i -> String.valueOf(i.getId())).collect(Collectors.joining(","));
        channelMediumPageParam.setMediumIdLists(mediumIdLists);
        List<Map<String, String>> countPidByMediumMap = channelPromotionService.countPidByMedium(channelMediumPageParam, user);
        Map<Long, Long> mediumSignMap = getMediumsSignMap(countPidByMediumMap);

        List<ChannelMedium> list = iPage.getRecords();
        for (ChannelMedium param : list) {
            param.setPIdNum(mediumSignMap.containsKey(param.getId()) ? String.valueOf(mediumSignMap.get(param.getId())) : "0");
        }
        return new PageEntity<ChannelMedium>(iPage);
    }

    public Map<Long, Long> getMediumsSignMap(List<Map<String, String>> MediumsMapList) {
        Map<Long, Long> map = new HashMap<Long, Long>();

        Map<String, Long> mediumsMap = MediumsMapList.stream().collect(Collectors.toMap(item -> String.valueOf(item.get("mediumId")), item -> Long.valueOf(String.valueOf(item.get("countPid")))));
        for (Map.Entry<String, Long> entry : mediumsMap.entrySet()) {
            String key = entry.getKey();
            Long value = entry.getValue();
            if (key.contains(",")) {
                List<String> list = new ArrayList<String>(Arrays.asList(key.split(",")));
                for (String key1 : list) {
                    getMedium2(map, Long.valueOf(key1), value);
                }
            } else {
                getMedium2(map, Long.valueOf(key), value);
            }
        }
        return map;
    }

    public void getMedium2(Map<Long, Long> map, Long key, Long value) {
        if (map.containsKey(key)) {
            map.put(key, map.get(key) + value);
        } else {
            map.put(key, value);
        }
    }

    @Override
    public List<ChannelMedium> getChannelMediumList(ChannelMediumPageParam channelMediumPageParam, HttpServletRequest request) throws Exception {
        // 权限 By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        channelMediumPageParam.setDepartmentCodeAllList(departmentCodeAllList);

        LambdaQueryWrapper<ChannelMedium> wrapper = getLambdaQueryWrapper(channelMediumPageParam);
        List<ChannelMedium> ChannelMediumList = channelMediumMapper.selectList(wrapper);

        if (StringUtils.isNotBlank(channelMediumPageParam.getPid())) {
            Map<String, ChannelMedium> channelMediumMap = ChannelMediumList.stream().collect(Collectors.toMap(s -> String.valueOf(s.getId()), s -> s));

            ChannelPromotion channelPromotion = channelPromotionMapper.selectOne(new LambdaQueryWrapper<ChannelPromotion>().eq(ChannelPromotion::getPid, channelMediumPageParam.getPid()));
            String mediumId = channelPromotion.getMediumId();
            if (StringUtils.isNotBlank(mediumId) && !channelMediumMap.containsKey(mediumId)) {
                ChannelMedium channelMedium = channelMediumMapper.selectOne(new LambdaQueryWrapper<ChannelMedium>().eq(ChannelMedium::getId, mediumId));
                if (ObjectUtil.isNotEmpty(channelMedium)) {
                    ChannelMediumList.add(channelMedium);
                }
            }
        }


        return ChannelMediumList;
    }

    @Override
    public List<DepartmentVO> listDepartment(HttpServletRequest request) {
        // 权限 By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);

        return channelMediumMapper.selectList(new QueryWrapper<ChannelMedium>().select("department_code", "department_name").lambda()
                .in(departmentCodeAllList.size() > 0, ChannelMedium::getDepartmentCode, departmentCodeAllList)
        ).stream().map(p -> new DepartmentVO(p.getDepartmentCode(), p.getDepartmentName())).collect(Collectors.toList());
    }

    private LambdaQueryWrapper<ChannelMedium> getLambdaQueryWrapper(ChannelMediumPageParam channelMediumPageParam) {
        LambdaQueryWrapper<ChannelMedium> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StrUtil.isNotEmpty(channelMediumPageParam.getDepartmentCode()), ChannelMedium::getDepartmentCode, channelMediumPageParam.getDepartmentCode());
        wrapper.in(channelMediumPageParam.getDepartmentCodeAllList().size() > 0, ChannelMedium::getDepartmentCode, channelMediumPageParam.getDepartmentCodeAllList());
        wrapper.like(StrUtil.isNotEmpty(channelMediumPageParam.getKeyword()), ChannelMedium::getDepartmentCode, channelMediumPageParam.getKeyword())
                .or().like(StrUtil.isNotEmpty(channelMediumPageParam.getKeyword()), ChannelMedium::getName, channelMediumPageParam.getKeyword())
                .or().like(StrUtil.isNotEmpty(channelMediumPageParam.getKeyword()), ChannelMedium::getDepartmentName, channelMediumPageParam.getKeyword());
        if (StrUtil.isNotEmpty(channelMediumPageParam.getKeyword())) {
            try {
                long id = Long.parseLong(channelMediumPageParam.getKeyword());
                wrapper.or().eq(ChannelMedium::getId, id);
            } catch (Exception e) {
            }
        }
        wrapper.orderByDesc(ChannelMedium::getUpdateTime);
        return wrapper;
    }

}
