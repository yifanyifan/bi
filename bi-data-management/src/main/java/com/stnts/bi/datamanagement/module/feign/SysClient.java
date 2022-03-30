package com.stnts.bi.datamanagement.module.feign;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.DepartmentEntity;
import com.stnts.bi.entity.sys.UserDmEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "bi-sys")
public interface SysClient {
    /**
     * 1. 通过组织ID获取部门列表
     * 2. 当组织ID为空时，则返回所有部门
     *
     * @param orgIds
     * @return
     */
    @RequestMapping(value = "/sys/api/listDepartmentByOrgId", method = RequestMethod.GET)
    List<DepartmentEntity> listDepartmentByOrgId(@RequestParam(required = false, name = "orgIds") List<Integer> orgIds);

    /**
     * 通过用户ID获取数据管理权限
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "sys/api/listDmByUserId", method = RequestMethod.GET)
    ResultEntity<List<UserDmEntity>> listDmByUserId(@RequestParam(required = false, name = "userId") String userId);


    /**
     * 根据CCID删除权限树
     *
     * @param ccid
     * @return
     */
    @RequestMapping(value = "sys/api/delDmByCcid", method = RequestMethod.GET)
    ResultEntity delDmByCcid(@RequestParam(required = false, name = "ccid") String ccid);
}
