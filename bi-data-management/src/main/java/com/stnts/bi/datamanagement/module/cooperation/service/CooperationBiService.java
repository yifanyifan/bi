package com.stnts.bi.datamanagement.module.cooperation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stnts.bi.datamanagement.module.cooperation.entity.CooperationBi;
import com.stnts.bi.datamanagement.module.cooperation.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 合作伙伴 源表（BI平台+订单系统） 服务类
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-03
 */
public interface CooperationBiService extends IService<CooperationBi> {

    void saveCooperation(CooperationBi cooperationBi);

    void updateCooperation(CooperationBi cooperationBi);

    void remove(Long id);

    List departmentList(HttpServletRequest request);

    List departmentListAll(HttpServletRequest request);

    List<UserVO> queryUserTree();
}
