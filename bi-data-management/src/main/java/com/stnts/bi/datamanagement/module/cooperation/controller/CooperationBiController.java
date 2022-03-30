package com.stnts.bi.datamanagement.module.cooperation.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stnts.bi.common.BiSessionUtil;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.constant.CooperationConstant;
import com.stnts.bi.datamanagement.constant.DataSourceConstant;
import com.stnts.bi.datamanagement.constant.EnvironmentProperties;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.cooperation.entity.CooperationBi;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationBiService;
import com.stnts.bi.datamanagement.module.cooperation.vo.UserVO;
import com.stnts.bi.datamanagement.util.DozerUtil;
import com.stnts.bi.datamanagement.util.JacksonUtil;
import com.stnts.bi.datamanagement.util.JwtUtil;
import com.stnts.bi.datamanagement.util.SignUtil;
import com.stnts.bi.entity.sys.UserEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * <p>
 * 合作伙伴 源表（BI平台+订单系统） 前端控制器
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-03
 */
@RestController
@RequestMapping("/cooperation/bi")
@Slf4j
@Api(value = "BI合作伙伴", tags = {"BI合作伙伴"})
public class CooperationBiController {

    private final CooperationBiService cooperationBiService;

    private final EnvironmentProperties environmentProperties;

    private final SignUtil signUtil;

    private final JwtUtil jwtUtil;

    private final RedisTemplate redisTemplate;

    public CooperationBiController(CooperationBiService cooperationBiService, EnvironmentProperties environmentProperties, SignUtil signUtil, JwtUtil jwtUtil, RedisTemplate redisTemplate) {
        this.cooperationBiService = cooperationBiService;
        this.environmentProperties = environmentProperties;
        this.signUtil = signUtil;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    @ApiIgnore
    @PostMapping("/save")
    public ResultEntity save(@RequestBody CooperationBi cooperationBi, HttpServletRequest request) {
        if (request != null) {
            UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
            cooperationBi.setCreateUser(user.getCnname());
            cooperationBi.setCreateUserId(StrUtil.toString(user.getId()));
            cooperationBi.setCreateDepartmentCode(StrUtil.sub(user.getCode(), 0, 5));
            cooperationBi.setCreateDepartment(user.getDepartmentName());
        }
        //.eq(CooperationBi::getCpName, cooperationBi.getCpName())
        //.eq(StrUtil.isNotEmpty(cooperationBi.getCompanyTaxkey()), CooperationBi::getCompanyTaxkey, cooperationBi.getCompanyTaxkey())
        List<CooperationBi> existCooperation = cooperationBiService.list(new QueryWrapper<CooperationBi>().lambda()
                .eq(CooperationBi::getCompanyName, cooperationBi.getCompanyName())
                .eq(CooperationBi::getCooperationType, cooperationBi.getCooperationType())
        );
        if (CollectionUtil.isNotEmpty(existCooperation)) {
            throw new BusinessException("合作方已存在，合作方全称+合作方类型");
        }

        if (StrUtil.isNotEmpty(cooperationBi.getCompanyQualification())) {
            String[] fileArray = StrUtil.splitToArray(cooperationBi.getCompanyQualification(), ',');
            for (String split : fileArray) {
                String tempPath = System.getProperty("java.io.tmpdir") + File.separator;
                File source = new File(tempPath + File.separator + split);

                String staticLocations = "/webser/www/bi-data-management/static/";
                FileUtil.mkdir(staticLocations);
                File target = new File(staticLocations + split);
                FileUtil.copy(source, target, true);
            }
        }
        if (cooperationBi.getDataSource() == null) {
            cooperationBi.setDataSource(DataSourceConstant.dataSourceBI);
        }
        if (cooperationBi.getLastStatus() == null) {
            cooperationBi.setLastStatus(CooperationConstant.LAST_STATUS_OFF);
        }
        cooperationBiService.saveCooperation(cooperationBi);
        return ResultEntity.success(null);
    }

    @ApiIgnore
    @PostMapping("/update")
    public ResultEntity update(@RequestBody CooperationBi cooperationBi, HttpServletRequest request) {
        if (request != null) {
            UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
            cooperationBi.setUpdateUser(user.getCnname());
            cooperationBi.setUpdateUserId(StrUtil.toString(user.getId()));
        }
        List<CooperationBi> existCooperation = cooperationBiService.list(new QueryWrapper<CooperationBi>().lambda()
                .eq(CooperationBi::getCompanyName, cooperationBi.getCompanyName())
                .eq(CooperationBi::getCpName, cooperationBi.getCpName())
                .eq(CooperationBi::getCooperationType, cooperationBi.getCooperationType())
                .eq(StrUtil.isNotEmpty(cooperationBi.getCompanyTaxkey()), CooperationBi::getCompanyTaxkey, cooperationBi.getCompanyTaxkey())
                .ne(CooperationBi::getId, cooperationBi.getId())
        );
        if (CollectionUtil.isNotEmpty(existCooperation)) {
            throw new BusinessException("合作方已存在");
        }
        cooperationBiService.updateCooperation(cooperationBi);
        return ResultEntity.success(null);
    }

    @ApiIgnore
    @GetMapping("/get")
    public ResultEntity get(Long id, HttpServletRequest request) {
        CooperationBi cooperation = cooperationBiService.getById(id);
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
        return ResultEntity.success(cooperation);
    }

    @ApiIgnore
    @PostMapping("/remove")
    public ResultEntity remove(Long id) {
        cooperationBiService.remove(id);
        return ResultEntity.success(null);
    }

    @ApiIgnore
    @GetMapping("/user/list")
    public ResultEntity queryUserTree() {
        List<UserVO> result = cooperationBiService.queryUserTree();
        return ResultEntity.success(result);
    }

    @ApiOperation(value = "部门列表【带权限控制】")
    @GetMapping("/department/list")
    public ResultEntity departmentList(HttpServletRequest request) {
        List data = cooperationBiService.departmentList(request);
        return ResultEntity.success(data);
    }

    @ApiOperation(value = "部门列表【所有】")
    @GetMapping("/department/listall")
    public ResultEntity departmentListAll(HttpServletRequest request) {
        List data = cooperationBiService.departmentListAll(request);
        return ResultEntity.success(data);
    }

    @ApiIgnore
    @PostMapping("/upload")
    public ResultEntity upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("上传失败，请选择文件");
        }
        String fileName = file.getOriginalFilename();
        String extName = FileUtil.extName(fileName);
        String mainName = FileUtil.mainName(fileName);

        String newFileName = StrUtil.format("{}.{}", IdUtil.fastUUID(), extName);

        String qualificationStaticPath = "qualification";
        String tempPath = System.getProperty("java.io.tmpdir") + File.separator + qualificationStaticPath + File.separator;
        FileUtil.mkdir(tempPath);
        File dest = new File(tempPath + newFileName);

        try {
            file.transferTo(dest);
            log.info("上传成功");
        } catch (IOException e) {
            log.error(e.toString(), e);
        }
        return ResultEntity.success(qualificationStaticPath + "/" + newFileName);
    }
}
