package com.stnts.bi.datamanagement.module.exportdata.service;

import com.stnts.bi.datamanagement.module.cooperation.vo.UserVO;
import com.stnts.bi.datamanagement.module.exportdata.param.ExportDataParam;
import com.stnts.bi.entity.sys.UserEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ExportDataService {

    List<UserVO> getUser();

    void addBatch(List<ExportDataParam> exportDataParamList, UserEntity user, HttpServletRequest request) throws Exception;

}
