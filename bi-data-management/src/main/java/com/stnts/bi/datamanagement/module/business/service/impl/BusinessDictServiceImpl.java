package com.stnts.bi.datamanagement.module.business.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stnts.bi.common.BiSessionUtil;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.constant.BooleanEnum;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.business.entity.BusinessDict;
import com.stnts.bi.datamanagement.module.business.mapper.BusinessCheckMapper;
import com.stnts.bi.datamanagement.module.business.mapper.BusinessDictMapper;
import com.stnts.bi.datamanagement.module.business.service.BusinessDictService;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelProductMapper;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelPromotionMapper;
import com.stnts.bi.datamanagement.module.channel.service.ChannelCooperationService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelPromotionAllService;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationBiService;
import com.stnts.bi.datamanagement.module.feign.SysClient;
import com.stnts.bi.entity.sys.DepartmentEntity;
import com.stnts.bi.entity.sys.OrgEntity;
import com.stnts.bi.entity.sys.UserDmEntity;
import com.stnts.bi.entity.sys.UserEntity;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务分类 服务实现类
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-08
 */
@Service
public class BusinessDictServiceImpl extends ServiceImpl<BusinessDictMapper, BusinessDict> implements BusinessDictService {
    private static final Logger logger = LoggerFactory.getLogger(BusinessDictServiceImpl.class);

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private BusinessDictMapper businessDictMapper;
    @Autowired
    private SysClient sysClient;
    @Autowired
    private ChannelCooperationService channelCooperationService;
    @Autowired
    private CooperationBiService cooperationBiService;
    @Autowired
    private ChannelPromotionAllService channelPromotionAllService;
    @Autowired
    private ChannelProductMapper channelProductMapper;
    @Autowired
    private BusinessCheckMapper businessCheckMapper;
    @Autowired
    private ChannelPromotionMapper channelPromotionMapper;

    @Override
    public BusinessDict saveBusinessDict(BusinessDict businessDict) {
        //判断业务考核是否存在
        exists(businessDict);

        businessDict.setIsValid(BooleanEnum.True.getKey());
        super.save(businessDict);

        return businessDict;
    }

    @Override
    public BusinessDict updateBusinessDict(BusinessDict businessDict) {
        //判断业务考核是否存在
        exists(businessDict);

        //先创建实体副本
        BusinessDict businessDictDB = businessDictMapper.selectOne(new QueryWrapper<BusinessDict>().lambda().eq(BusinessDict::getId, businessDict.getId()));

        // 在编辑时，如果业务分类被PID使用，则不能变更部门
        if (!businessDict.getDepartmentCode().equals(businessDictDB.getDepartmentCode())) {
            Integer count = channelPromotionMapper.countByDict(businessDictDB);
            if (count > 0) {
                throw new BusinessException("业务分类被PID使用，不能变更部门");
            }
        }

        super.updateById(businessDict);

        // 改产品相关业务分类
        businessCheckMapper.updateDict(businessDict);
        channelProductMapper.updateDict(businessDict);
        channelCooperationService.updateDict(businessDict);

        //存宽表
        channelPromotionAllService.updateDictThread(businessDictDB, businessDict);

        return businessDict;
    }


    /**
     * 通过组织架构获取用户有权限的部门
     * 1. 若组织与用户所在部门不重合，则用户失去本部门权限，只有组织下部门权限
     * 2. 若组织与用户所在部门重合，则用户有组织下部门权限
     *
     * @param user
     * @return
     */
    @Override
    public List<String> getDepartmentListByPermissions(UserEntity user) {
        List<String> departmentCodeAllList = new ArrayList<String>();
        if (user != null) {
            List<OrgEntity> orgEntities = user.getOrgs();
            List<Integer> orgId = orgEntities.stream().map(OrgEntity::getOrgId).collect(Collectors.toList());
            //获取部门
            if (orgId != null && orgId.size() > 0) {
                List<DepartmentEntity> departmentEntityList = sysClient.listDepartmentByOrgId(orgId);
                List<String> departmentCodeList = departmentEntityList.stream().map(DepartmentEntity::getCode).collect(Collectors.toList());
                departmentCodeAllList.addAll(departmentCodeList);
            }
        } else {
            logger.info("用户获取为null");
            throw new BusinessException("用户获取为null");
        }

        //若组织与用户所在部门不重合，则用户失去本部门权限，只有组织下部门权限
        /*if (departmentCodeAllList.size() > 0) {
            //departmentCodeAllList.remove(user.getCode());
        } else {
            departmentCodeAllList.add(user.getCode());
        }*/
        departmentCodeAllList.add(user.getCode());

        return departmentCodeAllList;
    }

    @Override
    public Map<Integer, List<UserDmEntity>> getCCIDAndPidByPermissions(UserEntity user) {
        if (user != null) {
            ResultEntity<List<UserDmEntity>> resultEntity = sysClient.listDmByUserId(String.valueOf(user.getId()));
            List<UserDmEntity> departmentEntityList = resultEntity.getData();

            Map<Integer, List<UserDmEntity>> departmentEntityMap = departmentEntityList.stream().collect(Collectors.groupingBy(UserDmEntity::getDmType));
            /*if (departmentEntityMap.containsKey(1)) {
                //1为部门组织
                List<String> departmentSelectList = departmentEntityMap.get(1).stream().map(UserDmEntity::getDmId).collect(Collectors.toList());
                mapAll.put(1, departmentSelectList);
            }
            if (departmentEntityMap.containsKey(2)) {
                //2为CCID
                List<String> ccidSelectList = departmentEntityMap.get(2).stream().map(UserDmEntity::getDmId).collect(Collectors.toList());
                mapAll.put(2, ccidSelectList);
            }
            if (departmentEntityMap.containsKey(3)) {
                //3为PID负责人【上一级的ccid】
                List<String> userSelectList = departmentEntityMap.get(3).stream().map(UserDmEntity::getDmPid).collect(Collectors.toList());
                mapAll.put(3, userSelectList);
            }*/
            return departmentEntityMap;
        }

        return null;
    }

    @Override
    public Page<BusinessDict> listPage(String departmentCode, String businessLevel, Integer currentPage, Integer pageSize, HttpServletRequest request) {
        // 权限 By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = this.getDepartmentListByPermissions(user);

        String[] splitBusinessLevel = StrUtil.splitToArray(businessLevel, ',');
        String firstLevelBusiness = ArrayUtil.<String>get(splitBusinessLevel, 0);
        String secondLevelBusiness = ArrayUtil.<String>get(splitBusinessLevel, 1);
        String thirdLevelBusiness = ArrayUtil.<String>get(splitBusinessLevel, 2);

        Page<BusinessDict> pageObj = new Page<>(Optional.ofNullable(currentPage).orElse(1), Optional.ofNullable(pageSize).orElse(10));
        Page<BusinessDict> businessDictList = businessDictMapper.selectPage(pageObj, new QueryWrapper<BusinessDict>()
                .lambda().eq(StringUtils.isNotEmpty(departmentCode), BusinessDict::getDepartmentCode, departmentCode)
                .eq(StringUtils.isNotEmpty(firstLevelBusiness), BusinessDict::getFirstLevel, firstLevelBusiness)
                .eq(StringUtils.isNotEmpty(secondLevelBusiness), BusinessDict::getSecondLevel, secondLevelBusiness)
                .eq(StringUtils.isNotEmpty(thirdLevelBusiness), BusinessDict::getThirdLevel, thirdLevelBusiness)
                .in(CollectionUtil.isNotEmpty(departmentCodeAllList), BusinessDict::getDepartmentCode, departmentCodeAllList)
        );

        return businessDictList;
    }

    @Override
    public Map<String, Object> searchList(String departmentCode, String businessLevel, HttpServletRequest request) {
        // 权限 By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = this.getDepartmentListByPermissions(user);

        String[] splitBusinessLevel = StrUtil.splitToArray(businessLevel, ',');
        String firstLevelBusiness = ArrayUtil.<String>get(splitBusinessLevel, 0);
        String secondLevelBusiness = ArrayUtil.<String>get(splitBusinessLevel, 1);
        String thirdLevelBusiness = ArrayUtil.<String>get(splitBusinessLevel, 2);

        List<BusinessDict> businessDictList = businessDictMapper.selectList(new QueryWrapper<BusinessDict>()
                .select("distinct department_code, root_level, first_level, second_level, third_level")
                .lambda().eq(StringUtils.isNotEmpty(departmentCode), BusinessDict::getDepartmentCode, departmentCode)
                .eq(StringUtils.isNotEmpty(firstLevelBusiness), BusinessDict::getFirstLevel, firstLevelBusiness)
                .eq(StringUtils.isNotEmpty(secondLevelBusiness), BusinessDict::getSecondLevel, secondLevelBusiness)
                .eq(StringUtils.isNotEmpty(thirdLevelBusiness), BusinessDict::getThirdLevel, thirdLevelBusiness)
                .in(departmentCodeAllList.size() > 0, BusinessDict::getDepartmentCode, departmentCodeAllList)
        );

        //部门
        Set<Map<String, String>> data = new HashSet<Map<String, String>>();
        for (BusinessDict businessDict : businessDictList) {
            String dc = businessDict.getDepartmentCode();
            String dn = businessDict.getRootLevel();
            Map<String, String> map = new HashMap<String, String>();
            map.put("code", dc);
            map.put("name", dn);
            data.add(map);
        }

        //业务分类
        Map<String, Map<String, Set<String>>> levelList = businessDictList.stream().distinct()
                .collect(
                        Collectors.groupingBy(BusinessDict::getFirstLevel,
                                Collectors.groupingBy(BusinessDict::getSecondLevel,
                                        Collectors.mapping(BusinessDict::getThirdLevel, Collectors.toSet()))));

        Map<String, Object> mapAll = new HashMap<String, Object>();
        mapAll.put("department", data);
        mapAll.put("level", levelList);

        return mapAll;
    }

    @Override
    public List<Map<String, String>> departmentList(String departmentCode, String businessLevel, HttpServletRequest request) {
        // 权限 By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = this.getDepartmentListByPermissions(user);

        List<Map<String, String>> data = cooperationBiService.departmentList(request);

        String[] splitBusinessLevel = StrUtil.splitToArray(businessLevel, ',');
        String firstLevelBusiness = ArrayUtil.<String>get(splitBusinessLevel, 0);
        String secondLevelBusiness = ArrayUtil.<String>get(splitBusinessLevel, 1);
        String thirdLevelBusiness = ArrayUtil.<String>get(splitBusinessLevel, 2);

        List<BusinessDict> businessDictList = businessDictMapper.selectList(new QueryWrapper<BusinessDict>().select("distinct department_code")
                .lambda()
                .eq(StringUtils.isNotEmpty(departmentCode), BusinessDict::getDepartmentCode, departmentCode)
                .eq(StringUtils.isNotEmpty(firstLevelBusiness), BusinessDict::getFirstLevel, firstLevelBusiness)
                .eq(StringUtils.isNotEmpty(secondLevelBusiness), BusinessDict::getSecondLevel, secondLevelBusiness)
                .eq(StringUtils.isNotEmpty(thirdLevelBusiness), BusinessDict::getThirdLevel, thirdLevelBusiness)
                .in(CollectionUtil.isNotEmpty(departmentCodeAllList), BusinessDict::getDepartmentCode, departmentCodeAllList)
        );
        List<String> departmentCodeList = businessDictList.stream().map(BusinessDict::getDepartmentCode).distinct().collect(Collectors.toList());

        data = data.stream().filter(map -> departmentCodeList.contains(map.get("code"))).collect(Collectors.toList());

        return data;
    }

    @Override
    public List<String> firstLevel(String keyword, String departmentCode, HttpServletRequest request) {
        // 权限 By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = this.getDepartmentListByPermissions(user);

        List<BusinessDict> businessDictList = businessDictMapper.selectList(new QueryWrapper<BusinessDict>()
                .select("distinct first_level")
                .lambda().eq(BusinessDict::getIsValid, BooleanEnum.True.getKey())
                .eq(StrUtil.isNotBlank(departmentCode), BusinessDict::getDepartmentCode, departmentCode)
                .like(StrUtil.isNotBlank(keyword), BusinessDict::getFirstLevel, keyword)
                .in(CollectionUtil.isNotEmpty(departmentCodeAllList), BusinessDict::getDepartmentCode, departmentCodeAllList)
        );
        List<String> firstLevel = businessDictList.stream().map(BusinessDict::getFirstLevel).collect(Collectors.toList());

        return firstLevel;
    }

    @Override
    public List<String> secondLevel(String keyword, String firstLevel, HttpServletRequest request) {
        // 权限 By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = this.getDepartmentListByPermissions(user);

        List<BusinessDict> businessDictList = businessDictMapper.selectList(new QueryWrapper<BusinessDict>()
                .select("distinct second_level")
                .lambda().eq(BusinessDict::getIsValid, BooleanEnum.True.getKey())
                .like(StrUtil.isNotBlank(keyword), BusinessDict::getSecondLevel, keyword)
                .eq(StrUtil.isNotBlank(firstLevel), BusinessDict::getFirstLevel, firstLevel)
                .in(CollectionUtil.isNotEmpty(departmentCodeAllList), BusinessDict::getDepartmentCode, departmentCodeAllList)
        );

        List<String> secondLevel = businessDictList.stream().map(BusinessDict::getSecondLevel).collect(Collectors.toList());

        return secondLevel;
    }

    @Override
    public List<String> thirdLevel(String keyword, String secondLevel, HttpServletRequest request) {
        // 权限 By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = this.getDepartmentListByPermissions(user);

        List<BusinessDict> businessDictList = businessDictMapper.selectList(new QueryWrapper<BusinessDict>()
                .select("distinct third_level")
                .lambda().eq(BusinessDict::getIsValid, BooleanEnum.True.getKey())
                .like(StrUtil.isNotBlank(keyword), BusinessDict::getThirdLevel, keyword)
                .eq(StrUtil.isNotBlank(secondLevel), BusinessDict::getSecondLevel, secondLevel)
                .in(CollectionUtil.isNotEmpty(departmentCodeAllList), BusinessDict::getDepartmentCode, departmentCodeAllList)
        );
        List<String> thirdLevel = businessDictList.stream().map(BusinessDict::getThirdLevel).collect(Collectors.toList());

        return thirdLevel;
    }

    @Override
    public Map<String, Object> tree(String keyword, HttpServletRequest request) {
        // 权限 By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = this.getDepartmentListByPermissions(user);

        //基础数据
        List<BusinessDict> businessDictList = getRes(keyword, null, departmentCodeAllList);

        //加工
        Map<String, Map<String, Set<String>>> dictMap = businessDictList.stream()
                .collect(
                        Collectors.groupingBy(BusinessDict::getFirstLevel,
                                Collectors.groupingBy(BusinessDict::getSecondLevel,
                                        Collectors.mapping(BusinessDict::getThirdLevel, Collectors.toSet()))));

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("dictMap", dictMap);
        return result;
    }

    @Override
    public Map<String, Object> row(String keyword, String searchKey, HttpServletRequest request) {
        // 权限 By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = this.getDepartmentListByPermissions(user);

        //基础数据
        List<BusinessDict> businessDictList = getRes(keyword, searchKey, departmentCodeAllList);
        businessDictList = businessDictList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(i -> i.getFirstLevel() + i.getSecondLevel() + i.getThirdLevel()))), ArrayList::new));

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("dictMap", businessDictList);
        return result;
    }

    public List<BusinessDict> getRes(String keyword, String searchKey, List<String> departmentCodeAllList) {
        List<BusinessDict> businessDictList = businessDictMapper.selectList(new QueryWrapper<BusinessDict>().lambda()
                .in(departmentCodeAllList.size() > 0, BusinessDict::getDepartmentCode, departmentCodeAllList)
                .and(StrUtil.isNotBlank(keyword), i -> i.like(BusinessDict::getRootLevel, keyword).or().like(BusinessDict::getDepartmentCode, keyword))
                .and(StrUtil.isNotBlank(searchKey), i -> i.like(BusinessDict::getFirstLevel, searchKey).or().like(BusinessDict::getSecondLevel, searchKey).or().like(BusinessDict::getThirdLevel, searchKey))
                .and(i -> i.le(BusinessDict::getYearStart, DateUtil.year(new Date())).ge(BusinessDict::getYearEnd, DateUtil.year(new Date())))
        );

        return businessDictList;
    }

    /**
     * 判断业务考核是否存在
     *
     * @param businessDict
     */
    private void exists(BusinessDict businessDict) {
        if (ObjectUtil.isEmpty(businessDict.getYearStart())) {
            throw new BusinessException("考核年份开始时间必填");
        }
        if (ObjectUtil.isEmpty(businessDict.getYearEnd())) {
            businessDict.setYearEnd(2099);
        }
        if (businessDict.getYearStart() > businessDict.getYearEnd()) {
            throw new BusinessException("考核年份结束时间不可小于开始时间");
        }

        LambdaQueryWrapper<BusinessDict> lambda = new QueryWrapper<BusinessDict>().lambda();
        lambda.eq(BusinessDict::getDepartmentCode, businessDict.getDepartmentCode())
                .eq(BusinessDict::getRootLevel, businessDict.getRootLevel())
                .eq(BusinessDict::getFirstLevel, businessDict.getFirstLevel())
                .eq(BusinessDict::getSecondLevel, businessDict.getSecondLevel())
                .eq(BusinessDict::getThirdLevel, businessDict.getThirdLevel())
                .and(j -> j.between(BusinessDict::getYearStart, businessDict.getYearStart(), businessDict.getYearEnd())
                        .or().between(BusinessDict::getYearEnd, businessDict.getYearStart(), businessDict.getYearEnd())
                        .or(k -> k.le(BusinessDict::getYearStart, businessDict.getYearStart()).ge(BusinessDict::getYearEnd, businessDict.getYearEnd()))
                )
                .ne(ObjectUtil.isNotEmpty(businessDict.getId()), BusinessDict::getId, businessDict.getId());
        List<BusinessDict> one = super.list(lambda);
        if (CollectionUtil.isNotEmpty(one)) {
            throw new BusinessException("业务分类已经存在");
        }
    }
}
