package com.stnts.bi.datamanagement.module.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stnts.bi.datamanagement.module.business.entity.BusinessDict;
import com.stnts.bi.entity.sys.UserDmEntity;
import com.stnts.bi.entity.sys.UserEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 业务分类 服务类
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-08
 */
public interface BusinessDictService extends IService<BusinessDict> {
    BusinessDict saveBusinessDict(BusinessDict businessDict);

    BusinessDict updateBusinessDict(BusinessDict businessDict);

    List<String> getDepartmentListByPermissions(UserEntity user);

    Map<Integer, List<UserDmEntity>> getCCIDAndPidByPermissions(UserEntity user);

    Page<BusinessDict> listPage(String departmentCode, String businessLevel, Integer currentPage, Integer pageSize, HttpServletRequest request);

    Map<String, Object> searchList(String departmentCode, String businessLevel, HttpServletRequest request);

    List<Map<String, String>> departmentList(String departmentCode, String businessLevel, HttpServletRequest request);

    List<String> firstLevel(String keyword, String departmentCode, HttpServletRequest request);

    List<String> secondLevel(String keyword, String firstLevel, HttpServletRequest request);

    List<String> thirdLevel(String keyword, String secondLevel, HttpServletRequest request);

    Map<String, Object> tree(String keyword, HttpServletRequest request);

    Map<String, Object> row(String keyword, String searchKey, HttpServletRequest request);
}
