package com.stnts.bi.sys.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.*;
import com.stnts.bi.mapper.sys.*;
import com.stnts.bi.sys.common.Constants;
import com.stnts.bi.sys.common.SysConfig;
import com.stnts.bi.sys.service.OlapService;
import com.stnts.bi.sys.utils.OlapPermIdUtil;
import com.stnts.bi.sys.vos.olap.OlapPermPostVO;
import com.stnts.bi.sys.vos.olap.OlapPermVO;
import com.stnts.bi.vo.SimplePermVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/1/7
 */
@Service
@Slf4j
public class OlapServiceImpl implements OlapService {

    @Autowired
    private SysConfig sysConfig;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private PermMapper permMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OlapPermMapper olapPermMapper;

    @Autowired
    private OlapUserPermMapper olapUserPermMapper;

    @Autowired
    private UserProductMapper userProductMapper;

    @Override
    public ResultEntity<List<OlapPermVO>> getPermList(Integer userId, String sign) {

        if(null == userId || StringUtils.isBlank(sign)){
            return ResultEntity.param();
        }
        // 2239 123456  fbd4cf56632e8eaaab4f3d77b5dc55aa
        String content = String.valueOf(userId).concat(sysConfig.getOlapKey());
        String _sign = SecureUtil.md5(content);
        if(!StringUtils.equals(_sign, sign)){
            return ResultEntity.sign();
        }
        /**
         * 这里需要查榕树的权限  确定有权限的产品线
         * 再查olap的权限表 拿到权限list
         */
        List<OlapPermVO> result = Collections.emptyList();
        try {
            //查出用户角色列表
            List<UserRoleEntity> roleListByUserId = userRoleMapper.getRoleListByUserId(userId);
            if(CollectionUtil.isNotEmpty(roleListByUserId)){
                List<Integer> roleIds = roleListByUserId.stream().map(UserRoleEntity::getRoleId).collect(Collectors.toList());
                //查出用户角色权限
                List<SimplePermVO> permCkList = permMapper.selectListSimplePermByRoleIds(roleIds);
                if(null != permCkList && !permCkList.isEmpty()) {
                    //查出权限列表  ..
                    List<PermEntity> permList = permMapper.selectList(null);
                    //找出根节点列表...根节点权限即为BI一级菜单权限 包括易乐游 SDK看板 主仪表盘
                    Set<Integer> rootPerm = new HashSet<>();
                    findRootPerm(permList, permCkList, rootPerm);
                    //TODO 这里替换为新的产品线list就行了吧
                    List<String> productIdList = userProductMapper.listProductByUser(userId).stream().map(ProductEntity::getProductId).collect(Collectors.toList());
//                    List<Integer> productIdList = roleListByUserId.stream().map(UserRoleEntity::getProductIds).map(p -> StringUtils.split(p, ",")).flatMap(Arrays::stream).map(Integer::parseInt).collect(Collectors.toList());
                    //是否拥有产品线权限
                    List<String> productPerm = new ArrayList<>();
                    productPerm.add("-1");
                    if(CollectionUtil.isNotEmpty(productIdList)){
                        //这里可以优化，如果是全部产品线，则不需要产品线条件
                        //如果olap权限表设计为permId为BI中菜单权限和产品线ID 可以合并查询
                        if(productIdList.contains(Constants.KEY_PERM_ALL_PRODUCT)){
                            List<String> collect = productMapper.selectList(null).stream().map(ProductEntity::getProductId).collect(Collectors.toList());
                            productPerm.addAll(collect);
                        }else{
                            productPerm.addAll(productIdList);
                        }
                    }
                    // rootPerm 对应 bi_perm_id , productPerm 对应 product_id
                    List<OlapPermEntity> olapPermEntities = olapPermMapper.listByBI(rootPerm, productPerm);
//                    List<OlapPermEntity> leafList = findLeafList(olapPermEntities);
                    QueryWrapper<OlapPermEntity> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("status", 1);
                    List<OlapPermEntity> olapPermAll = olapPermMapper.selectList(queryWrapper);
                    //有对应的olap打通权限数据
                    if(CollectionUtil.isNotEmpty(olapPermEntities)){
                        Map<OlapPermEntity, List<OlapPermEntity>> permMap = olapPermEntities.stream().collect(Collectors.toMap(p -> p, p -> {
                            List<OlapPermEntity> olapPermList = olapPermAll.stream().filter(perm -> StringUtils.equals(perm.getParentPermId(),p.getPermId())).collect(Collectors.toList());
                            return loadLeafList(olapPermAll, olapPermList);
                        }));
                        if(CollectionUtil.isNotEmpty(permMap)){
                            //to天元要的格式
                            result = permMap.keySet().stream().map(p -> {
                                OlapPermVO olapPermVO = toVO(p);
                                List<OlapPermVO> vos = permMap.get(p).stream().map(this::toVO).collect(Collectors.toList());
                                olapPermVO.setChildren(vos);
                                return olapPermVO;
                            }).collect(Collectors.toList());
                        }
                    }
                }
            }
            return ResultEntity.success(result);
        }catch(Exception e){
            log.warn("[BI-OLAP]获取可发布目录和已发布仪表盘出错, 异常信息: {}", e.getMessage());
            return ResultEntity.exception(e.getMessage());
        }
    }

    public OlapPermVO toVO(OlapPermEntity permEntity){
        OlapPermVO olapPermVO = new OlapPermVO();
        olapPermVO.setPermId(permEntity.getPermId());
        olapPermVO.setPermName(permEntity.getPermName());
        olapPermVO.setPermType(permEntity.getPermType());
        olapPermVO.setStatus(permEntity.getStatus());
        olapPermVO.setOlapPermId(permEntity.getOlapPermId());
        return olapPermVO;
    }

    /**
     * @param olapPermAll
     * @param olapPermList
     * @return
     */
    private List<OlapPermEntity> loadLeafList(List<OlapPermEntity> olapPermAll, List<OlapPermEntity> olapPermList) {

        if(CollectionUtil.isEmpty(olapPermList)){
            return Collections.emptyList();
        }
        List<String> permIds = olapPermList.stream().map(OlapPermEntity::getPermId).collect(Collectors.toList());
        List<OlapPermEntity> subOlapPermList = olapPermAll.stream().filter(p -> permIds.contains(p.getParentPermId())).collect(Collectors.toList());
        if(CollectionUtil.isEmpty(subOlapPermList)){
            return olapPermList;
        }else{
            return loadLeafList(olapPermAll, subOlapPermList);
        }
    }

    /**
     * 查找叶子节点
     * 初始参数为 根节点
     * 这里假设菜单层级一致  目前是一致
     * @param olapPermEntities
     * @return
     */
    private List<OlapPermEntity> findLeafList(List<OlapPermEntity> olapPermEntities) {

        if(CollectionUtil.isEmpty(olapPermEntities)){
            return Collections.emptyList();
        }
        List<String> olapPermIdList = olapPermEntities.stream().map(OlapPermEntity::getPermId).collect(Collectors.toList());
        List<OlapPermEntity> subList = olapPermMapper.list(olapPermIdList, 1);
        if(CollectionUtil.isEmpty(subList)){
            return olapPermEntities;
        }
        return findLeafList(subList);
    }

    private void findRootPerm(List<PermEntity> permList, List<SimplePermVO> permCkList, Set<Integer> rootPermSet) {

        List<Integer> ckPermIds = permCkList.stream().map(SimplePermVO::getId).map(Integer::parseInt).collect(Collectors.toList());
        //这里perms为当前有权限节点的父节点
        Set<Integer> ckParentPermIds = permList.stream().filter(p -> ckPermIds.contains(p.getPermId())).map(PermEntity::getParentPermId).collect(Collectors.toSet());
        if(ckParentPermIds.size() == 1 && ckParentPermIds.contains(-1)){
            rootPermSet.addAll(ckPermIds);
            return ;
        }else{
            List<SimplePermVO> midPermCkList = permList.stream().filter(p -> ckParentPermIds.contains(p.getPermId())).map(p -> {
                SimplePermVO simplePermVO = new SimplePermVO();
                simplePermVO.setId(p.getPermId().toString());
                return simplePermVO;
            }).collect(Collectors.toList());
            List<Integer> collect = permList.stream().filter(p -> ckPermIds.contains(p.getPermId())).filter(p -> p.getParentPermId().intValue() == -1).map(PermEntity::getPermId).collect(Collectors.toList());
            rootPermSet.addAll(collect);
            findRootPerm(permList, midPermCkList, rootPermSet);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultEntity<String> publish(OlapPermPostVO olapPermPostVO) {

        /**
         * 主要是更新status，插入新增的
         * 这里层级是固定的，可以静态处理
         */
        //log
        try{
            String rootId = olapPermPostVO.getRootId();
            Integer userId = olapPermPostVO.getUserId();
            List<OlapPermVO> perms = olapPermPostVO.getPerms();
            List<OlapPermEntity> entityList = new ArrayList<>();
            String defaultPermId = OlapPermIdUtil.initId(rootId);
            OlapPermEntity defaultDir = defaultDir(defaultPermId, rootId);
            entityList.add(defaultDir);
            perms.stream().forEach(vo -> {
                int permType = vo.getPermType();
                String parentPermId = permType == 1 ? rootId : defaultPermId;
                OlapPermEntity entity = toEntity(vo, parentPermId, rootId);
                entityList.add(entity);
                //处理页面层
                String subParentPermId = entity.getPermId();
                List<OlapPermVO> children = vo.getChildren();
                if(CollectionUtil.isNotEmpty(children)){
                    children.stream().forEach(subVO -> {
                        OlapPermEntity subEntity = toEntity(subVO, subParentPermId, rootId);
                        entityList.add(subEntity);
                    });
                }
            });
            //这里要处理 同名的情况  编号++
//            entityList.stream().collect(Collectors.groupingBy(item -> toName(item.getParentPermId(), item.getPermType(), item.getPermName()), Collectors.toList()))
//            .forEach((nameKey, itemList) -> {
//                int size = itemList.size();
//                if(size > 1){
//                    IntStream.range(1, size).forEach(i -> {
//                        OlapPermEntity item = itemList.get(i);
//                        item.setPermNickname(item.getPermName().concat(String.valueOf(i)));
//                    });
//                }
//            });

            olapPermMapper.publish(entityList);
            //发布完成后 将发布的权限赋予发布人
            List<String> permIds = entityList.stream().filter(perm -> perm.getPermType() == 2 && perm.getStatus() == 1).map(OlapPermEntity::getPermId).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(permIds)){
                List<OlapUserPermEntity> userPermList = permIds.stream().map(permId -> OlapUserPermEntity.builder().userId(userId).permId(permId).build()).collect(Collectors.toList());
                olapUserPermMapper.insertBatchIgnore(userPermList);
            }
            return ResultEntity.success(null);
        }catch(Exception e){
            log.warn("[BI-OLAP]发布出错, 异常信息: {}", e.getMessage());
            return ResultEntity.exception(e.getMessage());
        }
    }

    /**
     * @param parentId  父类ID
     * @param permType
     * @param permName
     * @return
     */
    private String toName(String parentId, Integer permType, String permName){
        return String.format("%s_%s_%s", parentId, permType, permName);
    }

    public OlapPermEntity defaultDir(String defaultPermId, String permId){
        OlapPermEntity entity = new OlapPermEntity();
        entity.setPermId(defaultPermId);
        entity.setPermType(1);
        entity.setPermName(sysConfig.getDefaultName());
        entity.setOrderNum(9999);
        entity.setParentPermId(permId);
        entity.setStatus(1);
        return entity;
    }

    public OlapPermEntity toEntity(OlapPermVO vo, String parentId, String rootId){
        OlapPermEntity entity = new OlapPermEntity();
        entity.setOlapPermId(vo.getOlapPermId());
        entity.setPermName(vo.getPermName());
        entity.setStatus(Optional.ofNullable(vo.getStatus()).orElse(1));
        int permType = vo.getPermType();
        entity.setParentPermId(parentId);
        entity.setPermType(permType);
        entity.setPermId(OlapPermIdUtil.initId(rootId, permType, vo.getOlapPermId()));
        return entity;
    }
}
