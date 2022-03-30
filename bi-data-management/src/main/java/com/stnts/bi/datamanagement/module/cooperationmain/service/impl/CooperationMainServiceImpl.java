package com.stnts.bi.datamanagement.module.cooperationmain.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stnts.bi.common.BiSessionUtil;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.business.service.BusinessDictService;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelProduct;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelProductMapper;
import com.stnts.bi.datamanagement.module.channel.service.ChannelPromotionAllService;
import com.stnts.bi.datamanagement.module.cooperationmain.entity.CooperationMain;
import com.stnts.bi.datamanagement.module.cooperationmain.mapper.CooperationMainMapper;
import com.stnts.bi.datamanagement.module.cooperationmain.param.CooperationMainPageParam;
import com.stnts.bi.datamanagement.module.cooperationmain.service.CooperationMainService;
import com.stnts.bi.entity.common.PageEntity;
import com.stnts.bi.entity.sys.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 公司主体 服务实现类
 *
 * @author 易樊
 * @since 2021-09-17
 */
@Slf4j
@Service
public class CooperationMainServiceImpl extends ServiceImpl<CooperationMainMapper, CooperationMain> implements CooperationMainService {
    @Autowired
    private CooperationMainMapper cooperationMainMapper;
    @Autowired
    private ChannelProductMapper channelProductMapper;
    @Autowired
    private ChannelPromotionAllService channelPromotionAllService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private BusinessDictService businessDictService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveCooperationMain(CooperationMain cooperationMain, HttpServletRequest request) throws Exception {
        CooperationMain cooperationMainDB = cooperationMainMapper.selectOne(new LambdaQueryWrapper<CooperationMain>()
                .eq(CooperationMain::getDepartmentCode, cooperationMain.getDepartmentCode())
                .eq(CooperationMain::getCooperationMainName, cooperationMain.getCooperationMainName())
        );
        if (ObjectUtil.isNotEmpty(cooperationMainDB)) {
            throw new BusinessException("公司主体已存在");
        }

        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        cooperationMain.setCreateUserId(Long.valueOf(user.getId()));
        cooperationMain.setCreateUserName(user.getCnname());

        return super.save(cooperationMain);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateCooperationMain(CooperationMain cooperationMain) throws Exception {
        CooperationMain cooperationMainDB = cooperationMainMapper.selectOne(new LambdaQueryWrapper<CooperationMain>()
                .eq(CooperationMain::getDepartmentCode, cooperationMain.getDepartmentCode())
                .eq(CooperationMain::getCooperationMainName, cooperationMain.getCooperationMainName())
                .ne(CooperationMain::getId, cooperationMain.getId())
        );
        if (ObjectUtil.isNotEmpty(cooperationMainDB)) {
            throw new BusinessException("公司主体已存在");
        }

        CooperationMain cooperationMainDBOne = cooperationMainMapper.selectOne(new LambdaQueryWrapper<CooperationMain>().eq(CooperationMain::getId, cooperationMain.getId()));
        //如果有产品关联公司主体，则不能修改部门
        if (!cooperationMainDBOne.getDepartmentCode().equals(cooperationMain.getDepartmentCode())) {
            Integer count = channelProductMapper.selectCount(new LambdaQueryWrapper<ChannelProduct>().eq(ChannelProduct::getCooperationMainId, cooperationMain.getId()));
            if (count > 0) {
                throw new BusinessException("已有产品关联当前公司主体，部门无法修改");
            }
        }

        super.updateById(cooperationMain);

        //更新产品中公司主体信息
        channelProductMapper.updateMain(cooperationMain);

        //存宽表
        channelPromotionAllService.updateMainThread(cooperationMain);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteCooperationMain(Long id) throws Exception {
        return super.removeById(id);
    }

    @Override
    public PageEntity<CooperationMain> getCooperationMainPageList(CooperationMainPageParam cooperationMainPageParam, HttpServletRequest request) throws Exception {
        // 权限 By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        cooperationMainPageParam.setDepartmentCodeAllList(departmentCodeAllList);

        Page<CooperationMain> page = new Page<>(cooperationMainPageParam.getPageIndex(), cooperationMainPageParam.getPageSize());
        LambdaQueryWrapper<CooperationMain> wrapper = getLambdaQueryWrapper(cooperationMainPageParam);
        IPage<CooperationMain> iPage = cooperationMainMapper.selectPage(page, wrapper);

        PageEntity pageEntity = new PageEntity<CooperationMain>(iPage);

        return pageEntity;
    }

    @Override
    public List<CooperationMain> getCooperationMainList(CooperationMainPageParam cooperationMainPageParam, HttpServletRequest request) throws Exception {
        // 权限 By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        cooperationMainPageParam.setDepartmentCodeAllList(departmentCodeAllList);

        List<CooperationMain> cooperationMainList = getCooperationMainListCommon(cooperationMainPageParam);
        return cooperationMainList;
    }

    @Override
    public List<CooperationMain> getCooperationMainListGeneral(CooperationMainPageParam cooperationMainPageParam) throws Exception {
        List<CooperationMain> cooperationMainList = getCooperationMainListCommon(cooperationMainPageParam);
        return cooperationMainList;
    }

    public List<CooperationMain> getCooperationMainListCommon(CooperationMainPageParam cooperationMainPageParam) {
        LambdaQueryWrapper<CooperationMain> wrapper = getLambdaQueryWrapper(cooperationMainPageParam);
        List<CooperationMain> CooperationMainList = cooperationMainMapper.selectList(wrapper);
        return CooperationMainList;
    }

    @Override
    public Map<String, Object> searchList(String departmentCode, String cooperationMainId, HttpServletRequest request) throws Exception {
        CooperationMainPageParam cooperationMainPageParam = new CooperationMainPageParam();
        cooperationMainPageParam.setDepartmentCode(departmentCode);
        cooperationMainPageParam.setCooperationMainId(StringUtils.isNotBlank(cooperationMainId) ? Long.valueOf(cooperationMainId) : null);
        List<CooperationMain> cooperationMainList = this.getCooperationMainList(cooperationMainPageParam, request);

        //部门
        Set<Map<String, String>> departmentSet = new HashSet<Map<String, String>>();
        //公司
        Set<Map<String, String>> cooperationMainSet = new HashSet<Map<String, String>>();
        for (CooperationMain cooperationMain : cooperationMainList) {
            if (StringUtils.isNotBlank(cooperationMain.getDepartmentCode())) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("code", cooperationMain.getDepartmentCode());
                map.put("name", cooperationMain.getDepartmentName());
                departmentSet.add(map);
            }
            if (ObjectUtil.isNotEmpty(cooperationMain.getId())) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("cooperationMainId", String.valueOf(cooperationMain.getId()));
                map.put("cooperationMainName", cooperationMain.getCooperationMainName());
                cooperationMainSet.add(map);
            }
        }

        departmentSet = departmentSet.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(i -> i.get("code")))), HashSet::new));
        cooperationMainSet = cooperationMainSet.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(i -> i.get("cooperationMainId")))), HashSet::new));

        Map<String, Object> mapAlls = new HashMap<String, Object>();
        mapAlls.put("department", departmentSet);
        mapAlls.put("cooperationMain", cooperationMainSet);

        return mapAlls;
    }

    private LambdaQueryWrapper<CooperationMain> getLambdaQueryWrapper(CooperationMainPageParam cooperationMainPageParam) {
        LambdaQueryWrapper<CooperationMain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(cooperationMainPageParam.getDepartmentCode()), CooperationMain::getDepartmentCode, cooperationMainPageParam.getDepartmentCode())
                .eq(ObjectUtil.isNotEmpty(cooperationMainPageParam.getCooperationMainId()), CooperationMain::getId, cooperationMainPageParam.getCooperationMainId())
                .like(StringUtils.isNotBlank(cooperationMainPageParam.getCooperationMainName()), CooperationMain::getCooperationMainName, cooperationMainPageParam.getCooperationMainName())
                .in(CollectionUtil.isNotEmpty(cooperationMainPageParam.getDepartmentCodeAllList()), CooperationMain::getDepartmentCode, cooperationMainPageParam.getDepartmentCodeAllList())
                .and(StringUtils.isNotBlank(cooperationMainPageParam.getKeyword()), w -> w.like(CooperationMain::getId, cooperationMainPageParam.getKeyword())
                        .or().like(CooperationMain::getDepartmentName, cooperationMainPageParam.getKeyword())
                        .or().like(CooperationMain::getCooperationMainName, cooperationMainPageParam.getKeyword())
                        .or().like(CooperationMain::getRemark, cooperationMainPageParam.getKeyword())
                        .or().like(CooperationMain::getCreateUserName, cooperationMainPageParam.getKeyword())
                        .or().like(CooperationMain::getCreateTime, cooperationMainPageParam.getKeyword()))
                .orderByDesc(CooperationMain::getId);
        return wrapper;
    }

}
