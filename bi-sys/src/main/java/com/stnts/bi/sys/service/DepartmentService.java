package com.stnts.bi.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.DepartmentEntity;
import com.stnts.bi.entity.sys.OrgEntity;

import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/5/27
 */
public interface DepartmentService {

    /**
     *  组织管理
     * @param keyword
     * @return
     */
    ResultEntity<Page<DepartmentEntity>> list(String keyword, int page);

    /**
     * 组织列表
     * @return
     */
    ResultEntity<List<OrgEntity>> listOrg();

    /**
     * 部门绑定组织
     * @param departmentEntity
     * @return
     */
    ResultEntity<Boolean> bindOrg(DepartmentEntity departmentEntity);

    /**
     * 添加部门组织
     * @param orgEntity
     * @return
     */
    ResultEntity<OrgEntity> addOrg(OrgEntity orgEntity);

    /**
     * 删除组织
     * @param orgId
     * @return
     */
    ResultEntity<Boolean> delOrg(int orgId);

    /**
     * 修改组织名称
     * @param orgEntity
     * @return
     */
    ResultEntity<OrgEntity> updOrg(OrgEntity orgEntity);

    /**
     * 所有一级部门
     * @return
     */
    ResultEntity<List<DepartmentEntity>> all();

    /**
     * 所有部门组织
     * @return
     */
    ResultEntity<List<OrgEntity>> allOrg();

    /**
     * 通过组织ID获取部门列表
     * @param orgId
     * @return
     */
    List<DepartmentEntity> listDepartmentByOrgId(List<Integer> orgId);
}
