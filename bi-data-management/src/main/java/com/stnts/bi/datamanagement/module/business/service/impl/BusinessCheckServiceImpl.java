package com.stnts.bi.datamanagement.module.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stnts.bi.common.BiSessionUtil;
import com.stnts.bi.datamanagement.constant.BooleanEnum;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.business.entity.BusinessCheck;
import com.stnts.bi.datamanagement.module.business.entity.BusinessCheckHistory;
import com.stnts.bi.datamanagement.module.business.entity.BusinessCheckLine;
import com.stnts.bi.datamanagement.module.business.entity.BusinessDict;
import com.stnts.bi.datamanagement.module.business.mapper.BusinessCheckMapper;
import com.stnts.bi.datamanagement.module.business.service.BusinessCheckHistoryService;
import com.stnts.bi.datamanagement.module.business.service.BusinessCheckService;
import com.stnts.bi.datamanagement.module.business.service.BusinessDictService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelCooperationService;
import com.stnts.bi.datamanagement.util.CompareMapUtil;
import com.stnts.bi.datamanagement.util.DozerUtil;
import com.stnts.bi.datamanagement.util.JacksonUtil;
import com.stnts.bi.entity.sys.UserEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务考核 服务实现类
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-08
 */
@Service
public class BusinessCheckServiceImpl extends ServiceImpl<BusinessCheckMapper, BusinessCheck> implements BusinessCheckService {
    private final Map<String, String> dict = MapUtil.<String, String>builder()
            .put("department", "部门")
            .put("firstLevelBusiness", "一级分类")
            .put("secondLevelBusiness", "二级分类")
            .put("thirdLevelBusiness", "三级分类")
            .put("checkTarget", "考核指标")
            .put("levelS", "S档")
            .put("levelA", "A档")
            .put("levelB", "B档")
            .put("levelC", "C档")
            .put("levelD", "D档")
            .put("checkStartDate", "考核期限开始时间")
            .put("checkEndDate", "考核期限结束时间")
            .map();

    @Autowired
    private BusinessCheckHistoryService businessCheckHistoryService;
    @Autowired
    private BusinessDictService businessDictService;
    @Autowired
    private BusinessCheckMapper businessCheckMapper;
    @Autowired
    private ChannelCooperationService channelCooperationService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public BusinessCheck saveBusinessCheck(BusinessCheck businessCheck, HttpServletRequest request) {
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();

        List<BusinessCheckLine> businessCheckLineList = businessCheck.getBusinessCheckLineList();
        if (CollectionUtil.isEmpty(businessCheckLineList)) {
            throw new BusinessException("考核目标不可为空");
        }

        //处理业务分类
        BusinessDict businessDict = channelCooperationService.handlerBusinessDictId(businessCheck.getBusinessDictId(), businessCheck.getDepartmentCode(), businessCheck.getFirstLevelBusiness(), businessCheck.getSecondLevelBusiness(), businessCheck.getThirdLevelBusiness());
        businessCheck.setBusinessDictId(businessDict.getId());
        businessCheck.setFirstLevelBusiness(businessDict.getFirstLevel());
        businessCheck.setSecondLevelBusiness(businessDict.getSecondLevel());
        businessCheck.setThirdLevelBusiness(businessDict.getThirdLevel());

        // 判断参数中是否有重复，且获取重复（有效期交叉）的业务明细
        List<BusinessCheck> businessCheckAll = new ArrayList<BusinessCheck>();
        for (BusinessCheckLine businessCheckLine : businessCheckLineList) {
            BusinessCheck businessCheckSub = DozerUtil.toBean(businessCheck, BusinessCheck.class);
            BeanUtils.copyProperties(businessCheckLine, businessCheckSub);
            businessCheckSub.setCreateUser(user.getCnname());
            businessCheckSub.setUpdateUser(user.getCnname());
            businessCheckSub.setIsValid(BooleanEnum.True.getKey());
            businessCheckAll.add(businessCheckSub);
        }
        exists(businessCheckAll);

        super.saveBatch(businessCheckAll);

        saveBusinessCheckHistoryAdd(businessCheckAll, null, user.getCnname(), user.getId(), "新增");

        return businessCheck;
    }

    /**
     * 判断业务考核是否存在
     *
     * @param businessCheckListParam
     */
    private void exists(List<BusinessCheck> businessCheckListParam) {
        if (CollectionUtil.isNotEmpty(businessCheckListParam)) {
            // 仅对参数中进行校验
            Set<String> detailDeciaml = businessCheckListParam.stream().map(o -> o.getCheckTarget() + "_" + o.getLevelS() + "_" + o.getLevelA() + "_" + o.getLevelB() + "_" + o.getLevelC() + "_" + o.getLevelD()).collect(Collectors.toSet());
            if (detailDeciaml.size() < businessCheckListParam.size()) {
                throw new BusinessException("参数中，考核目标存在相同行");
            }

            Date checkStartDate = businessCheckListParam.get(0).getCheckStartDate();
            Date checkEndDate = businessCheckListParam.get(0).getCheckEndDate();
            List<Long> idList = businessCheckListParam.stream().filter(i -> ObjectUtils.isNotEmpty(i.getId())).map(BusinessCheck::getId).collect(Collectors.toList());
            List<BusinessCheck> businessCheckListDB = super.list(new LambdaQueryWrapper<BusinessCheck>()
                    .eq(BusinessCheck::getIsValid, 1).eq(BusinessCheck::getIsDelete, 0)
                    .eq(BusinessCheck::getDepartmentCode, businessCheckListParam.get(0).getDepartmentCode())
                    .eq(BusinessCheck::getDepartment, businessCheckListParam.get(0).getDepartment())
                    .eq(ObjectUtils.isNotEmpty(businessCheckListParam.get(0).getBusinessDictId()), BusinessCheck::getBusinessDictId, businessCheckListParam.get(0).getBusinessDictId())
                    .eq(StringUtils.isNotBlank(businessCheckListParam.get(0).getFirstLevelBusiness()), BusinessCheck::getFirstLevelBusiness, businessCheckListParam.get(0).getFirstLevelBusiness())
                    .eq(StringUtils.isNotBlank(businessCheckListParam.get(0).getSecondLevelBusiness()), BusinessCheck::getSecondLevelBusiness, businessCheckListParam.get(0).getSecondLevelBusiness())
                    .eq(StringUtils.isNotBlank(businessCheckListParam.get(0).getThirdLevelBusiness()), BusinessCheck::getThirdLevelBusiness, businessCheckListParam.get(0).getThirdLevelBusiness())
                    .notIn(CollectionUtil.isNotEmpty(idList), BusinessCheck::getId, idList)
                    .and(i -> i.between(BusinessCheck::getCheckStartDate, checkStartDate, checkEndDate).or()
                            .between(BusinessCheck::getCheckEndDate, checkStartDate, checkEndDate)
                            .or(j -> j.le(BusinessCheck::getCheckStartDate, checkStartDate).ge(BusinessCheck::getCheckEndDate, checkEndDate))
                    )
            );

            if (CollectionUtil.isNotEmpty(businessCheckListDB)) {
                // DB
                //Set<String> businessCheckDetailStrDB = businessCheckListDB.stream().map(o -> o.getCheckTarget() + "_" + o.getLevelS() + "_" + o.getLevelA() + "_" + o.getLevelB() + "_" + o.getLevelC() + "_" + o.getLevelD()).collect(Collectors.toSet());
                Set<String> businessCheckDetailStrDB = businessCheckListDB.stream().map(o -> o.getCheckTarget()).collect(Collectors.toSet());
                //参数
                //Set<String> businessCheckDetailStrParam = businessCheckListParam.stream().map(o -> o.getCheckTarget() + "_" + o.getLevelS() + "_" + o.getLevelA() + "_" + o.getLevelB() + "_" + o.getLevelC() + "_" + o.getLevelD()).collect(Collectors.toSet());
                Set<String> businessCheckDetailStrParam = businessCheckListParam.stream().map(o -> o.getCheckTarget()).collect(Collectors.toSet());

                businessCheckDetailStrDB.retainAll(businessCheckDetailStrParam);
                if (CollectionUtil.isNotEmpty(businessCheckDetailStrDB)) {
                    throw new BusinessException("考核明细已经存在：" + String.join(",", businessCheckDetailStrDB));
                }
            }
        }
    }

    @Override
    public BusinessCheck updateBusinessCheck(BusinessCheck businessCheck, HttpServletRequest request) {
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();

        // 判断参数中是否有重复，且获取重复（有效期交叉）的业务明细
        exists(Arrays.asList(businessCheck));
        //处理业务分类
        BusinessDict businessDict = channelCooperationService.handlerBusinessDictId(businessCheck.getBusinessDictId(), businessCheck.getDepartmentCode(), businessCheck.getFirstLevelBusiness(), businessCheck.getSecondLevelBusiness(), businessCheck.getThirdLevelBusiness());
        businessCheck.setBusinessDictId(businessDict.getId());
        businessCheck.setFirstLevelBusiness(businessDict.getFirstLevel());
        businessCheck.setSecondLevelBusiness(businessDict.getSecondLevel());
        businessCheck.setThirdLevelBusiness(businessDict.getThirdLevel());

        //获取旧数据
        BusinessCheck existBusinessCheck = super.getById(businessCheck.getId());

        //更新考核明细
        businessCheck.setUpdateUser(user.getCnname());
        super.updateById(businessCheck);

        saveBusinessCheckHistoryUpdate(businessCheck, existBusinessCheck, user.getCnname(), user.getId(), "修改");
        return businessCheck;
    }

    @Override
    public Map<String, Object> searchList(String departmentCode, String businessLevel, HttpServletRequest request) {
        // 权限 By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);

        String[] splitBusinessLevel = StrUtil.splitToArray(businessLevel, ',');
        String firstLevelBusiness = ArrayUtil.<String>get(splitBusinessLevel, 0);
        String secondLevelBusiness = ArrayUtil.<String>get(splitBusinessLevel, 1);
        String thirdLevelBusiness = ArrayUtil.<String>get(splitBusinessLevel, 2);

        List<BusinessCheck> businessCheckList = businessCheckMapper.selectList(new QueryWrapper<BusinessCheck>()
                .select("distinct department_code, department, first_level_business, second_level_business, third_level_business")
                .lambda()
                .eq(StrUtil.isNotBlank(departmentCode), BusinessCheck::getDepartmentCode, departmentCode)
                .eq(StrUtil.isNotBlank(firstLevelBusiness), BusinessCheck::getFirstLevelBusiness, firstLevelBusiness)
                .eq(StrUtil.isNotBlank(secondLevelBusiness), BusinessCheck::getSecondLevelBusiness, secondLevelBusiness)
                .eq(StrUtil.isNotBlank(thirdLevelBusiness), BusinessCheck::getThirdLevelBusiness, thirdLevelBusiness)
                .in(CollectionUtil.isNotEmpty(departmentCodeAllList), BusinessCheck::getDepartmentCode, departmentCodeAllList)
        );

        //部门
        Set<Map<String, String>> departmentList = new HashSet<Map<String, String>>();
        for (BusinessCheck businessCheck : businessCheckList) {
            if (StringUtils.isNotBlank(businessCheck.getDepartmentCode())) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("code", businessCheck.getDepartmentCode());
                map.put("name", businessCheck.getDepartment());
                departmentList.add(map);
            }
        }

        //业务分类
        Map<String, Map<String, Set<String>>> levelList = businessCheckList.stream().distinct()
                .collect(
                        Collectors.groupingBy(BusinessCheck::getFirstLevelBusiness,
                                Collectors.groupingBy(BusinessCheck::getSecondLevelBusiness,
                                        Collectors.mapping(BusinessCheck::getThirdLevelBusiness, Collectors.toSet()))));

        Map<String, Object> mapAll = new HashMap<String, Object>();
        mapAll.put("department", departmentList);
        mapAll.put("level", levelList);

        return mapAll;
    }

    @Override
    public List<Map<String, String>> departmentList(String departmentCode, String businessLevel, HttpServletRequest request) {
        // 权限 By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);

        String[] splitBusinessLevel = StrUtil.splitToArray(businessLevel, ',');
        String firstLevelBusiness = ArrayUtil.<String>get(splitBusinessLevel, 0);
        String secondLevelBusiness = ArrayUtil.<String>get(splitBusinessLevel, 1);
        String thirdLevelBusiness = ArrayUtil.<String>get(splitBusinessLevel, 2);

        List<BusinessCheck> businessCheckList = businessCheckMapper.selectList(new QueryWrapper<BusinessCheck>().select("distinct department_code, department")
                .lambda()
                .eq(StrUtil.isNotBlank(departmentCode), BusinessCheck::getDepartmentCode, departmentCode)
                .eq(StrUtil.isNotBlank(firstLevelBusiness), BusinessCheck::getFirstLevelBusiness, firstLevelBusiness)
                .eq(StrUtil.isNotBlank(secondLevelBusiness), BusinessCheck::getSecondLevelBusiness, secondLevelBusiness)
                .eq(StrUtil.isNotBlank(thirdLevelBusiness), BusinessCheck::getThirdLevelBusiness, thirdLevelBusiness)
                .in(CollectionUtil.isNotEmpty(departmentCodeAllList), BusinessCheck::getDepartmentCode, departmentCodeAllList)
        );

        List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
        for (BusinessCheck businessCheck : businessCheckList) {
            if (StringUtils.isNotBlank(businessCheck.getDepartmentCode())) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("code", businessCheck.getDepartmentCode());
                map.put("name", businessCheck.getDepartment());
                resultList.add(map);
            }
        }
        return resultList;
    }

    @Override
    public Page<BusinessCheck> listPage(String department, String departmentCode, String businessLevel, Integer currentPage, Integer pageSize, HttpServletRequest request) {
        // 权限 By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);

        String[] splitBusinessLevel = StrUtil.splitToArray(businessLevel, ',');
        String firstLevelBusiness = ArrayUtil.<String>get(splitBusinessLevel, 0);
        String secondLevelBusiness = ArrayUtil.<String>get(splitBusinessLevel, 1);
        String thirdLevelBusiness = ArrayUtil.<String>get(splitBusinessLevel, 2);

        Page<BusinessCheck> pageObj = new Page<BusinessCheck>(Optional.ofNullable(currentPage).orElse(1), Optional.ofNullable(pageSize).orElse(10));
        Page<BusinessCheck> businessCheckListPage = businessCheckMapper.selectPage(pageObj, new QueryWrapper<BusinessCheck>()
                .lambda().eq(BusinessCheck::getIsDelete, BooleanEnum.False.getKey())
                .eq(StrUtil.isNotEmpty(department), BusinessCheck::getDepartment, department)
                .eq(StrUtil.isNotEmpty(departmentCode), BusinessCheck::getDepartmentCode, departmentCode)
                .eq(StrUtil.isNotEmpty(firstLevelBusiness), BusinessCheck::getFirstLevelBusiness, firstLevelBusiness)
                .eq(StrUtil.isNotEmpty(secondLevelBusiness), BusinessCheck::getSecondLevelBusiness, secondLevelBusiness)
                .eq(StrUtil.isNotEmpty(thirdLevelBusiness), BusinessCheck::getThirdLevelBusiness, thirdLevelBusiness)
                .in(CollectionUtil.isNotEmpty(departmentCodeAllList), BusinessCheck::getDepartmentCode, departmentCodeAllList)
                .orderByAsc(BusinessCheck::getDepartment)
                .orderByAsc(BusinessCheck::getFirstLevelBusiness)
                .orderByAsc(BusinessCheck::getSecondLevelBusiness)
                .orderByAsc(BusinessCheck::getThirdLevelBusiness)
        );

        return businessCheckListPage;
    }

    private void saveBusinessCheckHistoryAdd(List<BusinessCheck> businessCheckList, BusinessCheck existBusinessCheck, String userName, Integer userId, String option) {
        if (ObjectUtils.isEmpty(existBusinessCheck)) {
            //新增
            List<BusinessCheckHistory> businessCheckHistoryList = new ArrayList<BusinessCheckHistory>();
            for (int i = 0; i < businessCheckList.size(); i++) {
                BusinessCheck businessCheck = businessCheckList.get(i);

                Map<String, Object> sourceMap = BeanUtil.beanToMap(existBusinessCheck);
                Map<String, Object> targetMap = BeanUtil.beanToMap(businessCheck);
                rename(sourceMap);
                rename(targetMap);
                CompareMapUtil.compareMap(sourceMap, targetMap);
                BusinessCheckHistory businessCheckHistory = new BusinessCheckHistory();
                businessCheckHistory.setBusinessCheckId(businessCheck.getId());
                businessCheckHistory.setDepartment(businessCheck.getDepartment());
                businessCheckHistory.setOpration(option);
                businessCheckHistory.setCreateUser(userName);
                businessCheckHistory.setCreateUserId(StrUtil.toString(userId));
                businessCheckHistory.setSourceContent(JacksonUtil.toJSON(sourceMap));
                businessCheckHistory.setTargetContent(JacksonUtil.toJSON(targetMap));

                if (MapUtil.isNotEmpty(sourceMap) || MapUtil.isNotEmpty(targetMap)) {
                    businessCheckHistoryList.add(businessCheckHistory);
                }
            }
            businessCheckHistoryService.saveBatch(businessCheckHistoryList);
        }

    }

    private void saveBusinessCheckHistoryUpdate(BusinessCheck businessCheck, BusinessCheck existBusinessCheck, String userName, Integer userId, String option) {
        Map<String, Object> sourceMap = BeanUtil.beanToMap(existBusinessCheck);
        Map<String, Object> targetMap = BeanUtil.beanToMap(businessCheck);
        rename(sourceMap);
        rename(targetMap);
        CompareMapUtil.compareMap(sourceMap, targetMap);
        BusinessCheckHistory businessCheckHistory = new BusinessCheckHistory();
        businessCheckHistory.setBusinessCheckId(businessCheck.getId());
        businessCheckHistory.setDepartment(businessCheck.getDepartment());
        businessCheckHistory.setOpration(option);
        businessCheckHistory.setCreateUser(userName);
        businessCheckHistory.setCreateUserId(StrUtil.toString(userId));
        businessCheckHistory.setSourceContent(JacksonUtil.toJSON(sourceMap));
        businessCheckHistory.setTargetContent(JacksonUtil.toJSON(targetMap));
        if (MapUtil.isNotEmpty(sourceMap) || MapUtil.isNotEmpty(targetMap)) {
            businessCheckHistoryService.save(businessCheckHistory);
        }
    }

    private void rename(Map<String, Object> map) {
        if (map == null) {
            return;
        }
        map.entrySet().removeIf(v -> !dict.containsKey(v.getKey()));
        List<String> keys = new ArrayList<>(map.keySet());
        for (String key : keys) {
            map.put(dict.get(key), map.remove(key));
        }
    }
}
