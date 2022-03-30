package com.stnts.bi.sys.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.OlapPermEntity;
import com.stnts.bi.entity.sys.OlapUserPermEntity;
import com.stnts.bi.entity.sys.PermEntity;
import com.stnts.bi.entity.sys.ProductEntity;
import com.stnts.bi.mapper.sys.OlapPermMapper;
import com.stnts.bi.mapper.sys.OlapUserPermMapper;
import com.stnts.bi.mapper.sys.PermMapper;
import com.stnts.bi.mapper.sys.ProductMapper;
import com.stnts.bi.sys.common.SysConfig;
import com.stnts.bi.sys.service.OlapMenuService;
import com.stnts.bi.sys.utils.OlapUtil;
import com.stnts.bi.vo.OlapPermSubVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/1/13
 */
@Service
@Slf4j
public class OlapMenuServiceImpl implements OlapMenuService {

    @Autowired
    private SysConfig sysConfig;

    @Autowired
    private OlapPermMapper olapPermMapper;

    @Autowired
    private OlapUserPermMapper olapUserPermMapper;

    @Autowired
    private PermMapper permMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public ResultEntity<List<OlapPermEntity>> self(String permId, Integer userId) {

        try{
            List<OlapPermEntity> result = Collections.emptyList();
//            QueryWrapper<OlapUserPermEntity> userPermEntityQueryWrapper = new QueryWrapper<>();
//            userPermEntityQueryWrapper.eq("user_id", userId);
//            List<OlapUserPermEntity> olapUserPermEntities = olapUserPermMapper.selectList(userPermEntityQueryWrapper);
//            if(CollectionUtil.isNotEmpty(olapUserPermEntities)){
                //表示用户拥有权限，植入请求url 并组装权限树
//                List<String> permIds = olapUserPermEntities.stream().map(OlapUserPermEntity::getPermId).collect(Collectors.toList());
                //查出的是页面集合
//                List<OlapPermEntity> pageList = olapPermMapper.selectByPermList(permIds, 1);
                List<OlapPermEntity> pageList = olapPermMapper.listValid(userId);
                if(CollectionUtil.isNotEmpty(pageList)){
                    // 用户拥有的权限 且对应的 页面是 启用的
                    List<Integer> dashboardIds = pageList.stream().map(OlapPermEntity::getOlapPermId).collect(Collectors.toList());
                    Map<Integer, String> urlMap = OlapUtil.loadOlapUrl(dashboardIds, sysConfig.getOlapAppId(), sysConfig.getKeyFromOlap(), sysConfig.getOlapApiUrl());
                    //如果urlmap为空，有权限也无意义
                    if(CollectionUtil.isNotEmpty(urlMap)){

                        List<OlapPermEntity> validPageList = pageList.stream().filter(page -> urlMap.containsKey(page.getOlapPermId())).map(page -> {
                            page.setUrl(urlMap.get(page.getOlapPermId()));
                            return page;
                        }).collect(Collectors.toList());
                        List<String> menuPermIds = validPageList.stream().map(OlapPermEntity::getParentPermId).collect(Collectors.toList());
                        List<OlapPermEntity> menuList = olapPermMapper.selectByPermList(menuPermIds, null);
                        Map<String, List<OlapPermEntity>> validPageMap = validPageList.stream().collect(Collectors.groupingBy(OlapPermEntity::getParentPermId, Collectors.toList()));
                        result = menuList.stream().filter(menu -> StringUtils.equals(menu.getParentPermId(), permId)).map(menu -> {
                            menu.setChildren(validPageMap.get(menu.getPermId()));
                            return menu;
                        }).collect(Collectors.toList());
                    }
                }
//            }
            return ResultEntity.success(result);
        }catch(Exception e){
            log.warn("获取自己菜单出错, 异常信息: {}", e.getMessage());
            return ResultEntity.exception(e.getMessage());
        }
    }

    @Override
    public ResultEntity<List<OlapPermEntity>> all(String permId) {

        List<OlapPermEntity> result = new ArrayList<>();
        try{

            QueryWrapper<OlapPermEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("parent_perm_id", permId);
            queryWrapper.eq("status", 1);
            //找出所有的目录
            List<OlapPermEntity> menuAll = olapPermMapper.selectList(queryWrapper);
            if(CollectionUtil.isNotEmpty(menuAll)){
                //组装树结构
                List<String> pagePermList = menuAll.stream().map(OlapPermEntity::getPermId).collect(Collectors.toList());
                List<OlapPermEntity> pageList = olapPermMapper.list(pagePermList, 1);
                result = menuAll.stream().map(menu -> {
                    String menuId = menu.getPermId();
                    List<OlapPermEntity> children = pageList.stream().filter(m -> StringUtils.equals(m.getParentPermId(), menuId)).collect(Collectors.toList());
                    menu.setChildren(children);
                    return menu;
                }).filter(menu -> CollectionUtil.isNotEmpty(menu.getChildren())).sorted().collect(Collectors.toList());
            }
            return ResultEntity.success(result);
        }catch(Exception e){
            log.warn("获取菜单出错, 异常信息: {}", e.getMessage());
            return ResultEntity.exception(e.getMessage());
        }
    }

    @Override
    public ResultEntity<String> mod(List<OlapPermSubVO> perms) {

        try{
            //让菜单有序
            IntStream.range(0, perms.size()).forEach(orderIndex -> {
                OlapPermSubVO olapPermSubVO = perms.get(orderIndex);
                olapPermSubVO.setOrderNum(orderIndex+1);
                String permId = olapPermSubVO.getPermId();
                //这里前端没传是否是文件夹  通过permid判断  文件夹状态始终为1
                if(StringUtils.contains(permId, "_M_")){
                    olapPermSubVO.setStatus(1);
                }
            });
            //批量更新
            olapPermMapper.updateBatch(perms);
            return ResultEntity.success(null);
        }catch(Exception e){
            log.warn("修改菜单出错, 异常信息: {}", e.getMessage());
            return ResultEntity.exception(e.getMessage());
        }
    }

    @Override
    public boolean initMenu() {

        try{
            List<Integer> biOpenMenu = sysConfig.getBiOpenMenu();
            QueryWrapper<PermEntity> permEntityQueryWrapper = new QueryWrapper<>();
            permEntityQueryWrapper.eq("parent_perm_id", -1);
            List<PermEntity> permEntityList = permMapper.selectList(permEntityQueryWrapper);
            List<PermEntity> menus = permEntityList.stream().filter(perm -> biOpenMenu.contains(perm.getPermId())).collect(Collectors.toList());
            QueryWrapper<ProductEntity> productEntityQueryWrapper = new QueryWrapper<>();
            productEntityQueryWrapper.eq("status", 1);
            productEntityQueryWrapper.ne("product_id", "-9");
            List<ProductEntity> productEntityList = productMapper.selectList(productEntityQueryWrapper);
            List<OlapPermEntity> olapPermListByMenu = menus.stream().map(this::menu2Olap).collect(Collectors.toList());
            List<OlapPermEntity> olapPermListByProduct = productEntityList.stream().map(this::product2Olap).collect(Collectors.toList());
            List<OlapPermEntity> perms = new ArrayList<>();
            perms.addAll(olapPermListByMenu);
            perms.addAll(olapPermListByProduct);
            olapPermMapper.insertBatch(perms);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    private OlapPermEntity menu2Olap(PermEntity menu){
        OlapPermEntity olapPermEntity = new OlapPermEntity();
        olapPermEntity.setPermId(String.valueOf(menu.getPermId()));
        olapPermEntity.setPermName(menu.getPermName());
        olapPermEntity.setParentPermId("-1");
        olapPermEntity.setPermType(1);
        olapPermEntity.setOrderNum(menu.getPermId());
        olapPermEntity.setStatus(1);
        olapPermEntity.setBiPermId(menu.getPermId());
        olapPermEntity.setProductId(-1);
        return olapPermEntity;
    }

    private OlapPermEntity product2Olap(ProductEntity product){
        OlapPermEntity olapPermEntity = new OlapPermEntity();
        olapPermEntity.setPermId(product.getProductId());
        olapPermEntity.setPermName("SDK看板/".concat(product.getProductName()));
        olapPermEntity.setParentPermId("-1");
        olapPermEntity.setPermType(1);
        olapPermEntity.setOrderNum(9999);
        olapPermEntity.setStatus(1);
        //全部属于BI中SDK下的
        olapPermEntity.setBiPermId(2);
        olapPermEntity.setProductId(Integer.parseInt(product.getProductId()));
        return olapPermEntity;
    }
}
