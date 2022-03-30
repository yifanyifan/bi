package com.stnts.bi.sys.feign;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.vo.DmVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/5/26
 */
@FeignClient(value = "bi-data-management")
public interface DataManagementClient {

    @RequestMapping(value = "datamanagement/sys/dms", method = RequestMethod.GET)
    ResultEntity<List<DmVO>> dms(@RequestParam(required = false, name = "keyword") String keyword,
                                 @RequestParam(name = "departmentCode") List<String> departmentCodes);
}
