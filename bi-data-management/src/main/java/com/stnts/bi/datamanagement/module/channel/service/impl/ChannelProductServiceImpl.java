package com.stnts.bi.datamanagement.module.channel.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stnts.bi.common.BiSessionUtil;
import com.stnts.bi.datamanagement.constant.EnvironmentProperties;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.business.entity.BusinessDict;
import com.stnts.bi.datamanagement.module.business.service.BusinessDictService;
import com.stnts.bi.datamanagement.module.channel.entity.*;
import com.stnts.bi.datamanagement.module.channel.mapper.*;
import com.stnts.bi.datamanagement.module.channel.param.ChannelProductPageParam;
import com.stnts.bi.datamanagement.module.channel.param.ChannelPromotionPageParam;
import com.stnts.bi.datamanagement.module.channel.service.*;
import com.stnts.bi.datamanagement.module.channel.vo.DepartmentVO;
import com.stnts.bi.datamanagement.module.cooperation.entity.Cooperation;
import com.stnts.bi.datamanagement.module.cooperation.mapper.CooperationMapper;
import com.stnts.bi.datamanagement.module.cooperation.vo.UserVO;
import com.stnts.bi.datamanagement.module.cooperationmain.entity.CooperationMain;
import com.stnts.bi.datamanagement.module.cooperationmain.mapper.CooperationMainMapper;
import com.stnts.bi.datamanagement.module.exportdata.service.ExportDataService;
import com.stnts.bi.datamanagement.module.feign.SysClient;
import com.stnts.bi.datamanagement.util.PidUtil;
import com.stnts.bi.entity.common.PageEntity;
import com.stnts.bi.entity.sys.DepartmentEntity;
import com.stnts.bi.entity.sys.UserDmEntity;
import com.stnts.bi.entity.sys.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ???????????? ???????????????
 *
 * @author ?????????
 * @since 2021-02-04
 */
@Slf4j
@Service
public class ChannelProductServiceImpl extends ServiceImpl<ChannelProductMapper, ChannelProduct> implements ChannelProductService {

    @Value("${data-management.setting.youtop-api-host}")
    private String youtopApiHost;

    @Autowired
    private ChannelProductMapper channelProductMapper;
    @Autowired
    private ChannelPromotionMapper channelPromotionMapper;
    @Autowired
    private EnvironmentProperties properties;
    @Autowired
    private ChannelApplicationService channelApplicationService;
    @Autowired
    private ChannelApplicationMapper channelApplicationMapper;
    @Autowired
    private ChannelBaseIdService channelBaseIdService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private BusinessDictService businessDictService;
    @Autowired
    private ChannelPromotionAllService channelPromotionAllService;
    @Autowired
    private ExportDataService exportDataService;
    @Autowired
    private SysClient sysClient;
    @Autowired
    private ChannelProductVendorHistoryMapper channelProductVendorHistoryMapper;
    @Autowired
    private ChannelProductCostMapper channelProductCostMapper;
    @Autowired
    private CooperationMainMapper cooperationMainMapper;
    @Autowired
    private CooperationMapper cooperationMapper;
    @Autowired
    private ChannelProductLabelService channelProductLabelService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ChannelProduct saveChannelProduct(ChannelProduct channelProduct) throws Exception {
        //????????????
        addChannelProductEmptyParam(channelProduct);

        super.save(channelProduct);

        //???????????????
        setFlagAndApp(channelProduct);

        return channelProduct;
    }

    public void addChannelProductEmptyParam(ChannelProduct channelProduct) {
        updateChannelProductEmptyParam(channelProduct);

        // ??????code????????????????????????????????????
        List<ChannelProduct> channelProducts = channelProductMapper.selectList(new QueryWrapper<ChannelProduct>().lambda().eq(ChannelProduct::getProductCode, channelProduct.getProductCode()));
        if (CollectionUtil.isNotEmpty(channelProducts)) {
            throw new BusinessException(String.format("??????code:%s?????????, ?????????", channelProduct.getProductCode()));
        }
        // ?????????????????????ID
        String pid = PidUtil.initProductId(youtopApiHost, channelProduct.getProductName(), channelProduct.getProductCode(), channelProduct.getDepartmentCode());
        if (StringUtils.isNotBlank(pid)) {
            channelProduct.setProductId(pid);
        }
        //??????CP????????????
        String vendorName = channelProduct.getVendorName();
        if (StringUtils.isNotBlank(vendorName)) {
            channelProduct.setVendorCheckStartDate(DateUtil.parse(DateUtil.format(new Date(), "yyyy-MM-dd") + " 00:00:00", "yyyy-MM-dd HH:mm:ss"));
            channelProduct.setVendorCheckEndDate(DateUtil.parse("2099-12-31 23:59:59", "yyyy-MM-dd HH:mm:ss"));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateChannelProduct(ChannelProduct channelProduct) throws Exception {
        //????????????
        updateChannelProductEmptyParam(channelProduct);

        ChannelProduct channelProductDB = channelProductMapper.selectOne(new LambdaQueryWrapper<ChannelProduct>().eq(ChannelProduct::getProductCode, channelProduct.getProductCode()));
        Integer channelPromotionCountByProd = channelPromotionMapper.selectCount(new LambdaQueryWrapper<ChannelPromotion>().eq(ChannelPromotion::getProductCode, channelProduct.getProductCode()));
        if (!channelProductDB.getDepartmentCode().equals(channelProduct.getDepartmentCode())) {
            if (channelPromotionCountByProd > 0) {
                throw new BusinessException("?????????PID???????????????????????????");
            }
        }
        //================????????????????????? Start==================================
        String saleDepartmentCodeDB = channelProductDB.getSaleDepartmentCode();
        String saleDepartmentCodeParam = channelProduct.getSaleDepartmentCode();
        if (StringUtils.isNotBlank(saleDepartmentCodeDB)) {
            List<String> saleDepartmentCodeDBList = new ArrayList<String>(Arrays.asList(saleDepartmentCodeDB.split(",")));
            List<String> saleDepartmentCodeParamList = StringUtils.isNotBlank(saleDepartmentCodeParam) ? new ArrayList<String>(Arrays.asList(saleDepartmentCodeParam.split(","))) : new ArrayList<String>();

            saleDepartmentCodeDBList.removeAll(saleDepartmentCodeParamList);
            if (CollectionUtil.isNotEmpty(saleDepartmentCodeDBList)) {
                // ?????? ????????? PID ??????
                String countPid = channelProductMapper.getDeleteSaleDepartCount(channelProductDB, saleDepartmentCodeDBList);
                if (StringUtils.isNotBlank(countPid)) {
                    throw new BusinessException("???????????????????????????" + countPid);
                }
            }
        }
        //================?????? Start==================================
        //????????? ??????????????????
        List<String> applicationIdListDB = new ArrayList<String>();
        if (StringUtils.isNotBlank(channelProductDB.getApplicationIds())) {
            applicationIdListDB = new ArrayList<String>(Arrays.asList(channelProductDB.getApplicationIds().split(",")));
        }
        //?????? ??????????????????
        List<String> applicationIdList = new ArrayList<String>();
        if (StringUtils.isNotBlank(channelProduct.getApplicationIds())) {
            applicationIdList = new ArrayList<String>(Arrays.asList(channelProduct.getApplicationIds().split(",")));
        }
        //??????A????????????????????????PID??????????????????????????????????????????
        if (CollectionUtil.isEmpty(applicationIdListDB) && CollectionUtil.isNotEmpty(applicationIdList)) {
            List<ChannelPromotion> channelPromotionList = channelPromotionMapper.selectList(new LambdaQueryWrapper<ChannelPromotion>().eq(ChannelPromotion::getProductCode, channelProductDB.getProductCode()));
            if (CollectionUtil.isNotEmpty(channelPromotionList)) {
                throw new BusinessException("????????????????????????????????????PID???????????????????????????");
            }
        }
        //?????????????????????????????????PID?????????
        applicationIdListDB.removeAll(applicationIdList);
        // ??????????????????
        if (CollectionUtil.isNotEmpty(applicationIdListDB)) {
            List<ChannelPromotion> channelPromotionList = channelPromotionMapper.selectList(new LambdaQueryWrapper<ChannelPromotion>().eq(ChannelPromotion::getProductCode, channelProduct.getProductCode()).in(ChannelPromotion::getApplicationId, applicationIdListDB));
            if (CollectionUtil.isNotEmpty(channelPromotionList)) {
                List<Long> appIdList = channelPromotionList.stream().map(ChannelPromotion::getApplicationId).distinct().collect(Collectors.toList());
                List<ChannelApplication> channelApplicationList = channelApplicationMapper.selectList(new LambdaQueryWrapper<ChannelApplication>().in(ChannelApplication::getId, appIdList));
                String applicationNameStr = channelApplicationList.stream().map(ChannelApplication::getApplicationName).collect(Collectors.joining(","));
                throw new BusinessException("?????????????????????PID???????????????????????????????????????" + applicationNameStr);
            }
        }
        if (CollectionUtil.isNotEmpty(applicationIdList)) {
            List<ChannelApplication> channelApplicationList = channelApplicationMapper.selectList(new LambdaQueryWrapper<ChannelApplication>().in(ChannelApplication::getId, applicationIdList));
            String applicationIdStr = channelApplicationList.stream().map(i -> String.valueOf(i.getId())).collect(Collectors.joining(","));
            String applicationNameStr = channelApplicationList.stream().map(ChannelApplication::getApplicationName).collect(Collectors.joining(","));
            channelProductDB.setApplicationIds(applicationIdStr);
            channelProductDB.setApplicationNames(applicationNameStr);
        } else {
            channelProductDB.setApplicationIds(null);
            channelProductDB.setApplicationNames(null);
        }

        channelProductDB.setDepartmentCode(channelProduct.getDepartmentCode());
        channelProductDB.setDepartmentName(channelProduct.getDepartmentName());
        channelProductDB.setCooperationMainId(channelProduct.getCooperationMainId());
        channelProductDB.setCooperationMainName(channelProduct.getCooperationMainName());
        channelProductDB.setProductName(channelProduct.getProductName());

        if (ObjectUtil.isEmpty(channelProduct.getVendorId()) || channelProduct.getVendorId() == 0) {
            channelProductDB.setVendorCheckStartDate(null);
            channelProductDB.setVendorCheckEndDate(null);
        } else {
            Long vendorIdDB = channelProductDB.getVendorId();
            Long vendorId = channelProduct.getVendorId();
            if (ObjectUtil.isEmpty(vendorIdDB) || !vendorIdDB.equals(vendorId)) {
                channelProductDB.setVendorCheckStartDate(DateUtil.parse(DateUtil.format(new Date(), "yyyy-MM-dd") + " 00:00:00", "yyyy-MM-dd HH:mm:ss"));
                channelProductDB.setVendorCheckEndDate(DateUtil.parse("2099-12-31 23:59:59", "yyyy-MM-dd HH:mm:ss"));
            }
        }
        channelProductDB.setVendorId(channelProduct.getVendorId());
        channelProductDB.setVendorName(channelProduct.getVendorName());
        channelProductDB.setBusinessDictId(channelProduct.getBusinessDictId());
        channelProductDB.setFirstLevelBusiness(channelProduct.getFirstLevelBusiness());
        channelProductDB.setSecondLevelBusiness(channelProduct.getSecondLevelBusiness());
        channelProductDB.setThirdLevelBusiness(channelProduct.getThirdLevelBusiness());
        channelProductDB.setSaleDepartmentCode(channelProduct.getSaleDepartmentCode());
        channelProductDB.setSaleDepartmentName(channelProduct.getSaleDepartmentName());
        channelProductDB.setProductFlag(channelProduct.getProductFlag());
        channelProductDB.setProductScreen(channelProduct.getProductScreen());
        channelProductDB.setProductClass(channelProduct.getProductClass());
        channelProductDB.setProductTheme(channelProduct.getProductTheme());
        channelProductDB.setUserid(channelProduct.getUserid());
        channelProductDB.setUsername(channelProduct.getUsername());
        channelProductMapper.updateById(channelProductDB);

        //?????????
        //channelProductDB.setApplicationList(channelApplicationList);
        channelPromotionAllService.updateProductThread(channelProductDB);
        return true;
    }

    //????????????
    public void updateChannelProductEmptyParam(ChannelProduct channelProduct) {
        //================???????????????????????????????????????H5???????????? Start==================================
        if (StringUtils.isNotBlank(channelProduct.getProductFlag())) {
            if (StringUtils.isBlank(channelProduct.getProductClass()) || StringUtils.isBlank(channelProduct.getProductTheme())) {
                throw new BusinessException("??????????????????????????????????????????????????????");
            }
            if ("H5".equals(channelProduct.getProductFlag()) && StringUtils.isBlank(channelProduct.getProductScreen())) {
                throw new BusinessException("???????????????H5????????????????????????");
            }
        }
        if ((StringUtils.isNotBlank(channelProduct.getProductClass()) && StringUtils.isBlank(channelProduct.getProductTheme())) || (StringUtils.isBlank(channelProduct.getProductClass()) && StringUtils.isNotBlank(channelProduct.getProductTheme()))) {
            throw new BusinessException("??????????????????????????????????????????");
        }
        if (StringUtils.isNotBlank(channelProduct.getProductClass())) {
            List<String> productClassIdList = new ArrayList<String>(Arrays.asList(channelProduct.getProductClass().split(",")));
            List<ChannelProductLabel> channelProductLabelOneList = channelProductLabelService.list(new LambdaQueryWrapper<ChannelProductLabel>()
                    .in(ChannelProductLabel::getId, productClassIdList)
                    .eq(ChannelProductLabel::getLabelLevel, "1")
            );
            if (channelProductLabelOneList.size() < productClassIdList.size()) {
                List<String> labelOneIdList = channelProductLabelOneList.stream().map(i -> String.valueOf(i.getId())).collect(Collectors.toList());
                productClassIdList.removeAll(labelOneIdList);
                throw new BusinessException("????????????ID????????????" + String.join(",", productClassIdList));
            }
        }
        if (StringUtils.isNotBlank(channelProduct.getProductTheme())) {
            List<String> productThemeIdList = new ArrayList<String>(Arrays.asList(channelProduct.getProductTheme().split(",")));
            List<ChannelProductLabel> channelProductLabelTwoList = channelProductLabelService.list(new LambdaQueryWrapper<ChannelProductLabel>()
                    .in(ChannelProductLabel::getId, productThemeIdList)
                    .eq(ChannelProductLabel::getLabelLevel, "2")
            );
            if (channelProductLabelTwoList.size() < productThemeIdList.size()) {
                List<String> labelTwoIdList = channelProductLabelTwoList.stream().map(i -> String.valueOf(i.getId())).collect(Collectors.toList());
                productThemeIdList.removeAll(labelTwoIdList);
                throw new BusinessException("????????????ID????????????" + String.join(",", productThemeIdList));
            }
        }
        //================???????????? Start==================================
        if (ObjectUtil.isNotEmpty(channelProduct.getUserid())) {
            List<UserVO> userVOList = exportDataService.getUser();
            List<UserVO> user = userVOList.stream().filter(i -> String.valueOf(channelProduct.getUserid()).equals(String.valueOf(i.getId()))).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(user)) {
                throw new BusinessException("???????????????");
            } else if (!user.get(0).getCnname().equals(channelProduct.getUsername())) {
                throw new BusinessException("??????ID?????????????????????");
            }
        } else {
            throw new BusinessException("??????ID????????????");
        }

        //================???????????? Start==================================
        if (StringUtils.isNotBlank(channelProduct.getApplicationIds())) {
            List<String> applicationIdList = new ArrayList<String>(Arrays.asList(channelProduct.getApplicationIds().split(",")));
            List<ChannelApplication> channelApplicationListDB = channelApplicationService.list(new LambdaQueryWrapper<ChannelApplication>().in(ChannelApplication::getId, applicationIdList));
            List<String> applicationNameList = channelApplicationListDB.stream().map(ChannelApplication::getApplicationName).collect(Collectors.toList());
            channelProduct.setApplicationNames(StringUtils.join(applicationNameList, ","));
        } else {
            channelProduct.setApplicationIds(null);
            channelProduct.setApplicationNames(null);
        }

        //================?????????????????? Start==================================
        if (ObjectUtil.isNotEmpty(channelProduct.getBusinessDictId())) {
            BusinessDict businessDict = businessDictService.getOne(new LambdaQueryWrapper<BusinessDict>().eq(BusinessDict::getId, channelProduct.getBusinessDictId()));
            if (ObjectUtil.isNotEmpty(businessDict)) {
                if (StringUtils.isNotBlank(channelProduct.getDepartmentCode()) && !businessDict.getDepartmentCode().equals(channelProduct.getDepartmentCode())) {
                    throw new BusinessException("??????????????????????????????");
                }

                channelProduct.setFirstLevelBusiness(businessDict.getFirstLevel());
                channelProduct.setSecondLevelBusiness(businessDict.getSecondLevel());
                channelProduct.setThirdLevelBusiness(businessDict.getThirdLevel());
            } else {
                throw new BusinessException("????????????ID?????????");
            }
        } else if (StringUtils.isNotBlank(channelProduct.getFirstLevelBusiness()) && StringUtils.isNotBlank(channelProduct.getSecondLevelBusiness()) && StringUtils.isNotBlank(channelProduct.getThirdLevelBusiness())) {
            List<BusinessDict> businessDictList = businessDictService.list(new LambdaQueryWrapper<BusinessDict>()
                    .eq(BusinessDict::getDepartmentCode, channelProduct.getDepartmentCode())
                    .eq(BusinessDict::getFirstLevel, channelProduct.getFirstLevelBusiness())
                    .eq(BusinessDict::getSecondLevel, channelProduct.getSecondLevelBusiness())
                    .eq(BusinessDict::getThirdLevel, channelProduct.getThirdLevelBusiness())
                    .le(BusinessDict::getYearStart, DateUtil.year(new Date()))
                    .ge(BusinessDict::getYearEnd, DateUtil.year(new Date()))
            );

            if (businessDictList.size() > 1) {
                throw new BusinessException("?????????????????????");
            } else if (businessDictList.size() == 0) {
                throw new BusinessException("?????????????????????");
            } else {
                channelProduct.setBusinessDictId(businessDictList.get(0).getId());
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteChannelProduct(Long id) throws Exception {
        ChannelProduct channelProduct = channelProductMapper.selectById(id);
        List<ChannelPromotion> promotions = channelPromotionMapper.selectList(new QueryWrapper<ChannelPromotion>().lambda().select(ChannelPromotion::getId)
                .eq(ChannelPromotion::getProductCode, channelProduct.getProductCode()));
        if (CollectionUtil.isNotEmpty(promotions)) {
            throw new BusinessException("?????????????????????PID,???????????????");
        }

        // ????????????????????????
        channelProductVendorHistoryMapper.delete(new LambdaQueryWrapper<ChannelProductVendorHistory>().eq(ChannelProductVendorHistory::getProductCode, channelProduct.getProductCode()));

        return super.removeById(id);
    }

    @Override
    public PageEntity<ChannelProduct> getChannelProductPageList(ChannelProductPageParam channelProductPageParam, HttpServletRequest request) throws Exception {
        // ?????? By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        channelProductPageParam.setDepartmentCodeAllList(departmentCodeAllList);

        Page<ChannelProduct> page = new Page<>(channelProductPageParam.getPageIndex(), channelProductPageParam.getPageSize());

        LambdaQueryWrapper<ChannelProduct> wrapper = new LambdaQueryWrapper<>();
        if (channelProductPageParam.getDepartmentCodeAllList().size() > 0) {
            wrapper.in(channelProductPageParam.getDepartmentCodeAllList().size() > 0, ChannelProduct::getDepartmentCode, channelProductPageParam.getDepartmentCodeAllList())
                    .and(i -> i.eq(StrUtil.isNotEmpty(channelProductPageParam.getKeyword()), ChannelProduct::getDepartmentCode, channelProductPageParam.getKeyword())
                            .or().eq(StrUtil.isNotEmpty(channelProductPageParam.getKeyword()), ChannelProduct::getDepartmentName, channelProductPageParam.getKeyword())
                    );
        } else {
            wrapper.eq(StrUtil.isNotEmpty(channelProductPageParam.getKeyword()), ChannelProduct::getDepartmentCode, channelProductPageParam.getKeyword())
                    .or().eq(StrUtil.isNotEmpty(channelProductPageParam.getKeyword()), ChannelProduct::getDepartmentName, channelProductPageParam.getKeyword());
        }

        IPage<ChannelProduct> iPage = channelProductMapper.selectPage(page, wrapper);
        return new PageEntity<ChannelProduct>(iPage);
    }

    @Override
    public List<ChannelProduct> getChannelProductList(ChannelProductPageParam channelProductPageParam, HttpServletRequest request) {
        // ?????? By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        channelProductPageParam.setDepartmentCodeAllList(departmentCodeAllList);

        List<ChannelProduct> channelProductListByDB = channelProductMapper.selectListProduct(channelProductPageParam);

        String applicationIds = channelProductListByDB.stream().filter(i -> StringUtils.isNotBlank(i.getApplicationIds())).map(ChannelProduct::getApplicationIds).collect(Collectors.joining(","));
        Map<String, ChannelApplication> channelApplicationMap = new HashMap<String, ChannelApplication>();
        if (StringUtils.isNotBlank(applicationIds)) {
            List<String> applicationIdList = new ArrayList<String>(Arrays.asList(applicationIds.split(",")));
            List<ChannelApplication> channelApplicationList = channelApplicationMapper.selectList(new LambdaQueryWrapper<ChannelApplication>().in(ChannelApplication::getId, applicationIdList));
            channelApplicationMap = channelApplicationList.stream().collect(Collectors.toMap(k -> String.valueOf(k.getId()), s -> s));
        }

        List<ChannelProduct> channelProductList = new ArrayList<ChannelProduct>();
        for (ChannelProduct channelProduct : channelProductListByDB) {
            String appIds = channelProduct.getApplicationIds();
            if (StringUtils.isNotBlank(appIds)) {
                List<String> appIdList = new ArrayList<String>(Arrays.asList(appIds.split(",")));
                for (String appId : appIdList) {
                    if (channelApplicationMap.containsKey(appId)) {
                        ChannelProduct channelProductNew = new ChannelProduct();
                        BeanUtils.copyProperties(channelProduct, channelProductNew, new String[]{"id"});
                        channelProductNew.setApplicationId(appId);
                        channelProductNew.setApplicationName(channelApplicationMap.get(appId).getApplicationName());
                        channelProductList.add(channelProductNew);
                    }
                }
            } else {
                ChannelProduct channelProductNew = new ChannelProduct();
                BeanUtils.copyProperties(channelProduct, channelProductNew, new String[]{"id"});
                channelProductList.add(channelProductNew);
            }
        }

        return channelProductList;
    }

    @Override
    public List<ChannelProduct> getChannelProductListToDept(ChannelProductPageParam channelProductPageParam) {
        List<ChannelProduct> channelProducts = channelProductMapper.listProductToDept(channelProductPageParam);
        return channelProducts;
    }

    @Override
    public PageEntity<ChannelProduct> getChannelProductPageListExt(ChannelProductPageParam channelProductPageParam, HttpServletRequest request) throws Exception {
        // ?????? By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        channelProductPageParam.setDepartmentCodeAllList(departmentCodeAllList);
        Map<Integer, List<UserDmEntity>> mapAll = businessDictService.getCCIDAndPidByPermissions(user);
        channelProductPageParam.setMapAll(mapAll);

        Page<ChannelProduct> page = new Page<>(channelProductPageParam.getPageIndex(), channelProductPageParam.getPageSize());
        List<OrderItem> orderItemList = CollectionUtil.isNotEmpty(channelProductPageParam.getOrders()) ? channelProductPageParam.getOrders() : new ArrayList<OrderItem>();
        orderColumn(orderItemList);
        page.setOrders(orderItemList);

        List<ChannelProduct> channelProducts = channelProductMapper.listProductToOrder(page, channelProductPageParam, user);

        if (CollectionUtil.isNotEmpty(channelProducts)) {
            ChannelPromotionPageParam channelPromotionPageParam = new ChannelPromotionPageParam();
            channelPromotionPageParam.setDepartmentCodeAllList(departmentCodeAllList);
            channelPromotionPageParam.setMapAll(mapAll);

            //????????????
            String applicationIds = channelProducts.stream().filter(i -> StringUtils.isNotBlank(i.getApplicationIds())).map(ChannelProduct::getApplicationIds).collect(Collectors.joining(","));
            if (StringUtils.isNotBlank(applicationIds)) {
                List<String> applicationIdList = new ArrayList<String>(Arrays.asList(applicationIds.split(",")));
                List<ChannelApplication> channelApplicationList = channelApplicationMapper.selectList(new LambdaQueryWrapper<ChannelApplication>().in(ChannelApplication::getId, applicationIdList));

                for (ChannelProduct channelProduct : channelProducts) {
                    if (StringUtils.isNotBlank(channelProduct.getApplicationIds())) {
                        List<String> appIds = new ArrayList<String>(Arrays.asList(channelProduct.getApplicationIds().split(",")));
                        List<ChannelApplication> appListFilter = channelApplicationList.stream().filter(i -> appIds.contains(String.valueOf(i.getId()))).collect(Collectors.toList());
                        channelProduct.setApplicationList(appListFilter);
                    }
                }
            }
        }

        return new PageEntity<>(page, channelProducts);
    }

    public void orderColumn(List<OrderItem> orderItemList) {
        if (CollectionUtil.isNotEmpty(orderItemList)) {
            List<String> errorOrder = new ArrayList<String>();

            Iterator<OrderItem> iterator = orderItemList.iterator();
            while (iterator.hasNext()) {
                OrderItem orderItem = iterator.next();
                if ("id".equals(orderItem.getColumn())) {
                    orderItem.setColumn("pt.id");
                    continue;
                } else if ("departmentName".equals(orderItem.getColumn())) {
                    orderItem.setColumn("pt.department_name");
                    continue;
                } else if ("cooperationMainName".equals(orderItem.getColumn())) {
                    orderItem.setColumn("pt.cooperation_main_name");
                    continue;
                } else if ("productCode".equals(orderItem.getColumn())) {
                    orderItem.setColumn("pt.product_code");
                    continue;
                } else if ("productName".equals(orderItem.getColumn())) {
                    orderItem.setColumn("pt.product_name");
                    continue;
                } else if ("applicationNames".equals(orderItem.getColumn())) {
                    orderItem.setColumn("pt.application_names");
                    continue;
                } else if ("saleDepartmentName".equals(orderItem.getColumn())) {
                    orderItem.setColumn("pt.sale_department_name");
                    continue;
                } else if ("ccidNum".equals(orderItem.getColumn())) {
                    orderItem.setColumn("ccidNum");
                    continue;
                } else if ("pidNum".equals(orderItem.getColumn())) {
                    orderItem.setColumn("pidNum");
                    continue;
                } else if ("usernameName".equals(orderItem.getColumn())) {
                    orderItem.setColumn("pt.username");
                    continue;
                } else if ("updateTime".equals(orderItem.getColumn())) {
                    orderItem.setColumn("pt.update_time");
                    continue;
                }
                errorOrder.add(orderItem.getColumn());
            }

            if (CollectionUtil.isNotEmpty(errorOrder)) {
                throw new BusinessException("?????????????????????" + org.apache.commons.lang.StringUtils.join(errorOrder, ","));
            }

        } else {
            OrderItem orderItem = new OrderItem();
            orderItem.setColumn("pt.id");
            orderItem.setAsc(false);
            orderItemList.add(orderItem);
        }
    }

    @Override
    public List<ChannelProduct> getChannelProductPageListExtGeneral(ChannelProductPageParam channelProductPageParam) {
        List<ChannelProduct> channelProducts = channelProductMapper.listProduct(null, channelProductPageParam);

        List<UserVO> userVOList = exportDataService.getUser();
        Map<Long, Long> userVOMap = userVOList.stream().collect(Collectors.toMap(s -> Long.valueOf(s.getId()), s -> Long.valueOf(s.getCardNumber())));
        String productBqAllStr = channelProducts.stream().map(i -> i.getProductClass() + "," + i.getProductTheme()).collect(Collectors.joining(","));
        List<ChannelProductLabel> channelProductLabelList = new ArrayList<ChannelProductLabel>();
        if (StringUtils.isNotBlank(productBqAllStr)) {
            List<String> productBqAllList = new ArrayList<String>(Arrays.asList(productBqAllStr.split(",")));
            channelProductLabelList = channelProductLabelService.list(new LambdaQueryWrapper<ChannelProductLabel>().in(ChannelProductLabel::getId, productBqAllList));
        }

        for (ChannelProduct channelProduct : channelProducts) {
            if (StringUtils.isNotBlank(channelProduct.getApplicationIds())) {
                List<String> appIdList = new ArrayList<String>(Arrays.asList(channelProduct.getApplicationIds().split(",")).stream().filter(i -> StringUtils.isNotBlank(i)).collect(Collectors.toList()));
                List<ChannelApplication> channelApplicationList = channelApplicationMapper.selectList(new LambdaQueryWrapper<ChannelApplication>().in(ChannelApplication::getId, appIdList));

                if (channelApplicationList != null && channelApplicationList.size() > 0) {
                    channelProduct.setApplicationList(channelApplicationList);
                }
            }
            if (channelProduct.getUserid() != null && userVOMap.containsKey(channelProduct.getUserid())) {
                channelProduct.setUserid(userVOMap.get(channelProduct.getUserid()));
            }
            if (StringUtils.isNotBlank(channelProduct.getProductClass())) {
                List<String> productClassList = Arrays.asList(channelProduct.getProductClass().split(","));
                List<String> productThemeList = Arrays.asList(channelProduct.getProductTheme().split(","));

                String classStr = channelProductLabelList.stream().filter(i -> productClassList.contains(String.valueOf(i.getId()))).map(ChannelProductLabel::getLabelValue).collect(Collectors.joining(","));
                String themeStr = channelProductLabelList.stream().filter(i -> productThemeList.contains(String.valueOf(i.getId()))).map(ChannelProductLabel::getLabelValue).collect(Collectors.joining(","));

                channelProduct.setProductClassStr(StringUtils.isNotBlank(classStr) ? classStr : null);
                channelProduct.setProductThemeStr(StringUtils.isNotBlank(themeStr) ? themeStr : null);
            }
        }

        return channelProducts;
    }

    @Override
    public ChannelProduct saveChannelProductGeneral(ChannelProduct channelProduct) throws Exception {
        //????????????
        emptyParam(channelProduct);
        //???????????????
        defaultParam(channelProduct);

        return this.saveChannelProduct(channelProduct);
    }


    @Override
    public Boolean moveCPCompany(ChannelProductPageParam channelProductPageParam, HttpServletRequest request) {
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();

        String productCode = channelProductPageParam.getProductCode();
        ChannelProduct channelProduct = channelProductMapper.selectOne(new LambdaQueryWrapper<ChannelProduct>().eq(ChannelProduct::getProductCode, productCode));

        if (ObjectUtil.isNotEmpty(channelProduct.getVendorId()) && StringUtils.isNotBlank(channelProduct.getVendorName())) {
            // ??????
            // 1. ?????????????????? ?????????????????? ??????????????????
            if (channelProductPageParam.getVendorCheckStartDate().getTime() <= channelProduct.getVendorCheckStartDate().getTime()) {
                throw new BusinessException("CP?????????????????????");
            }

            //??????CP??????????????????
            ChannelProductVendorHistory channelProductVendorHistory = new ChannelProductVendorHistory();
            channelProductVendorHistory.setProductCode(channelProduct.getProductCode());
            channelProductVendorHistory.setProductName(channelProduct.getProductName());
            channelProductVendorHistory.setVendorId(channelProduct.getVendorId());
            channelProductVendorHistory.setVendorName(channelProduct.getVendorName().replace("???????????????", "").replace("????????????", ""));
            channelProductVendorHistory.setVendorCheckStartDate(channelProduct.getVendorCheckStartDate());
            //??????????????????
            Date endDate = null;

            if (channelProductPageParam.getVendorCheckStartDate().getTime() > channelProduct.getVendorCheckEndDate().getTime()) {
                endDate = channelProduct.getVendorCheckEndDate();
            } else {
                Date date = DateUtils.addDays(channelProductPageParam.getVendorCheckStartDate(), -1);
                endDate = DateUtil.parse(DateUtil.format(date, "yyyy-MM-dd") + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
            }
            channelProductVendorHistory.setVendorCheckEndDate(endDate);
            channelProductVendorHistory.setUserid(Long.valueOf(user.getId()));
            channelProductVendorHistory.setUsername(user.getCnname());
            channelProductVendorHistoryMapper.insert(channelProductVendorHistory);
        }
        //???CP??????
        channelProduct.setVendorId(channelProductPageParam.getVendorId());
        channelProduct.setVendorName(channelProductPageParam.getVendorName().replace("???????????????", "").replace("????????????", ""));
        channelProduct.setVendorCheckStartDate(channelProductPageParam.getVendorCheckStartDate());
        channelProduct.setVendorCheckEndDate(channelProductPageParam.getVendorCheckEndDate());
        channelProductMapper.updateById(channelProduct);

        //?????????
        channelPromotionAllService.moveCPCompany(channelProduct);

        return true;
    }

    @Override
    public List<String> departmentCodeAndNameVaild(String departmentCode, String departmentName) {
        List<String> resultList = new ArrayList<String>();
        //??????CODE
        List<DepartmentEntity> departmentEntityList = sysClient.listDepartmentByOrgId(new ArrayList<Integer>());
        DepartmentEntity departmentEntityCode = null;
        DepartmentEntity departmentEntityName = null;
        if (StringUtils.isNotBlank(departmentCode)) {
            List<DepartmentEntity> departmentEntityListTmpCode = departmentEntityList.stream().filter(t -> t.getCode().equals(departmentCode)).collect(Collectors.toList());
            if (departmentEntityListTmpCode.size() > 1) {
                throw new BusinessException("??????Code?????????????????????");
            } else if (departmentEntityListTmpCode.size() == 0) {
                throw new BusinessException("??????Code?????????");
            } else {
                departmentEntityCode = departmentEntityListTmpCode.get(0);
            }
        }
        if (StringUtils.isNotBlank(departmentName)) {
            List<DepartmentEntity> departmentEntityListTmpName = departmentEntityList.stream().filter(t -> t.getName().equals(departmentName)).collect(Collectors.toList());
            if (departmentEntityListTmpName.size() > 1) {
                throw new BusinessException("?????????????????????????????????");
            } else if (departmentEntityListTmpName.size() == 0) {
                throw new BusinessException("?????????????????????");
            } else {
                departmentEntityName = departmentEntityListTmpName.get(0);
            }
        }
        if (ObjectUtil.isNotEmpty(departmentEntityCode) && ObjectUtil.isNotEmpty(departmentEntityName)) {
            if (!departmentEntityCode.getCode().equals(departmentEntityName.getCode()) || !departmentEntityCode.getName().equals(departmentEntityName.getName())) {
                throw new BusinessException("??????CODE????????????????????????");
            } else {
                resultList.add(departmentEntityCode.getCode());
                resultList.add(departmentEntityCode.getName());
            }
        } else if (ObjectUtil.isNotEmpty(departmentEntityCode) || ObjectUtil.isEmpty(departmentEntityName)) {
            resultList.add(departmentEntityCode.getCode());
            resultList.add(departmentEntityCode.getName());
        } else if (ObjectUtil.isEmpty(departmentEntityCode) || ObjectUtil.isNotEmpty(departmentEntityName)) {
            resultList.add(departmentEntityName.getCode());
            resultList.add(departmentEntityName.getName());
        }

        return resultList;
    }

    public void defaultParam(ChannelProduct channelProduct) {
        //??????CODE
        List<String> resultList = departmentCodeAndNameVaild(channelProduct.getDepartmentCode(), channelProduct.getDepartmentName());
        channelProduct.setDepartmentCode(resultList.get(0));
        channelProduct.setDepartmentName(resultList.get(1));
        // ????????????
        if (StringUtils.isNotBlank(channelProduct.getCooperationMainId())) {
            if (StringUtils.isBlank(channelProduct.getCooperationMainName())) {
                CooperationMain cooperationMain = cooperationMainMapper.selectOne(new LambdaQueryWrapper<CooperationMain>()
                        .eq(CooperationMain::getId, channelProduct.getCooperationMainId())
                        .eq(CooperationMain::getDepartmentCode, channelProduct.getDepartmentCode())
                );
                if (ObjectUtil.isNotEmpty(cooperationMain)) {
                    channelProduct.setCooperationMainName(cooperationMain.getCooperationMainName());
                } else {
                    throw new BusinessException("????????????????????????" + channelProduct.getDepartmentCode() + ":" + channelProduct.getCooperationMainId());
                }
            }
        } else {
            CooperationMain cooperationMain = cooperationMainMapper.selectOne(new LambdaQueryWrapper<CooperationMain>()
                    .eq(CooperationMain::getDepartmentCode, channelProduct.getDepartmentCode())
                    .eq(CooperationMain::getCooperationMainName, "????????????")
            );
            if (ObjectUtil.isNotEmpty(cooperationMain)) {
                channelProduct.setCooperationMainId(String.valueOf(cooperationMain.getId()));
                channelProduct.setCooperationMainName(cooperationMain.getCooperationMainName());
            } else {
                throw new BusinessException("????????????????????????" + channelProduct.getDepartmentCode() + ":????????????");
            }
        }
        // CP??????
        if (ObjectUtil.isNotEmpty(channelProduct.getVendorId())) {
            Cooperation cooperation = cooperationMapper.selectById(channelProduct.getVendorId());
            if (ObjectUtil.isEmpty(cooperation)) {
                throw new BusinessException("CP???????????????");
            } else {
                channelProduct.setVendorName(cooperation.getCompanyName());
            }
        }
        // ????????????
        if (ObjectUtil.isNotEmpty(channelProduct.getBusinessDictId())) {
            BusinessDict businessDict = businessDictService.getOne(new LambdaQueryWrapper<BusinessDict>().eq(BusinessDict::getId, channelProduct.getBusinessDictId()));
            if (ObjectUtil.isEmpty(businessDict)) {
                throw new BusinessException("?????????????????????");
            } else if (!businessDict.getDepartmentCode().equals(channelProduct.getDepartmentCode())
                    || !(businessDict.getYearStart() <= DateUtil.year(new Date()) && DateUtil.year(new Date()) <= businessDict.getYearEnd())
                    || ((StringUtils.isNotBlank(channelProduct.getFirstLevelBusiness())
                    && StringUtils.isNotBlank(channelProduct.getSecondLevelBusiness())
                    && StringUtils.isNotBlank(channelProduct.getThirdLevelBusiness()))
                    && (!channelProduct.getFirstLevelBusiness().equals(businessDict.getFirstLevel())
                    || !channelProduct.getSecondLevelBusiness().equals(businessDict.getSecondLevel())
                    || !channelProduct.getThirdLevelBusiness().equals(businessDict.getThirdLevel())))) {
                throw new BusinessException("????????????ID??????????????????????????????");
            } else {
                channelProduct.setFirstLevelBusiness(businessDict.getFirstLevel());
                channelProduct.setSecondLevelBusiness(businessDict.getSecondLevel());
                channelProduct.setThirdLevelBusiness(businessDict.getThirdLevel());
            }
        } else if (StringUtils.isNotBlank(channelProduct.getFirstLevelBusiness()) && StringUtils.isNotBlank(channelProduct.getSecondLevelBusiness()) && StringUtils.isNotBlank(channelProduct.getThirdLevelBusiness())) {
            List<BusinessDict> businessDictList = businessDictService.list(new LambdaQueryWrapper<BusinessDict>()
                    .eq(BusinessDict::getDepartmentCode, channelProduct.getDepartmentCode())
                    .eq(BusinessDict::getFirstLevel, channelProduct.getFirstLevelBusiness())
                    .eq(BusinessDict::getSecondLevel, channelProduct.getSecondLevelBusiness())
                    .eq(BusinessDict::getThirdLevel, channelProduct.getThirdLevelBusiness())
                    .le(BusinessDict::getYearStart, DateUtil.year(new Date()))
                    .ge(BusinessDict::getYearEnd, DateUtil.year(new Date()))
            );

            if (businessDictList.size() > 1) {
                throw new BusinessException("?????????????????????");
            } else if (businessDictList.size() == 0) {
                throw new BusinessException("?????????????????????");
            } else {
                channelProduct.setBusinessDictId(businessDictList.get(0).getId());
            }
        }
        //??????
        if (CollectionUtil.isNotEmpty(channelProduct.getApplicationList())) {
            Set<String> applicationNameList = channelProduct.getApplicationList().stream().map(ChannelApplication::getApplicationName).collect(Collectors.toSet());
            List<ChannelApplication> channelApplicationList = channelApplicationMapper.selectList(new LambdaQueryWrapper<ChannelApplication>().in(ChannelApplication::getApplicationName, applicationNameList));

            if (channelApplicationList.size() < applicationNameList.size()) {
                Set<String> applicationNameDBList = channelApplicationList.stream().map(i -> String.valueOf(i.getApplicationName())).collect(Collectors.toSet());
                applicationNameList.removeAll(applicationNameDBList);
                throw new BusinessException("????????????????????????" + applicationNameList.stream().collect(Collectors.joining(",")));
            }

            String applicationIdStr = channelApplicationList.stream().map(i -> String.valueOf(i.getId())).collect(Collectors.joining(","));
            String applicationNameStr = channelApplicationList.stream().map(ChannelApplication::getApplicationName).collect(Collectors.joining(","));
            channelProduct.setApplicationIds(applicationIdStr);
            channelProduct.setApplicationNames(applicationNameStr);
        } else if (StringUtils.isNotBlank(channelProduct.getApplicationIds())) {
            Set<String> applicationIdList = new HashSet<String>(Arrays.asList(channelProduct.getApplicationIds().split(",")));
            List<ChannelApplication> channelApplicationList = channelApplicationMapper.selectList(new LambdaQueryWrapper<ChannelApplication>().in(ChannelApplication::getId, applicationIdList));

            if (channelApplicationList.size() < applicationIdList.size()) {
                Set<String> applicationIdDBList = channelApplicationList.stream().map(i -> String.valueOf(i.getId())).collect(Collectors.toSet());
                applicationIdList.removeAll(applicationIdDBList);
                throw new BusinessException("??????ID????????????" + applicationIdList.stream().collect(Collectors.joining(",")));
            }

            String applicationIdStr = channelApplicationList.stream().map(i -> String.valueOf(i.getId())).collect(Collectors.joining(","));
            String applicationNameStr = channelApplicationList.stream().map(ChannelApplication::getApplicationName).collect(Collectors.joining(","));
            channelProduct.setApplicationIds(applicationIdStr);
            channelProduct.setApplicationNames(applicationNameStr);
        }
        // ????????????
        if (StringUtils.isNotBlank(channelProduct.getSaleDepartmentCode()) && StringUtils.isBlank(channelProduct.getSaleDepartmentName())) {
            List<String> saleDepartmentCodeList = new ArrayList<String>(Arrays.asList(channelProduct.getSaleDepartmentCode().split(",")));

            List<DepartmentEntity> departmentEntityList = sysClient.listDepartmentByOrgId(new ArrayList<Integer>());
            List<DepartmentEntity> departmentEntityListTmp3 = departmentEntityList.stream().filter(t -> saleDepartmentCodeList.contains(t.getCode())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(departmentEntityListTmp3)) {
                if (departmentEntityListTmp3.size() < saleDepartmentCodeList.size()) {
                    throw new BusinessException("???????????????????????????");
                }
                String saleDpartmentNameStr = departmentEntityListTmp3.stream().map(DepartmentEntity::getName).collect(Collectors.joining(","));
                channelProduct.setSaleDepartmentName(saleDpartmentNameStr);
            } else {
                throw new BusinessException("?????????????????????");
            }
        }
        //???????????????
        List<UserVO> userVOList = exportDataService.getUser();
        userVOList = userVOList.stream().filter(t -> String.valueOf(channelProduct.getUserid()).equals(t.getCardNumber())).collect(Collectors.toList());
        if (userVOList.size() != 1) {
            throw new BusinessException("??????????????????");
        } else {
            channelProduct.setUserid(Long.valueOf(userVOList.get(0).getId()));
            channelProduct.setUsername(userVOList.get(0).getCnname());
        }
    }

    public void emptyParam(ChannelProduct channelProduct) {
        List<String> errorMsg = new ArrayList<String>();
        if (StringUtils.isBlank(channelProduct.getDepartmentCode()) && StringUtils.isBlank(channelProduct.getDepartmentName())) {
            errorMsg.add("??????CODE????????????????????????");
        }
        if (StringUtils.isBlank(channelProduct.getProductCode())) {
            errorMsg.add("??????CODE??????");
        } else if (!channelProduct.getProductCode().matches("^[A-Z0-9]+$")) {
            errorMsg.add("??????CODE??????????????????????????????");
        }
        if (StringUtils.isBlank(channelProduct.getProductName())) {
            errorMsg.add("??????????????????");
        }
        if (CollectionUtil.isNotEmpty(channelProduct.getApplicationList()) && StringUtils.isNotBlank(channelProduct.getApplicationIds())) {
            errorMsg.add("???????????????????????????ID????????????????????????");
        }
        List<String> level = new ArrayList<String>();
        if (StringUtils.isNotBlank(channelProduct.getFirstLevelBusiness())) {
            level.add(channelProduct.getFirstLevelBusiness());
        }
        if (StringUtils.isNotBlank(channelProduct.getSecondLevelBusiness())) {
            level.add(channelProduct.getSecondLevelBusiness());
        }
        if (StringUtils.isNotBlank(channelProduct.getThirdLevelBusiness())) {
            level.add(channelProduct.getThirdLevelBusiness());
        }
        if (CollectionUtil.isNotEmpty(level) && level.size() != 3) {
            errorMsg.add("??????????????????/??????/????????????????????????");
        }
        if (ObjectUtil.isNotEmpty(channelProduct.getBusinessDictId()) && (StringUtils.isNotBlank(channelProduct.getFirstLevelBusiness()) || StringUtils.isNotBlank(channelProduct.getSecondLevelBusiness()) || StringUtils.isNotBlank(channelProduct.getThirdLevelBusiness()))) {
            errorMsg.add("????????????ID ??? ???????????????????????? ??????????????????");
        }

        if ((StringUtils.isNotBlank(channelProduct.getProductClass()) && StringUtils.isBlank(channelProduct.getProductTheme())) || (StringUtils.isBlank(channelProduct.getProductClass()) && StringUtils.isNotBlank(channelProduct.getProductTheme()))) {
            throw new BusinessException("??????????????????????????????????????????");
        }
        if (StringUtils.isNotBlank(channelProduct.getProductFlag())) {
            Map<String, List<String>> map = this.getProductFlagList();

            if (StringUtils.isBlank(channelProduct.getProductClass()) || StringUtils.isBlank(channelProduct.getProductTheme())) {
                errorMsg.add("???????????????????????????????????????????????????????????????");
            } else {
                List<String> productFlagParam = new ArrayList<String>(Arrays.asList(channelProduct.getProductFlag().split(",")));
                if (productFlagParam.size() > 1) {
                    errorMsg.add("?????????????????????");
                }
                if (productFlagParam.size() == 1) {
                    if ("H5".equals(productFlagParam.get(0))) {
                        if (StringUtils.isBlank(channelProduct.getProductScreen())) {
                            errorMsg.add("???????????????H5????????????????????????");
                        } else {
                            List<String> productScreenParam = new ArrayList<String>(Arrays.asList(channelProduct.getProductScreen().split(",")));
                            if (productScreenParam.size() > 1) {
                                errorMsg.add("?????????????????????");
                            }
                            List<String> productScreenList = map.get("productScreenList");
                            productScreenParam.removeAll(productScreenList);
                            if (productScreenParam.size() > 0) {
                                errorMsg.add("?????????????????????:" + StringUtils.join(productScreenParam, ","));
                            }
                        }
                    } else {
                        if (StringUtils.isNotBlank(channelProduct.getProductScreen())) {
                            errorMsg.add("??????????????????H5???????????????????????????");
                        }
                    }
                }
                List<String> productFlagList = map.get("productFlagList");
                productFlagParam.removeAll(productFlagList);
                if (productFlagParam.size() > 0) {
                    errorMsg.add("?????????????????????:" + StringUtils.join(productFlagParam, ","));
                }
            }
        }
        if (StringUtils.isNotBlank(channelProduct.getProductClass()) && StringUtils.isNotBlank(channelProduct.getProductTheme())) {
            List<ChannelProductLabel> channelProductLabelList = channelProductLabelService.list(new LambdaQueryWrapper<ChannelProductLabel>());

            if (StringUtils.isNotBlank(channelProduct.getProductClass())) {
                Set<String> productClassParam = new HashSet<String>(Arrays.asList(channelProduct.getProductClass().split(",")));
                List<ChannelProductLabel> channelProductLabelListHave = channelProductLabelList.stream().filter(i -> "1".equals(i.getLabelLevel()) && productClassParam.contains(String.valueOf(i.getId()))).collect(Collectors.toList());
                Set<String> labelAreaHave = channelProductLabelListHave.stream().map(ChannelProductLabel::getLabelArea).collect(Collectors.toSet());

                if (productClassParam.size() > channelProductLabelListHave.size()) {
                    errorMsg.add("????????????????????????" + StringUtils.join(productClassParam, ","));
                } else if (labelAreaHave.size() > 1) {
                    errorMsg.add("???????????????????????????????????????????????????" + StringUtils.join(labelAreaHave, ","));
                } else {
                    channelProduct.setProductClass(StringUtils.join(productClassParam, ","));
                }
            }
            if (StringUtils.isNotBlank(channelProduct.getProductTheme())) {
                Set<String> productThemeParam = new HashSet<String>(Arrays.asList(channelProduct.getProductTheme().split(",")));
                List<ChannelProductLabel> channelProductLabelListHave = channelProductLabelList.stream().filter(i -> "2".equals(i.getLabelLevel()) && productThemeParam.contains(String.valueOf(i.getId()))).collect(Collectors.toList());
                Set<String> labelAreaHave = channelProductLabelListHave.stream().map(ChannelProductLabel::getLabelArea).collect(Collectors.toSet());

                if (productThemeParam.size() > channelProductLabelListHave.size()) {
                    errorMsg.add("?????????????????????:" + StringUtils.join(productThemeParam, ","));
                } else if (labelAreaHave.size() > 1) {
                    errorMsg.add("???????????????????????????????????????????????????" + StringUtils.join(labelAreaHave, ","));
                } else {
                    channelProduct.setProductTheme(StringUtils.join(productThemeParam, ","));
                }
            }
        }

        if (ObjectUtil.isNull(channelProduct.getUserid())) {
            errorMsg.add("?????????????????????");
        }
        if (errorMsg.size() > 0) {
            throw new BusinessException(String.join(",", errorMsg));
        }

    }

    @Override
    public List<DepartmentVO> listDepartment(String cooperationMainName, HttpServletRequest request) {
        // ?????? By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);

        return channelProductMapper.selectList(new QueryWrapper<ChannelProduct>().select("department_code", "department_name").lambda()
                .eq(StrUtil.isNotEmpty(cooperationMainName), ChannelProduct::getCooperationMainName, cooperationMainName)
                .in(departmentCodeAllList.size() > 0, ChannelProduct::getDepartmentCode, departmentCodeAllList)
        ).stream().map(p -> new DepartmentVO(p.getDepartmentCode(), p.getDepartmentName())).collect(Collectors.toList());
    }

    @Override
    public ChannelProduct getById(String id) {
        ChannelProduct channelProduct = channelProductMapper.selectOne(new QueryWrapper<ChannelProduct>().lambda().eq(ChannelProduct::getId, id));

        // ???????????????
        setFlagAndApp(channelProduct);
        // ????????????ID??????
        List<ChannelProductCost> channelProductCostList = channelProductCostMapper.selectList(new LambdaQueryWrapper<ChannelProductCost>().eq(ChannelProductCost::getProductCode, channelProduct.getProductCode()));
        if (CollectionUtil.isNotEmpty(channelProductCostList)) {
            List<String> idList = channelProductCostList.stream().map(i -> String.valueOf(i.getId())).collect(Collectors.toList());
            channelProduct.setChannelProductCostIdList(idList);
        }
        // ??????PID??????
        Integer pidNum = channelPromotionMapper.selectCount(new LambdaQueryWrapper<ChannelPromotion>().eq(ChannelPromotion::getProductCode, channelProduct.getProductCode()));
        channelProduct.setPidNum(Long.valueOf(pidNum));

        return channelProduct;
    }

    /**
     * ???????????????
     *
     * @param channelProduct
     */
    public void setFlagAndApp(ChannelProduct channelProduct) {
        if (StringUtils.isNotBlank(channelProduct.getProductClass())) {
            List<ChannelProductLabel> channelProductLabelList = channelProductLabelService.list(new LambdaQueryWrapper<ChannelProductLabel>().in(ChannelProductLabel::getId, channelProduct.getProductClass().split(",")));
            channelProduct.setProductClassStr(channelProductLabelList.stream().map(ChannelProductLabel::getLabelValue).collect(Collectors.joining(",")));
        }
        if (StringUtils.isNotBlank(channelProduct.getProductTheme())) {
            List<ChannelProductLabel> channelProductLabelList = channelProductLabelService.list(new LambdaQueryWrapper<ChannelProductLabel>().in(ChannelProductLabel::getId, channelProduct.getProductTheme().split(",")));
            channelProduct.setProductThemeStr(channelProductLabelList.stream().map(ChannelProductLabel::getLabelValue).collect(Collectors.joining(",")));
        }
        // ????????????
        String applicationIds = channelProduct.getApplicationIds();
        if (StringUtils.isNotBlank(applicationIds)) {
            List<String> applicationIdList = new ArrayList<String>(Arrays.asList(applicationIds.split(",")));
            List<ChannelApplication> channelApplicationList = channelApplicationMapper.selectList(new QueryWrapper<ChannelApplication>().lambda().in(ChannelApplication::getId, applicationIdList));
            if (CollectionUtil.isNotEmpty(channelApplicationList)) {
                channelProduct.setApplicationList(channelApplicationList);
            }
        }
    }

    @Override
    public List<ChannelCooperation> getCCIDListExt(ChannelProductPageParam channelProductPageParam, HttpServletRequest request) {
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        channelProductPageParam.setDepartmentCodeAllList(departmentCodeAllList);
        Map<Integer, List<UserDmEntity>> mapAll = businessDictService.getCCIDAndPidByPermissions(user);
        channelProductPageParam.setMapAll(mapAll);

        List<ChannelCooperation> channelCooperationList = channelPromotionMapper.getCCIDListByProduct(channelProductPageParam, user);

        return channelCooperationList;
    }

    @Override
    public List<ChannelProduct> selectListProduct(ChannelProductPageParam channelProductPageParam) {
        return channelProductMapper.selectListProduct(channelProductPageParam);
    }

    @Override
    public ChannelProduct getOneByParam(String departmentCode, String productCode) {
        return channelProductMapper.getOneByParam(departmentCode, productCode);
    }

    @Override
    public List<ChannelProduct> getOneByParamList(String departmentCode, List<String> productCode) {
        List<ChannelProduct> temp = channelProductMapper.getOneByParamList(departmentCode, productCode);
        return temp;
    }

    @Override
    public Map<String, List<String>> getProductFlagList() {
        //????????????
        List<String> productFlagList = new ArrayList<String>();
        productFlagList.add("??????");
        productFlagList.add("??????");
        productFlagList.add("??????");
        productFlagList.add("H5");
        productFlagList.add("??????");
        //????????????
        List<String> productScreenList = new ArrayList<String>();
        productScreenList.add("??????");
        productScreenList.add("??????");
        /*//????????????
        List<String> productClassList = new ArrayList<String>();
        productClassList.add("????????????");
        productClassList.add("????????????");
        productClassList.add("????????????");
        productClassList.add("????????????");
        productClassList.add("????????????");
        productClassList.add("????????????");
        productClassList.add("????????????");
        productClassList.add("?????????");
        productClassList.add("??????");
        //????????????
        List<String> productThemeList = new ArrayList<String>();
        productThemeList.add("?????????");
        productThemeList.add("MMO");
        productThemeList.add("MOBA");
        productThemeList.add("FPS");
        productThemeList.add("??????");
        productThemeList.add("??????");
        productThemeList.add("??????");
        productThemeList.add("SLG");
        productThemeList.add("??????");
        productThemeList.add("??????");
        productThemeList.add("??????");
        productThemeList.add("??????");
        productThemeList.add("??????");
        productThemeList.add("??????");
        productThemeList.add("??????");
        productThemeList.add("??????");
        productThemeList.add("??????");
        productThemeList.add("ARPG");
        productThemeList.add("??????");
        productThemeList.add("??????");
        productThemeList.add("??????");
        productThemeList.add("?????????");
        productThemeList.add("??????");
        productThemeList.add("????????????");*/

        Map<String, List<String>> all = new HashMap<String, List<String>>();
        all.put("productFlagList", productFlagList);
        all.put("productScreenList", productScreenList);
        /*all.put("productClassList", productClassList);
        all.put("productThemeList", productThemeList);*/

        return all;
    }

    @Override
    public List<ChannelProduct> selectListByAppId(String valueOf) {
        return channelProductMapper.selectListByAppId(valueOf);
    }

    @Override
    public List<ChannelApplication> getChannelProductAppList(ChannelProductPageParam channelProductPageParam, HttpServletRequest request) {
        List<ChannelProduct> ChannelProductList = channelProductMapper.selectList(new QueryWrapper<ChannelProduct>().lambda()
                .eq(ChannelProduct::getDepartmentCode, channelProductPageParam.getDepartmentCode())
        );

        String appIds = ChannelProductList.stream().filter(i -> StringUtils.isNotBlank(i.getApplicationIds())).map(ChannelProduct::getApplicationIds).collect(Collectors.joining(","));
        List<String> appIdList = new ArrayList<String>(Arrays.asList(appIds));

        List<ChannelApplication> channelApplicationList = channelApplicationService.list(new LambdaQueryWrapper<ChannelApplication>().in(ChannelApplication::getId, appIdList));
        return channelApplicationList;
    }

    @Override
    public Map<String, Object> searchList(String departmentCode, String cooperationMainId, HttpServletRequest request) {
        // ?????? By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);

        List<ChannelProduct> businessCheckList = channelProductMapper.selectList(new QueryWrapper<ChannelProduct>().select("distinct department_code, department_name, cooperation_main_id, cooperation_main_name")
                .lambda().eq(StrUtil.isNotBlank(departmentCode), ChannelProduct::getDepartmentCode, departmentCode)
                .eq(StrUtil.isNotBlank(cooperationMainId), ChannelProduct::getCooperationMainId, cooperationMainId)
                .in(departmentCodeAllList.size() > 0, ChannelProduct::getDepartmentCode, departmentCodeAllList)
        );

        Set<Map<String, String>> departmentList = new HashSet<Map<String, String>>();
        Set<Map<String, String>> cooperationMainList = new HashSet<Map<String, String>>();
        for (ChannelProduct channelProduct : businessCheckList) {
            //??????
            if (StringUtils.isNotBlank(channelProduct.getDepartmentCode())) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("code", channelProduct.getDepartmentCode());
                map.put("name", channelProduct.getDepartmentName());
                departmentList.add(map);
            }
            //????????????
            if (StringUtils.isNotBlank(channelProduct.getCooperationMainId())) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("cooperationMainId", channelProduct.getCooperationMainId());
                map.put("cooperationMainName", channelProduct.getCooperationMainName());
                cooperationMainList.add(map);
            }
        }

        departmentList = departmentList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(i -> i.get("code")))), HashSet::new));
        cooperationMainList = cooperationMainList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(i -> i.get("cooperationMainId")))), HashSet::new));


        Map<String, Object> mapAll = new HashMap<String, Object>();
        mapAll.put("department", departmentList);
        mapAll.put("cooperationMain", cooperationMainList);

        return mapAll;
    }


}
