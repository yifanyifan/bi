package com.stnts.bi.datamanagement.module.cooperation.controller;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.common.BiSessionUtil;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.constant.BooleanEnum;
import com.stnts.bi.datamanagement.constant.CooperationConstant;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.cooperation.entity.Cooperation;
import com.stnts.bi.datamanagement.module.cooperation.enums.CooperationTypeEnum;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationService;
import com.stnts.bi.datamanagement.module.cooperation.vo.CooperationVO;
import com.stnts.bi.datamanagement.util.JacksonSerializer;
import com.stnts.bi.entity.sys.UserEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 合作伙伴汇总表 前端控制器
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-03
 */
@RestController
@RequestMapping("/cooperation")
@Validated
@Api(value = "合作方管理", tags = {"合作方管理"})
public class CooperationController {

    private final CooperationService dmCooperationService;

    private final RedisTemplate redisTemplate;

    public CooperationController(CooperationService dmCooperationService, RedisTemplate redisTemplate) {
        this.dmCooperationService = dmCooperationService;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/get")
    public ResultEntity get(Long id, HttpServletRequest request) {
        Cooperation cooperation = dmCooperationService.getById(id);
        if (request != null) {
            UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
            if (cooperation.getIsProtection().equals(CooperationConstant.IS_PROTECTION_DEPARTMENT)) {
                // 部门保护
                if (!StrUtil.equals(cooperation.getCreateDepartmentCode(), StrUtil.sub(user.getCode(), 0, 5))) {
                    throw new BusinessException("部门保护数据，无法查看");
                }
            }
            if (cooperation.getIsProtection().equals(CooperationConstant.IS_PROTECTION_PERSON)) {
                // 私有保护
                if (!StrUtil.equals(cooperation.getCreateUserId(), user.getId().toString())) {
                    throw new BusinessException("私有保护数据，无法查看");
                }
            }
        }
        return ResultEntity.success(cooperation);
    }

    @GetMapping("/list")
    public ResultEntity list(Integer currentPage, Integer pageSize, String companyName, String cpName, String parentIndustry, String childIndustry, String keyword, Integer cooperationType, HttpServletRequest request) {
        Page<Cooperation> pageObj = new Page<>(Optional.ofNullable(currentPage).orElse(1), Optional.ofNullable(pageSize).orElse(10));
        //页面暂不查出dataSource=6【CRM】公司数据
        Page<Cooperation> cooperationList = dmCooperationService.page(pageObj, new QueryWrapper<Cooperation>().lambda()
                .like(StrUtil.isNotEmpty(companyName), Cooperation::getCompanyName, companyName)
                .like(StrUtil.isNotEmpty(cpName), Cooperation::getCpName, cpName)
                .like(StrUtil.isNotEmpty(parentIndustry), Cooperation::getParentIndustry, parentIndustry)
                .like(StrUtil.isNotEmpty(childIndustry), Cooperation::getChildIndustry, childIndustry)
                .eq(ObjectUtil.isNotNull(cooperationType), Cooperation::getCooperationType, cooperationType)
                .and(StrUtil.isNotEmpty(keyword), wrapper ->
                        wrapper.like(Cooperation::getId, keyword)
                                .or().like(Cooperation::getCompanyName, keyword)
                                .or().like(Cooperation::getCpName, keyword)
                                .or().like(Cooperation::getCreateUser, keyword)
                                .or().like(Cooperation::getCompanyContact, keyword))
                .ne(Cooperation::getDataSource, 6)
        );

        cooperationList.getRecords().forEach(v -> {
            if (CooperationConstant.COMPANY_TYPE_PROXY.equals(v.getCompanyType())) {
                v.setCompanyTypeDisplay(CooperationConstant.COMPANY_TYPE_PROXY_NAME);
            } else if (CooperationConstant.COMPANY_TYPE_DIRECT.equals(v.getCompanyType())) {
                v.setCompanyTypeDisplay(CooperationConstant.COMPANY_TYPE_DIRECT_NAME);
            }

            if (CooperationConstant.COOPERATION_TYPE_UP.equals(v.getCooperationType())) {
                v.setCooperationTypeDisplay(CooperationConstant.COOPERATION_TYPE_UP_NAME);
            } else if (CooperationConstant.COOPERATION_TYPE_DOWN.equals(v.getCooperationType())) {
                v.setCooperationTypeDisplay(CooperationConstant.COOPERATION_TYPE_DOWN_NAME);
            }
        });

        if (request != null) {
            UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
            for (Cooperation cooperation : cooperationList.getRecords()) {
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

    private void secretCooperation(Cooperation cooperation) {
        String secret = "***";
        cooperation.setIsProtectionActive(BooleanEnum.True.getKey());
        cooperation.setCompanyName(secret);
        cooperation.setCompanyType(JacksonSerializer.integerSecret);
        cooperation.setCompanyTypeDisplay(secret);
        cooperation.setCompanyTaxkey(secret);
        cooperation.setCompanyLegal(secret);
        cooperation.setCompanyTel(secret);
        cooperation.setCompanyContact(secret);
        cooperation.setContactPhone(secret);
        cooperation.setContactMail(secret);
        cooperation.setContactFax(secret);
        cooperation.setCompanyWebsite(secret);
        cooperation.setCompanyAddress(secret);
        cooperation.setCompanyDesc(secret);
        cooperation.setCompanyProducts(secret);
        cooperation.setCompanySize(JacksonSerializer.integerSecret);
        cooperation.setCompanyQualification(secret);
        cooperation.setEasCode(secret);
        cooperation.setCpLevel(JacksonSerializer.integerSecret);
        cooperation.setCpDetail(secret);
        cooperation.setRelatedProducts(secret);
        cooperation.setLastOrderTime(JacksonSerializer.dateSecret);
        cooperation.setIsRelateContract(JacksonSerializer.integerSecret);
        cooperation.setContractIdSet(secret);
    }

    @GetMapping("/listall")
    public ResultEntity listAll(String companyName, String cpName, String parentIndustry, String childIndustry, Integer cooperationType) {
        //参数处理
        emptyParam(cooperationType);

        List<Cooperation> cooperationList = dmCooperationService.list(new QueryWrapper<Cooperation>().lambda()
                .like(StrUtil.isNotEmpty(companyName), Cooperation::getCompanyName, companyName)
                .like(StrUtil.isNotEmpty(cpName), Cooperation::getCpName, cpName)
                .like(StrUtil.isNotEmpty(parentIndustry), Cooperation::getParentIndustry, parentIndustry)
                .like(StrUtil.isNotEmpty(childIndustry), Cooperation::getChildIndustry, childIndustry)
                .eq(ObjectUtil.isNotNull(cooperationType), Cooperation::getCooperationType, cooperationType)
        );
        return ResultEntity.success(cooperationList);
    }

    public void emptyParam(Integer cooperationType) {
        List<String> errorMsg = new ArrayList<String>();
        if (ObjectUtil.isNotEmpty(cooperationType)) {
            CooperationTypeEnum cooperationTypeEnum = CooperationTypeEnum.getByKey(cooperationType);
            if (ObjectUtil.isEmpty(cooperationTypeEnum)) {
                errorMsg.add("公司类型参数不正确");
            }
        }
        if (errorMsg.size() > 0) {
            throw new BusinessException(String.join(",", errorMsg));
        }
    }

    @GetMapping("/companyname/list")
    public ResultEntity listCompanyName(String companyName) {
        List<Cooperation> cooperationList = dmCooperationService.list(new QueryWrapper<Cooperation>()
                .lambda()
                .select(Cooperation::getCompanyName)
                .like(StrUtil.isNotEmpty(companyName), Cooperation::getCompanyName, companyName)
                //全展示 丁20210816
                //.eq(Cooperation::getCooperationType, 2)
                .last("limit 100"));
        return ResultEntity.success(cooperationList.stream().map(Cooperation::getCompanyName).distinct().collect(Collectors.toList()));
    }

    @GetMapping("/companyname/all")
    public ResultEntity listCompanyNameAll(String companyName, String companyId) {
        List<Cooperation> cooperationList = dmCooperationService.list(new QueryWrapper<Cooperation>()
                .select("id", "company_name", "cooperation_type").lambda()
                .eq(StringUtils.isNotBlank(companyId), Cooperation::getId, companyId)
                .like(StringUtils.isNotBlank(companyName), Cooperation::getCompanyName, companyName)
                .last("limit 100"));
        //update by yf 20210726
        //.eq(Cooperation::getCooperationType, 2)
        Stream<CooperationVO> voStream = cooperationList.stream().map(c -> {
            CooperationVO cooperationVO = new CooperationVO();
            cooperationVO.setId(c.getId());
            cooperationVO.setCpName(c.getCompanyName());
            cooperationVO.setCpType(c.getCooperationType() == 1 ? "客户" : "供应商");
            return cooperationVO;
        });
        return ResultEntity.success(voStream);
    }


    @GetMapping("/cpname/list")
    public ResultEntity listCpName(String cpName) {
        List<Cooperation> cooperationList = dmCooperationService.list(new QueryWrapper<Cooperation>()
                .lambda()
                .select(Cooperation::getCpName)
                .like(StrUtil.isNotEmpty(cpName), Cooperation::getCpName, cpName)
                .isNotNull(Cooperation::getCpName)
                .ne(Cooperation::getCpName, "")
                .last("limit 100"));
        return ResultEntity.success(cooperationList.stream().map(Cooperation::getCpName).distinct().collect(Collectors.toList()));
    }

    @PostMapping("/status/switch")
    public ResultEntity switchStatus(Long id, @Range(min = 1, max = 2, message = "status只能传1或2") @RequestParam(name = "status", required = true) Integer status, String remark, HttpServletRequest request) {
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        Integer userId = user.getId();
        String userName = user.getCnname();
        dmCooperationService.switchStatus(id, status, remark, userId, userName);
        return ResultEntity.success(null);
    }

    @ApiOperation(value = "获取下拉广告主列表")
    @GetMapping("listForOp")
    public ResultEntity<List<CooperationVO>> listForOp() {
        List<Cooperation> list = dmCooperationService.list(new QueryWrapper<Cooperation>()
                .select("id", "cp_name")
                .lambda()
                .eq(Cooperation::getIsTest, 0)
                .eq(Cooperation::getLastStatus, 1));
        List<CooperationVO> result = list.stream().map(c -> {
            CooperationVO cooperationVO = new CooperationVO();
            cooperationVO.setId(c.getId());
            cooperationVO.setCpName(c.getCpName());
            return cooperationVO;
        }).collect(Collectors.toList());
        return ResultEntity.success(result);
    }
}
