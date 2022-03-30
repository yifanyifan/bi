package com.stnts.bi.datamanagement.module.business.controller;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.common.BiSessionUtil;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.constant.BooleanEnum;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.business.entity.BusinessDict;
import com.stnts.bi.datamanagement.module.business.service.BusinessDictService;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelCooperation;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelProduct;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelPromotionAll;
import com.stnts.bi.datamanagement.module.channel.service.ChannelCooperationService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelProductService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelPromotionAllService;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationBiService;
import com.stnts.bi.entity.sys.UserEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务分类 前端控制器
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-08
 */
@RestController
@RequestMapping("/business/dict")
@Api(value = "业务考核分类管理", tags = {"业务考核分类管理"})
public class BusinessDictController {
    private static final Logger logger = LoggerFactory.getLogger(BusinessDictController.class);

    @Autowired
    private CooperationBiService cooperationBiService;
    @Autowired
    private BusinessDictService businessDictService;
    @Autowired
    private ChannelProductService channelProductService;
    @Autowired
    private ChannelCooperationService channelCooperationService;
    @Autowired
    private ChannelPromotionAllService channelPromotionAllService;
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/list")
    @ApiOperation(value = "业务分类-列表")
    public ResultEntity list(String keyword) {
        List<BusinessDict> businessDictList = businessDictService.list(new QueryWrapper<BusinessDict>()
                .lambda().eq(BusinessDict::getIsValid, BooleanEnum.True.getKey())
                .and(StrUtil.isNotBlank(keyword),
                        wrapper -> wrapper.like(BusinessDict::getFirstLevel, keyword)
                                .or().like(BusinessDict::getSecondLevel, keyword)
                                .or().like(BusinessDict::getThirdLevel, keyword))
        );
        return ResultEntity.success(businessDictList);
    }

    @ApiOperation(value = "业务分类-树状（返回有效数据）")
    @GetMapping("/tree")
    public ResultEntity<Map<String, Object>> tree(@ApiParam(name = "keyword", value = "部门名称或部门CODE") String keyword, HttpServletRequest request) {
        Map<String, Object> map = businessDictService.tree(keyword, request);

        return ResultEntity.success(map);
    }

    @ApiOperation(value = "业务分类-行状（返回有效数据）")
    @GetMapping("/row")
    public ResultEntity<Map<String, Object>> row(@ApiParam(name = "keyword", value = "部门名称或部门CODE") String keyword,
                                                 @ApiParam(name = "searchKey", value = "三级搜索项") String searchKey, HttpServletRequest request) {
        Map<String, Object> map = businessDictService.row(keyword, searchKey, request);

        return ResultEntity.success(map);
    }

    @ApiOperation("业务分类-业务分类")
    @GetMapping("/query")
    public ResultEntity query(String keyword) {
        List<BusinessDict> businessDictList = businessDictService.list(new QueryWrapper<BusinessDict>()
                .lambda()
                .eq(BusinessDict::getIsValid, BooleanEnum.True.getKey())
                .like(StrUtil.isNotBlank(keyword), BusinessDict::getDepartmentCode, keyword)
                .orderByAsc(BusinessDict::getRootLevel)
                .orderByAsc(BusinessDict::getFirstLevel)
                .orderByAsc(BusinessDict::getSecondLevel)
                .orderByAsc(BusinessDict::getThirdLevel));

        Map<Integer, List<BusinessDict>> collect = businessDictList.stream().collect(Collectors.groupingBy(BusinessDict::getId, Collectors.toList()));
        Map<Integer, String> map = collect.entrySet().stream().collect(Collectors.toMap(subEntry -> subEntry.getKey(),
                subEntry -> subEntry.getValue().stream().map(businessDict -> {
                    String splitChar = "-";
                    String value = StrUtil.join(splitChar, businessDict.getFirstLevel(), businessDict.getSecondLevel(), businessDict.getThirdLevel());
                    while (StrUtil.endWith(value, splitChar)) {
                        value = StrUtil.removeSuffix(value, splitChar);
                    }
                    return value;
                }).collect(Collectors.joining(""))));

        return ResultEntity.success(map);
    }

    @ApiOperation("业务分类-业务分类-通用")
    @GetMapping("/queryGeneral")
    public ResultEntity queryGeneral(BusinessDict businessDictParam) {
        List<BusinessDict> businessDictList = businessDictService.list(new QueryWrapper<BusinessDict>()
                .lambda()
                .eq(BusinessDict::getIsValid, BooleanEnum.True.getKey())
                .like(StrUtil.isNotBlank(businessDictParam.getDepartmentCode()), BusinessDict::getDepartmentCode, businessDictParam.getDepartmentCode())
                .like(StrUtil.isNotBlank(businessDictParam.getRootLevel()), BusinessDict::getRootLevel, businessDictParam.getRootLevel())
                .ge(ObjectUtil.isNotEmpty(businessDictParam.getUpdateTimeStart()), BusinessDict::getUpdateTime, businessDictParam.getUpdateTimeStart())
                .le(ObjectUtil.isNotEmpty(businessDictParam.getUpdateTimeEnd()), BusinessDict::getUpdateTime, businessDictParam.getUpdateTimeEnd())
                .orderByAsc(BusinessDict::getRootLevel)
                .orderByAsc(BusinessDict::getFirstLevel)
                .orderByAsc(BusinessDict::getSecondLevel)
                .orderByAsc(BusinessDict::getThirdLevel));
        return ResultEntity.success(businessDictList);
    }

    @ApiOperation("业务分类-一级")
    @GetMapping("/list/first")
    public ResultEntity listFirstLevel(String keyword) {
        List<BusinessDict> businessDictList = businessDictService.list(new QueryWrapper<BusinessDict>()
                .select("distinct first_level")
                .lambda().eq(BusinessDict::getIsValid, BooleanEnum.True.getKey())
                .like(StrUtil.isNotBlank(keyword), BusinessDict::getFirstLevel, keyword));
        return ResultEntity.success(businessDictList.stream().map(BusinessDict::getFirstLevel).collect(Collectors.toList()));
    }

    @ApiOperation("业务分类-二级")
    @GetMapping("/list/second")
    public ResultEntity listSecondLevel(String keyword) {
        List<BusinessDict> businessDictList = businessDictService.list(new QueryWrapper<BusinessDict>()
                .select("distinct second_level")
                .lambda().eq(BusinessDict::getIsValid, BooleanEnum.True.getKey())
                .like(StrUtil.isNotBlank(keyword), BusinessDict::getSecondLevel, keyword));
        return ResultEntity.success(businessDictList.stream().map(BusinessDict::getSecondLevel).collect(Collectors.toList()));
    }

    @ApiOperation("业务分类-三级")
    @GetMapping("/list/third")
    public ResultEntity listThirdLevel(String keyword) {
        List<BusinessDict> businessDictList = businessDictService.list(new QueryWrapper<BusinessDict>()
                .select("distinct third_level")
                .lambda().eq(BusinessDict::getIsValid, BooleanEnum.True.getKey())
                .like(StrUtil.isNotBlank(keyword), BusinessDict::getThirdLevel, keyword));
        return ResultEntity.success(businessDictList.stream().map(BusinessDict::getThirdLevel).collect(Collectors.toList()));
    }

    @PostMapping("/add/first")
    @ApiOperation(value = "业务考核分类-新增")
    public ResultEntity add(String firstLevel, String secondLevel, String thirdLevel) {
        BusinessDict businessDict = new BusinessDict();
        businessDict.setFirstLevel(firstLevel);
        businessDict.setSecondLevel(secondLevel);
        businessDict.setThirdLevel(thirdLevel);
        businessDictService.save(businessDict);
        return ResultEntity.success(businessDict);
    }


    /************************************************业务考核分类页面 By Yifan Start*****************************************************************************/

    @GetMapping("/search/all")
    @ApiOperation(value = "查询-搜索框全部集合 By Yf")
    public ResultEntity searchList(@ApiParam(name = "departmentCode", value = "部门Code") String departmentCode,
                                   @ApiParam(name = "businessLevel", value = "层级") String businessLevel,
                                   HttpServletRequest request) {
        Map<String, Object> mapAll = businessDictService.searchList(departmentCode, businessLevel, request);

        return ResultEntity.success(mapAll);
    }

    @GetMapping("/search/department")
    @ApiOperation(value = "查询-部门 By Yf")
    public ResultEntity departmentList(@ApiParam(name = "departmentCode", value = "部门Code") String departmentCode,
                                       @ApiParam(name = "businessLevel", value = "层级") String businessLevel,
                                       HttpServletRequest request) {
        List<Map<String, String>> data = businessDictService.departmentList(departmentCode, businessLevel, request);

        return ResultEntity.success(data);
    }

    @GetMapping("/first/list")
    @ApiOperation("业务分类-一级 By Yf")
    public ResultEntity firstLevel(@ApiParam(name = "keyword", value = "关键字") String keyword,
                                   @ApiParam(name = "departmentCode", value = "部门") String departmentCode,
                                   HttpServletRequest request) {
        List<String> firstLevel = businessDictService.firstLevel(keyword, departmentCode, request);

        return ResultEntity.success(firstLevel);
    }

    @GetMapping("/second/list")
    @ApiOperation("业务分类-二级 By Yf")
    public ResultEntity secondLevel(@ApiParam(name = "keyword", value = "关键字") String keyword,
                                    @ApiParam(name = "firstLevel", value = "一级业务分类") String firstLevel,
                                    HttpServletRequest request) {
        List<String> secondLevel = businessDictService.secondLevel(keyword, firstLevel, request);

        return ResultEntity.success(secondLevel);
    }

    @GetMapping("/third/list")
    @ApiOperation("业务分类-三级 By Yf")
    public ResultEntity thirdLevel(@ApiParam(name = "keyword", value = "关键字") String keyword,
                                   @ApiParam(name = "secondLevel", value = "二级业务分类") String secondLevel,
                                   HttpServletRequest request) {
        List<String> thirdLevel = businessDictService.thirdLevel(keyword, secondLevel, request);

        return ResultEntity.success(thirdLevel);
    }

    @GetMapping("/listPage")
    @ApiOperation(value = "业务考核分类分页页面 By Yf")
    public ResultEntity<Page<BusinessDict>> listPage(@ApiParam(name = "departmentCode", value = "部门Code") String departmentCode,
                                                     @ApiParam(name = "businessLevel", value = "层级") String businessLevel,
                                                     Integer currentPage, Integer pageSize,
                                                     HttpServletRequest request) {
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        if (ObjectUtil.isEmpty(user)) {
            logger.info("开始时，user为null");
        }

        Page<BusinessDict> businessDictList = businessDictService.listPage(departmentCode, businessLevel, currentPage, pageSize, request);

        return ResultEntity.success(businessDictList);
    }

    @GetMapping("/get")
    @ApiOperation(value = "业务考核分类查询 By Yf")
    public ResultEntity<BusinessDict> get(@ApiParam(name = "id", value = "业务考核ID") Long id) {
        BusinessDict BusinessDict = businessDictService.getById(id);
        return ResultEntity.success(BusinessDict);
    }

    @PostMapping("/save")
    @ApiOperation(value = "业务考核分类新增 By Yf")
    public ResultEntity<BusinessDict> save(@RequestBody BusinessDict businessDict, HttpServletRequest request) {
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        if (ObjectUtil.isNotEmpty(user)) {
            businessDict.setCreateUser(user.getCnname());
        }

        businessDict.setSecondLevel(StringUtils.isBlank(businessDict.getSecondLevel()) ? "-" : businessDict.getSecondLevel());
        businessDict.setThirdLevel(StringUtils.isBlank(businessDict.getThirdLevel()) ? "-" : businessDict.getThirdLevel());

        businessDictService.saveBusinessDict(businessDict);
        return ResultEntity.success(businessDict);
    }

    @PostMapping("/update")
    @ApiOperation(value = "业务考核分类更新 By Yf")
    public ResultEntity<BusinessDict> update(@RequestBody BusinessDict businessDict, HttpServletRequest request) {
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        if (ObjectUtil.isNotEmpty(user)) {
            businessDict.setUpdateUser(user.getCnname());
        }

        businessDict.setSecondLevel(StringUtils.isBlank(businessDict.getSecondLevel()) ? "-" : businessDict.getSecondLevel());
        businessDict.setThirdLevel(StringUtils.isBlank(businessDict.getThirdLevel()) ? "-" : businessDict.getThirdLevel());

        businessDictService.updateBusinessDict(businessDict);
        return ResultEntity.success(businessDict);
    }

    @PostMapping("/remove")
    @ApiOperation(value = "业务考核分类删除 By Yf", response = ResultEntity.class)
    public ResultEntity remove(Integer id) {
        int productNum = channelProductService.count(new LambdaQueryWrapper<ChannelProduct>().eq(ChannelProduct::getBusinessDictId, id));
        if (productNum > 0) {
            throw new BusinessException("有产品使用当前分类，无法删除");
        }
        int ccidNum = channelCooperationService.count(new LambdaQueryWrapper<ChannelCooperation>().eq(ChannelCooperation::getBusinessDictId, id));
        if (ccidNum > 0) {
            throw new BusinessException("有CCID使用当前分类，无法删除");
        }
        int pidAllNum = channelPromotionAllService.count(new LambdaQueryWrapper<ChannelPromotionAll>().eq(ChannelPromotionAll::getBusinessDictId, id));
        if (pidAllNum > 0) {
            throw new BusinessException("宽表使用当前分类，无法删除");
        }

        businessDictService.removeById(id);
        return ResultEntity.success(null);
    }

    /************************************************业务考核分类页面 By Yifan End*****************************************************************************/
}
