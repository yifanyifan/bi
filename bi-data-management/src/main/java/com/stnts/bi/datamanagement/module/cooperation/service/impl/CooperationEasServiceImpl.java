package com.stnts.bi.datamanagement.module.cooperation.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.stnts.bi.datamanagement.constant.CooperationConstant;
import com.stnts.bi.datamanagement.constant.DataSourceConstant;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.cooperation.entity.*;
import com.stnts.bi.datamanagement.module.cooperation.mapper.CooperationEasMapper;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationEasHistoryService;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationEasService;
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
 * 合作伙伴 源表（EAS金蝶） 服务实现类
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-03
 */
@Service
public class CooperationEasServiceImpl extends ServiceImpl<CooperationEasMapper, CooperationEas> implements CooperationEasService {
    private final Map<String, String> englishToChineseFieldNameMap = MapUtil.<String, String>builder().put("cooperationType", "合作方类型").put("cpName", "合作方简称")
            .put("cpCode", "助记码").put("companyName", "公司名称").put("companyType", "合作方式").put("companyTaxkey", "税务登记号")
            .put("companyLegal", "公司法人代表")
            .put("companyTel", "公司电话").put("companyContact", "公司联系人").put("contactPhone", "公司联系人手机")
            .put("contactMail", "联系邮箱").put("contactFax", "传真").put("companyWebsite", "公司网址").put("companyAddress", "公司地址")
            .put("parentIndustry", "所属行业（一级）").put("childIndustry", "所属行业（二级）").put("handlerUser", "经手人")
            .map();

    private final Map<String, Map<Integer, String>> fieldConstantValueMap = MapUtil.<String, Map<Integer, String>>builder()
            .put("cooperationType", MapUtil.<Integer, String>builder().put(1, "上游客户").put(2, "下游供应商").build())
            .put("companyType", MapUtil.<Integer, String>builder().put(1, "代理").put(2, "直客").build())
            .build();

    private final CooperationService dmCooperationService;

    private final CooperationEasHistoryService cooperationEasHistoryService;

    public CooperationEasServiceImpl(CooperationService dmCooperationService, CooperationEasHistoryService cooperationEasHistoryService) {
        this.dmCooperationService = dmCooperationService;
        this.cooperationEasHistoryService = cooperationEasHistoryService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveCooperation(CooperationEas cooperationEas) {
        cooperationEas.setDataSource(DataSourceConstant.dataSourceEAS);
        cooperationEas.setLastStatus(CooperationConstant.LAST_STATUS_ON);
        super.save(cooperationEas);
        Cooperation cooperation = DozerUtil.toBean(cooperationEas, Cooperation.class);
        if(NumberUtil.isNumber(StrUtil.subBefore(cooperation.getCompanyName(), '.', false)) ) {
            cooperation.setCompanyName(StrUtil.subAfter(cooperation.getCompanyName(), '.', false));
        }
        dmCooperationService.save(cooperation);
        CooperationEas updateCooperationEas = new CooperationEas();
        updateCooperationEas.setEasCode(cooperationEas.getEasCode());
        updateCooperationEas.setRelatedCooperationId(cooperation.getId());
        super.updateById(updateCooperationEas);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCooperation(CooperationEas cooperationEas) {
        cooperationEas.setDataSource(DataSourceConstant.dataSourceEAS);
        cooperationEas.setLastStatus(CooperationConstant.LAST_STATUS_ON);
        String easCode = cooperationEas.getEasCode();
        CooperationEas existCooperationEas = super.getById(easCode);
        if(ObjectUtil.isNull(existCooperationEas)) {
            throw new BusinessException(StrUtil.format("id为{}的合作伙伴不存在", easCode));
        }
        super.updateById(cooperationEas);
        Cooperation cooperation = DozerUtil.toBean(cooperationEas, Cooperation.class);
        cooperation.setId(existCooperationEas.getRelatedCooperationId());
        if(NumberUtil.isNumber(StrUtil.subBefore(cooperation.getCompanyName(), '.', false)) ) {
            cooperation.setCompanyName(StrUtil.subAfter(cooperation.getCompanyName(), '.', false));
        }
        dmCooperationService.updateById(cooperation);

        saveCooperationBiHistory(cooperationEas, existCooperationEas);
    }

    private void saveCooperationBiHistory(CooperationEas cooperationEas, CooperationEas existCooperationEas) {
        Map<String, Object> sourceMap = BeanUtil.beanToMap(existCooperationEas);
        Map<String, Object> targetMap = BeanUtil.beanToMap(cooperationEas);
        replaceConstant(sourceMap);
        replaceConstant(targetMap);
        rename(sourceMap);
        rename(targetMap);
        CompareMapUtil.compareMap(sourceMap, targetMap);
        if(MapUtil.isNotEmpty(sourceMap) || MapUtil.isNotEmpty(targetMap)) {
            CooperationEasHistory cooperationBiHistory = new CooperationEasHistory();
            cooperationBiHistory.setEasCode(cooperationEas.getEasCode());
            cooperationBiHistory.setCreateUser(existCooperationEas.getUpdateUser());
            cooperationBiHistory.setSourceContent(JacksonUtil.toJSON(sourceMap));
            cooperationBiHistory.setTargetContent(JacksonUtil.toJSON(targetMap));
            cooperationEasHistoryService.save(cooperationBiHistory);
        }
    }

    private void replaceConstant(Map<String, Object> map) {
        map.forEach((key, value) -> {
            if(fieldConstantValueMap.containsKey(key)) {
                Map<Integer, String> integerStringMap = fieldConstantValueMap.get(key);
                map.put(key, integerStringMap.get(value));
            }
        });
    }

    private void rename(Map<String, Object> map) {
        if(map == null) {
            return;
        }
        map.entrySet().removeIf(v -> !englishToChineseFieldNameMap.containsKey(v.getKey()));
        List<String> keys = new ArrayList<>(map.keySet());
        for (String key : keys) {
            map.put(englishToChineseFieldNameMap.get(key), map.remove(key));
        }
    }
}
