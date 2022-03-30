package com.stnts.bi.datamanagement.module.channel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelApplication;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelCooperation;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelProduct;
import com.stnts.bi.datamanagement.module.channel.param.ChannelProductPageParam;
import com.stnts.bi.datamanagement.module.channel.vo.ChannelProductVO;
import com.stnts.bi.datamanagement.module.channel.vo.DepartmentVO;
import com.stnts.bi.entity.common.PageEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 产品信息 服务类
 *
 * @author 刘天元
 * @since 2021-02-04
 */
public interface ChannelProductService extends IService<ChannelProduct> {

    /**
     * 保存
     *
     * @param channelProduct
     * @return
     * @throws Exception
     */
    ChannelProduct saveChannelProduct(ChannelProduct channelProduct) throws Exception;

    /**
     * 修改
     *
     * @param channelProduct
     * @return
     * @throws Exception
     */
    boolean updateChannelProduct(ChannelProduct channelProduct) throws Exception;

    /**
     * 删除
     *
     * @param id
     * @return
     * @throws Exception
     */
    boolean deleteChannelProduct(Long id) throws Exception;


    /**
     * 获取分页对象
     *
     * @param channelProductPageParam
     * @return
     * @throws Exception
     */
    PageEntity<ChannelProduct> getChannelProductPageList(ChannelProductPageParam channelProductPageParam, HttpServletRequest request) throws Exception;

    /**
     * 获取列表对象
     *
     * @param channelProductPageParam
     * @return
     * @throws Exception
     */
    List<ChannelProduct> getChannelProductList(ChannelProductPageParam channelProductPageParam, HttpServletRequest request);

    /**
     * 获取列表对象2
     *
     * @param channelProductPageParam
     * @return
     * @throws Exception
     */
    List<ChannelProduct> getChannelProductListToDept(ChannelProductPageParam channelProductPageParam);

    /**
     * @param channelProductPageParam
     * @return
     * @throws Exception
     */
    PageEntity<ChannelProduct> getChannelProductPageListExt(ChannelProductPageParam channelProductPageParam, HttpServletRequest request) throws Exception;

    /**
     * 搜索条件中部门列表
     *
     * @return
     */
    List<DepartmentVO> listDepartment(String cooperationMainName, HttpServletRequest request);

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    ChannelProduct getById(String productCode);

    List<ChannelApplication> getChannelProductAppList(ChannelProductPageParam channelProductPageParam, HttpServletRequest request);

    Map<String, Object> searchList(String departmentCode, String cooperationMainId, HttpServletRequest request);

    List<ChannelProduct> getChannelProductPageListExtGeneral(ChannelProductPageParam channelProductPageParam);

    ChannelProduct saveChannelProductGeneral(ChannelProduct channelProduct) throws Exception;

    List<String> departmentCodeAndNameVaild(String departmentCode, String departmentName);

    Boolean moveCPCompany(ChannelProductPageParam channelProductPageParam, HttpServletRequest request);

    List<ChannelCooperation> getCCIDListExt(ChannelProductPageParam channelProductPageParam, HttpServletRequest request);

    List<ChannelProduct> selectListProduct(ChannelProductPageParam channelProductPageParam);

    ChannelProduct getOneByParam(String departmentCode, String productCode);

    /**
     * 指定产品中，部门或推广部门 为 指定部门的 集合
     *
     * @param departmentCode
     * @param productCode
     * @return
     */
    List<ChannelProduct> getOneByParamList(String departmentCode, List<String> productCode);

    Map<String, List<String>> getProductFlagList();

    List<ChannelProduct> selectListByAppId(String valueOf);
}
