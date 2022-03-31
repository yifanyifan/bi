package com.stnts.bi.datamanagement.schedule;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.extra.template.Template;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stnts.bi.datamanagement.constant.BooleanEnum;
import com.stnts.bi.datamanagement.module.cooperation.entity.CooperationEas;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationEasService;
import com.stnts.bi.datamanagement.util.TemplateHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author liutianyuan
 * @date 2019-09-25 14:47
 */


@Component
@Slf4j
public class EasCooperationSchedule {

    private final CooperationEasService cooperationEasService;

    public EasCooperationSchedule(CooperationEasService cooperationEasService) {
        this.cooperationEasService = cooperationEasService;
    }

    /**
     * 每天凌晨六点运行
     */
    //@Scheduled(cron="0 0 5 * * ?")
//    @Scheduled(fixedDelay = 100000)
    public void easCooperationSchedule() {
        List<CooperationEas> cooperationEasList = cooperationEasService.list(new QueryWrapper<CooperationEas>().lambda().select(CooperationEas::getEasCode));
        Set<String> existEasCodeSet = cooperationEasList.stream().map(CooperationEas::getEasCode).collect(Collectors.toSet());

        Template easCustomerTemplate = TemplateHelper.getTemplateEngine().getTemplate("easCustomer.sql");
        String easCustomerSql = easCustomerTemplate.render(Dict.create());
        easCustomerSql = StrUtil.removeAllLineBreaks(easCustomerSql);
        try {
            List<CooperationEas> entities = Db.use("oracle").query(easCustomerSql, CooperationEas.class);

            Set<String> effectEasCodeSet = new HashSet<>();
            Set<String> effectCompanyNameSet = new HashSet<>();
            for (CooperationEas entity : entities) {
                String companyName = entity.getCompanyName();
                String prefix = StrUtil.subBefore(companyName, '.', false);
                if(NumberUtil.isNumber(prefix)) {
                    effectEasCodeSet.add(entity.getEasCode());
                    effectCompanyNameSet.add(StrUtil.subAfter(companyName, '.', false));
                }
            }
            for (CooperationEas entity : entities) {
                String companyName = entity.getCompanyName();
                String prefix = StrUtil.subBefore(companyName, '.', false);
                if(!NumberUtil.isNumber(prefix)) {
                    if(entity.getIsApproved() == 1 || entity.getIsApproved() == 2) {
                        if(!effectCompanyNameSet.contains(companyName)) {
                            effectEasCodeSet.add(entity.getEasCode());
                        }
                    }
                }
            }

            for (CooperationEas cooperationEas : entities) {
                if(effectEasCodeSet.contains(cooperationEas.getEasCode())) {
                    if(StrUtil.isNotEmpty(cooperationEas.getContractIdSet())) {
                        cooperationEas.setIsRelateContract(BooleanEnum.True.getKey());
                    } else {
                        cooperationEas.setIsRelateContract(BooleanEnum.False.getKey());
                    }
                    if(existEasCodeSet.contains(cooperationEas.getEasCode())) {
                        cooperationEasService.updateCooperation(cooperationEas);
                    } else {
                        cooperationEasService.saveCooperation(cooperationEas);
                    }
                }
            }
        } catch (SQLException sqlException) {
            log.error("easCustomer.aql execute error", sqlException);
        }

        Template easSupplierTemplate = TemplateHelper.getTemplateEngine().getTemplate("easSupplier.sql");
        String easSupplierSql = easSupplierTemplate.render(Dict.create());
        easSupplierSql = StrUtil.removeAllLineBreaks(easSupplierSql);
        try {
            List<CooperationEas> entities = Db.use("oracle").query(easSupplierSql, CooperationEas.class);

            Set<String> effectEasCodeSet = new HashSet<>();
            Set<String> effectCompanyNameSet = new HashSet<>();
            for (CooperationEas entity : entities) {
                String companyName = entity.getCompanyName();
                String prefix = StrUtil.subBefore(companyName, '.', false);
                if(NumberUtil.isNumber(prefix)) {
                    effectEasCodeSet.add(entity.getEasCode());
                    effectCompanyNameSet.add(StrUtil.subAfter(companyName, '.', false));
                }
            }
            for (CooperationEas entity : entities) {
                String companyName = entity.getCompanyName();
                String prefix = StrUtil.subBefore(companyName, '.', false);
                if(!NumberUtil.isNumber(prefix)) {
                    if(entity.getIsApproved() == 1 || entity.getIsApproved() == 2) {
                        if(!effectCompanyNameSet.contains(companyName)) {
                            effectEasCodeSet.add(entity.getEasCode());
                        }
                    }
                }
            }

            for (CooperationEas cooperationEas : entities) {
                if(effectEasCodeSet.contains(cooperationEas.getEasCode())) {
                    if(StrUtil.isNotEmpty(cooperationEas.getContractIdSet())) {
                        cooperationEas.setIsRelateContract(BooleanEnum.True.getKey());
                    } else {
                        cooperationEas.setIsRelateContract(BooleanEnum.False.getKey());
                    }
                    if(existEasCodeSet.contains(cooperationEas.getEasCode())) {
                        cooperationEasService.updateCooperation(cooperationEas);
                    } else {
                        cooperationEasService.saveCooperation(cooperationEas);
                    }
                }
            }
        } catch (SQLException sqlException) {
            log.error("easCustomer.easSupplierSql execute error", sqlException);
        }
    }


}
