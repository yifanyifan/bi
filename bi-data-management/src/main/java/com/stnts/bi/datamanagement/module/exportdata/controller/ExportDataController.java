package com.stnts.bi.datamanagement.module.exportdata.controller;

import com.alibaba.fastjson.JSON;
import com.stnts.bi.common.BiSessionUtil;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.module.exportdata.param.ExportDataParam;
import com.stnts.bi.datamanagement.module.exportdata.service.ExportDataService;
import com.stnts.bi.datamanagement.util.ExcelUtils;
import com.stnts.bi.entity.sys.UserEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 渠道推广 控制器
 *
 * @author 刘天元
 * @since 2021-02-04
 */
@Slf4j
@RestController
@RequestMapping("/exportData")
@Api(value = "数据导入", tags = {"数据导入"})
public class ExportDataController {
    @Autowired
    private ExportDataService exportDataService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @PostMapping("/addBatch")
    @ApiOperation(value = "导入批量数据", response = ResultEntity.class)
    public ResultEntity<Boolean> addBatch(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
        UserEntity user = null;
        if (request != null) {
            user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        }

        String[] columNames = {"pid", "pidAlias", "promoteDepartmentName", "ccidSettlement", "pidUsername", "year", "firstLevelBusiness", "secondLevelBusiness", "thirdLevelBusiness",
                "companyId", "channelName", "channelDepartmentName", "channelType", "secretType", "settlementType", "subChannelName", "ppName", "ppFlag", "plugId", "plugName", "productUsername",
                "productName", "productCode", "applicationName", "mediumName", "chargeRule", "channelShareType", "channelShareFlag", "channelShare", "channelShareStep", "price", "channelRate", "ccidUsername", "isWB", "isYDD"};
        List<Map<String, Object>> list = ExcelUtils.leading(file, columNames, 3);

        List<ExportDataParam> exportDataParamList = new ArrayList<ExportDataParam>();
        for (Map<String, Object> obj : list) {
            ExportDataParam exportDataParam = JSON.parseObject(JSON.toJSONString(obj), ExportDataParam.class);
            if (StringUtils.isBlank(exportDataParam.getProductCode())) {
                continue;
            }
            //页面来源默认BI
            if (StringUtils.isBlank(exportDataParam.getDataSource())) {
                exportDataParam.setDataSource("YOUTOP-IBK");
            }
            exportDataParamList.add(exportDataParam);
        }

        exportDataService.addBatch(exportDataParamList, user, request);

        return ResultEntity.success(null);
    }

    // flag
}

