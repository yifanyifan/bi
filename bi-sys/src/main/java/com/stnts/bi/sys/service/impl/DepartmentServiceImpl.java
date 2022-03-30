package com.stnts.bi.sys.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.DepartmentEntity;
import com.stnts.bi.entity.sys.OrgEntity;
import com.stnts.bi.exception.BiException;
import com.stnts.bi.groups.InsertGroup;
import com.stnts.bi.groups.UpdateGroup;
import com.stnts.bi.mapper.sys.DepartmentMapper;
import com.stnts.bi.mapper.sys.OrgMapper;
import com.stnts.bi.sys.common.SysConfig;
import com.stnts.bi.sys.service.DepartmentService;
import com.stnts.bi.sys.utils.SysUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/5/27
 */
@Service
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private SysConfig sysConfig;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private OrgMapper orgMapper;

    @Override
    public ResultEntity<Page<DepartmentEntity>> list(String keyword, int pageNo) {

        try {
            Page<DepartmentEntity> page = SysUtil.toPage(pageNo, this.sysConfig.getPageSize());
            List<DepartmentEntity> departmentEntityList = departmentMapper.listDepartment(keyword, page);
            page.setRecords(departmentEntityList);
            return ResultEntity.success(page);
        }catch(Exception e){
            log.warn("部门列表查询出错, 异常信息: {}", e.getMessage());
            throw new BiException("部门列表查询出错, 异常信息: " + e.getMessage());
        }
    }

    @Override
    public ResultEntity<List<OrgEntity>> listOrg() {

        try {
            List<OrgEntity> orgEntityList = orgMapper.list();
            return ResultEntity.success(orgEntityList);
        }catch(Exception e){
            log.warn("组织列表查询出错, 异常信息: {}", e.getMessage());
            throw new BiException("组织列表查询出错, 异常信息: " + e.getMessage());
        }
    }

    @Override
    public ResultEntity<Boolean> bindOrg(DepartmentEntity departmentEntity) {
        try {

            orgMapper.bindOrg(departmentEntity.getId(), departmentEntity.getOrgId());
            return ResultEntity.success(true);
        }catch(Exception e){
            log.warn("绑定组织出错, 异常信息: {}", e.getMessage());
            throw new BiException("绑定组织出错, 异常信息: " + e.getMessage());
        }
    }

    @Override
    public ResultEntity<OrgEntity> addOrg(@Validated(InsertGroup.class) OrgEntity orgEntity) {

        try {
            orgMapper.insert(orgEntity);
            return ResultEntity.success(orgEntity);
        }catch(Exception e){
            log.warn("添加组织出错, 异常信息: {}", e.getMessage());
            throw new BiException("添加组织出错, 异常信息: " + e.getMessage());
        }
    }

    @Override
    public ResultEntity<Boolean> delOrg(int orgId) {
        try {
            Integer selectCount = departmentMapper.selectCount(new QueryWrapper<DepartmentEntity>().lambda().eq(DepartmentEntity::getOrgId, orgId));
            if(selectCount > 0){
                return ResultEntity.exception("当前组织关联了部门, 不允许删除");
            }
            orgMapper.deleteById(orgId);
            return ResultEntity.success(true);
        }catch(Exception e){
            log.warn("组织删除报错, 异常信息: {}", e.getMessage());
            throw new BiException("组织删除报错, 异常信息: " + e.getMessage());
        }
    }

    @Override
    public ResultEntity<OrgEntity> updOrg(@Validated(UpdateGroup.class) OrgEntity orgEntity) {

        try {
            orgMapper.updateById(orgEntity);
            return ResultEntity.success(orgEntity);
        }catch(Exception e){
            log.warn("组织更新报错, 异常信息: {}", e.getMessage());
            throw new BiException("组织更新报错, 异常信息: " + e.getMessage());
        }
    }

    @Override
    public ResultEntity<List<DepartmentEntity>> all() {

        try {
            List<DepartmentEntity> list = departmentMapper.selectList(new QueryWrapper<DepartmentEntity>().lambda().eq(DepartmentEntity::getPid, 1));
            return ResultEntity.success(list);
        }catch(Exception e){
            log.warn("查询部门列表报错, 异常信息: {}", e.getMessage());
            throw new BiException("查询部门列表报错, 异常信息: " + e.getMessage());
        }
    }

    @Override
    public ResultEntity<List<OrgEntity>> allOrg() {
        try {
            //这里查的是 用户表中包含的
            List<OrgEntity> list = orgMapper.all();
            return ResultEntity.success(list);
        }catch(Exception e){
            log.warn("查询组织列表报错, 异常信息: {}", e.getMessage());
            throw new BiException("查询组织列表报错, 异常信息: " + e.getMessage());
        }
    }

    @Override
    public List<DepartmentEntity> listDepartmentByOrgId(List<Integer> orgIds) {
        try {
//            if(CollectionUtil.isEmpty(orgIds)){
//                throw new BiException("组织ID不允许为空");
//            }
            return departmentMapper.selectList(new LambdaQueryWrapper<DepartmentEntity>().eq(DepartmentEntity::getPid, 1).in(CollectionUtil.isNotEmpty(orgIds), DepartmentEntity::getOrgId, orgIds));
        }catch(Exception e){
            log.warn("查询部门列表报错, 异常信息: {}", e.getMessage());
            throw new BiException("查询部门列表报错, 异常信息: " + e.getMessage());
        }
    }
}
