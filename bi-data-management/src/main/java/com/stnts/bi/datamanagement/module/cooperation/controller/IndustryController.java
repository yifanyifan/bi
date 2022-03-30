package com.stnts.bi.datamanagement.module.cooperation.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stnts.bi.common.BiSessionUtil;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.constant.BooleanEnum;
import com.stnts.bi.datamanagement.module.cooperation.entity.Industry;
import com.stnts.bi.datamanagement.module.cooperation.service.IndustryService;
import com.stnts.bi.entity.sys.UserEntity;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * BI系统维护的行业分类表 前端控制器
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-03
 */
@RestController
@RequestMapping("/cooperation/industry")
public class IndustryController {

    private final IndustryService industryService;

    private final RedisTemplate redisTemplate;

    public IndustryController(IndustryService industryService, RedisTemplate redisTemplate) {
        this.industryService = industryService;
        this.redisTemplate = redisTemplate;
    }

    /**
     * @param parentIndustry
     * @param childIndustry
     * @return
     */
    @GetMapping("/list")
    public ResultEntity list(String parentIndustry, String childIndustry) {
        List<Industry> industryList = industryService.list(new QueryWrapper<Industry>().lambda()
                .like(StrUtil.isNotEmpty(parentIndustry), Industry::getParentIndustry, parentIndustry)
                .like(StrUtil.isNotEmpty(childIndustry), Industry::getChildIndustry, childIndustry));
        return ResultEntity.success(industryList);
    }

    @GetMapping("/tree")
    public ResultEntity tree() {
        List<Industry> industryList = industryService.list(new QueryWrapper<Industry>().lambda().eq(Industry::getIsValid, BooleanEnum.True.getKey()));
        Map<String, Set<String>> result = industryList.stream().collect(Collectors.groupingBy(Industry::getParentIndustry, Collectors.mapping(Industry::getChildIndustry, Collectors.toSet())));
        return ResultEntity.success(result);
    }

    @GetMapping("/parent/list")
    public ResultEntity listParentIndustry(String keyword) {
        List<Industry> industryList = industryService.list(new QueryWrapper<Industry>()
                .select("distinct parent_industry")
                .lambda().eq(Industry::getIsValid, BooleanEnum.True.getKey()).like(StrUtil.isNotEmpty(keyword), Industry::getParentIndustry, keyword));
        return ResultEntity.success(industryList.stream().map(Industry::getParentIndustry).collect(Collectors.toList()));
    }

    @GetMapping("/child/list")
    public ResultEntity listChildIndustry(String keyword) {
        List<Industry> industryList = industryService.list(new QueryWrapper<Industry>()
                .select("distinct child_industry")
                .lambda().eq(Industry::getIsValid, BooleanEnum.True.getKey()).like(StrUtil.isNotEmpty(keyword), Industry::getChildIndustry, keyword));
        return ResultEntity.success(industryList.stream().map(Industry::getChildIndustry).collect(Collectors.toList()));
    }

    @PostMapping("/valid")
    public ResultEntity validIndustry(Integer id, Integer valid, HttpServletRequest request) {
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        Industry updateIndustry = new Industry();
        updateIndustry.setId(id);
        updateIndustry.setIsValid(valid);
        updateIndustry.setUpdateUser(user.getCnname());
        updateIndustry.setUpdateTime(LocalDateTime.now());
        industryService.updateById(updateIndustry);
        return ResultEntity.success(null);
    }

}
