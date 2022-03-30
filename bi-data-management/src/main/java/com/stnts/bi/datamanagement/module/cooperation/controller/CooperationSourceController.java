package com.stnts.bi.datamanagement.module.cooperation.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.common.BiSessionUtil;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.constant.BooleanEnum;
import com.stnts.bi.datamanagement.constant.CooperationConstant;
import com.stnts.bi.datamanagement.module.cooperation.entity.CooperationSource;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationSourceService;
import com.stnts.bi.entity.sys.UserEntity;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * VIEW 前端控制器
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-10
 */
@RestController
@RequestMapping("/cooperation/source")
public class CooperationSourceController {

    private final CooperationSourceService cooperationSourceService;

    private final RedisTemplate redisTemplate;

    public CooperationSourceController(CooperationSourceService cooperationSourceService, RedisTemplate redisTemplate) {
        this.cooperationSourceService = cooperationSourceService;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/list")
    public ResultEntity list(Integer currentPage, Integer pageSize, String companyName, String cpName, String keyword, Long relatedCooperationId, HttpServletRequest request) {
        Page<CooperationSource> pageObj = new Page<>(Optional.ofNullable(currentPage).orElse(1), Optional.ofNullable(pageSize).orElse(10));
        Page<CooperationSource> cooperationList = cooperationSourceService.page(pageObj, new QueryWrapper<CooperationSource>().lambda()
                .like(StrUtil.isNotEmpty(companyName), CooperationSource::getCompanyName, companyName)
                .like(StrUtil.isNotEmpty(cpName), CooperationSource::getCpName, cpName)
                .eq(relatedCooperationId != null, CooperationSource::getRelatedCooperationId, relatedCooperationId)
                .and(StrUtil.isNotEmpty(keyword), wrapper ->
                        wrapper.like(CooperationSource::getId, keyword)
                                .or().like(CooperationSource::getCompanyName, keyword)
                                .or().like(CooperationSource::getCpName, keyword)
                                .or().like(CooperationSource::getCreateUser, keyword)
                                .or().like(CooperationSource::getCompanyContact, keyword))
                .ne(CooperationSource::getDataSource, 6)
        );

        cooperationList.getRecords().forEach(v -> {
            if (CooperationConstant.COOPERATION_TYPE_UP.equals(v.getCooperationType())) {
                v.setCooperationTypeDisplay(CooperationConstant.COOPERATION_TYPE_UP_NAME);
            } else if (CooperationConstant.COOPERATION_TYPE_DOWN.equals(v.getCooperationType())) {
                v.setCooperationTypeDisplay(CooperationConstant.COOPERATION_TYPE_DOWN_NAME);
            }
        });

        if (request != null) {
            UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
            for (CooperationSource cooperation : cooperationList.getRecords()) {
                if (cooperation.getIsProtection() == 1) {
                    // 部门保护
                    if (!StrUtil.equals(cooperation.getCreateDepartmentCode(), StrUtil.sub(user.getCode(), 0, 5))) {
                        secretCooperation(cooperation);
                    }
                }
                if (cooperation.getIsProtection() == 2) {
                    // 私有保护
                    if (!StrUtil.equals(cooperation.getCreateUserId(), user.getId().toString())) {
                        secretCooperation(cooperation);
                    }
                }
            }
        }
        return ResultEntity.success(cooperationList);
    }

    private void secretCooperation(CooperationSource cooperation) {
        String secret = "***";
        cooperation.setIsProtectionActive(BooleanEnum.True.getKey());
        cooperation.setCompanyName(secret);
        cooperation.setCompanyTaxkey(secret);
        cooperation.setCompanyContact(secret);
        cooperation.setContactPhone(secret);
        cooperation.setCompanyWebsite(secret);
        cooperation.setCompanyAddress(secret);
    }

    @GetMapping("/companyname/list")
    public ResultEntity listCompanyName(String companyName) {
        List<CooperationSource> cooperationList = cooperationSourceService.list(new QueryWrapper<CooperationSource>()
                .lambda()
                .select(CooperationSource::getCompanyName)
                .like(StrUtil.isNotEmpty(companyName), CooperationSource::getCompanyName, companyName)
                .last("limit 100"));
        return ResultEntity.success(cooperationList.stream().map(CooperationSource::getCompanyName).distinct().collect(Collectors.toList()));
    }

    @GetMapping("/cpname/list")
    public ResultEntity listCpName(String cpName) {
        List<CooperationSource> cooperationList = cooperationSourceService.list(new QueryWrapper<CooperationSource>()
                .lambda()
                .select(CooperationSource::getCpName)
                .like(StrUtil.isNotEmpty(cpName), CooperationSource::getCpName, cpName)
                .isNotNull(CooperationSource::getCpName)
                .ne(CooperationSource::getCpName, "")
                .last("limit 100"));
        return ResultEntity.success(cooperationList.stream().map(CooperationSource::getCpName).distinct().collect(Collectors.toList()));
    }
}
