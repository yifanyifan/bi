package com.stnts.bi.sys.service.impl;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.entity.sys.SdkDataLevelEntity;
import com.stnts.bi.entity.sys.UserRoleEntity;
import com.stnts.bi.exception.BiException;
import com.stnts.bi.mapper.sys.SdkDataLevelMapper;
import com.stnts.bi.mapper.sys.UserProductMapper;
import com.stnts.bi.mapper.sys.UserRoleMapper;
import com.stnts.bi.sys.common.Constants;
import com.stnts.bi.sys.utils.SysUtil;
import com.stnts.bi.sys.vos.ProductBindVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.util.Md5Utils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.ProductEntity;
import com.stnts.bi.mapper.sys.ProductMapper;
import com.stnts.bi.sys.common.SysConfig;
import com.stnts.bi.sys.service.ProductService;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.transform.Result;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private SysConfig sysConfig;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private SdkDataLevelMapper sdkDataLevelMapper;

    @Autowired
    private UserProductMapper userProductMapper;

    @Override
    public ResultEntity<String> syncProduct() {

        try {

            long ts = Instant.now().getEpochSecond();
            String md5Content = String.format("%s%s", ts, sysConfig.getProductApiKey());
            String sign = Md5Utils.getMD5(md5Content.getBytes()).toLowerCase();
            String api = String.format("%s?time=%s&sign=%s", sysConfig.getProductApiHost(), ts, sign);
            String products = HttpUtil.get(api);
            JSONObject jsonObj = JSON.parseObject(products);
            String status = jsonObj.getString("status");
            if (status.equals(Constants.DSSP_SUCCESS)) {

                JSONArray jsonArr = jsonObj.getJSONObject("data").getJSONArray("datas");
                List<ProductEntity> productEntities = new ArrayList<>(jsonArr.size());
                for (int i = 0; i < jsonArr.size(); i++) {
                    JSONObject product = jsonArr.getJSONObject(i);
                    ProductEntity pe = new ProductEntity();
                    pe.setProductId(product.getString("id"));
                    pe.setProductName(product.getString("name"));
                    pe.setBusiness(product.getString("business"));
                    pe.setClassification(product.getString("classification"));
                    pe.setSdkproduct(product.getIntValue("sdkproduct"));
                    pe.setSdkproductDisplay(product.getString("sdkproduct_display"));
                    int isActive = product.getBooleanValue("is_active") ? 1 : 0;
                    pe.setStatus(isActive);
                    productEntities.add(pe);
                }
                productMapper.updateProducts(productEntities);
                /**
                 * 这里追加一个逻辑：被梧桐树删掉的 状态改为禁用
                 */
                List<String> productIds = productEntities.stream().map(ProductEntity::getProductId).collect(Collectors.toList());
                productMapper.updateStatus(productIds);
                return ResultEntity.success(null);
            } else {
                return ResultEntity.failure(jsonObj.getString("info"));
            }
        } catch (Exception e) {
            log.warn("syncProduct failed, msg: {}", e.getMessage());
            return ResultEntity.exception(e.getMessage());
        }
    }

    @Override
    public ResultEntity<Page<ProductEntity>> list(Integer pageNo, String name) {

        List<ProductEntity> products;
        try {
            Page<ProductEntity> page = SysUtil.toPage(pageNo, sysConfig.getPageSize());
            products = productMapper.listProducts(page, name);
            products = products.stream().map(product -> {
               Integer levelId = product.getLevelId();
               if(null != levelId){
                   SdkDataLevelEntity sdkDataLevelEntity = sdkDataLevelMapper.selectById(levelId);
                   String path = sdkDataLevelEntity.getPath();
                   if(StrUtil.isNotEmpty(path)){
                       List<String> ids = Stream.of(path.split("\\.")).collect(Collectors.toList());
                       List<String> levelNames = sdkDataLevelMapper.selectList(new LambdaQueryWrapper<SdkDataLevelEntity>().select(SdkDataLevelEntity::getLevelName).in(CollectionUtil.isNotEmpty(ids), SdkDataLevelEntity::getLevelId, ids).orderByAsc(SdkDataLevelEntity::getPath, SdkDataLevelEntity::getType, SdkDataLevelEntity::getIdx).orderByDesc(SdkDataLevelEntity::getUpdatedAt)).stream().map(SdkDataLevelEntity::getLevelName).collect(Collectors.toList());
                       product.setDataLevel(StringUtils.join(levelNames, "/").concat("/").concat(sdkDataLevelEntity.getLevelName()));
                   }else{
                       product.setDataLevel(sdkDataLevelEntity.getLevelName());
                   }
               }
               return product;
            }).collect(Collectors.toList());
            page.setRecords(products);
            return ResultEntity.success(page);
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("list failed, msg: {}", e.getMessage());
            return ResultEntity.exception(e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultEntity<String> delUsers(Integer productId, List<Integer> userIds) {

        ProductEntity productEntity = productMapper.selectById(productId);
        if (null == productEntity)
            return ResultEntity.failure("非法的产品线ID");
        userProductMapper.deleteBy(productId, userIds);
        return ResultEntity.success(null);
    }

//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public ResultEntity<String> delUsers(Integer productId, List<Integer> userIds) {
//
//        try {
//
//            ProductEntity productEntity = productMapper.selectById(productId);
//            if (null == productEntity)
//                return ResultEntity.failure("非法的产品线ID");
//            String productName = productEntity.getProductName();
//            QueryWrapper<UserRoleEntity> userRoleEntityQueryWrapper = new QueryWrapper<>();
//            userRoleEntityQueryWrapper.like("product_ids", productId);
//            if(CollectionUtil.isNotEmpty(userIds)){
//                userRoleEntityQueryWrapper.in("user_id", userIds);
//            }
//            List<UserRoleEntity> userRoleEntities = userRoleMapper.selectList(userRoleEntityQueryWrapper);
//            List<UserRoleEntity> userRoles = userRoleEntities.stream().map(userRoleEntity -> {
//
//                String productIds = userRoleEntity.getProductIds();
//                String productNames = userRoleEntity.getProductNames();
//                //如果有全部产品线，则其它角色不能选择产品线，保证这里不会出现问题
//                Set<String> ids = Arrays.asList(StringUtils.split(productIds, ",")).stream().collect(Collectors.toSet());
//                ids.remove(String.valueOf(productId));
//                userRoleEntity.setProductIds(StringUtils.join(ids, ","));
//
//                Set<String> names = Arrays.asList(StringUtils.split(productNames, ",")).stream().collect(Collectors.toSet());
//                names.remove(productName);
//                userRoleEntity.setProductNames(StringUtils.join(names, ","));
//
//                return userRoleEntity;
//            }).collect(Collectors.toList());
//
//            if (CollectionUtil.isEmpty(userRoles))
//                return ResultEntity.success("该产品线下没有用户");
//            userRoleMapper.updateBatch(userRoles);
//            return ResultEntity.success(null);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw e;
//        }
//    }

    @Override
    public ResultEntity<String> bindDataLevel(ProductEntity productEntity) {

        try{
            if(ObjectUtil.isNotNull(productEntity.getLevelId())){
                productMapper.updateById(productEntity);
            }else{
                //解绑
                productMapper.unBind(productEntity.getProductId());
            }
            return ResultEntity.success(null);
        }catch(Exception e){
            throw new BiException(e.getMessage());
        }
    }

    @Override
    public ResultEntity<List<ProductEntity>> all(String name) {
        List<ProductEntity> products;
        try {

            QueryWrapper<ProductEntity> query;
            query = new QueryWrapper<>();
            if (StringUtils.isNotBlank(name)) {
                query.like("product_name", name);
            }
            query.eq("status", 1);
            products = productMapper.selectList(query);
        } catch (Exception e) {
            return ResultEntity.exception(e.getMessage());
        }
        return ResultEntity.success(products);
    }
}
