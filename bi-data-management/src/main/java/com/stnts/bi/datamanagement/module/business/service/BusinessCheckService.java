package com.stnts.bi.datamanagement.module.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.module.business.entity.BusinessCheck;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 业务考核 服务类
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-08
 */
public interface BusinessCheckService extends IService<BusinessCheck> {

    BusinessCheck saveBusinessCheck(BusinessCheck businessCheck, HttpServletRequest request);

    BusinessCheck updateBusinessCheck(BusinessCheck businessCheck, HttpServletRequest request);

    Map<String, Object> searchList(String departmentCode, String businessLevel, HttpServletRequest request);

    List<Map<String, String>> departmentList(String departmentCode, String businessLevel, HttpServletRequest request);

    Page<BusinessCheck> listPage(String department, String departmentCode, String businessLevel, Integer currentPage, Integer pageSize, HttpServletRequest request);
}
