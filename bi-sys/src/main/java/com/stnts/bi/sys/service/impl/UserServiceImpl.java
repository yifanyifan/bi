package com.stnts.bi.sys.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.*;
import com.stnts.bi.exception.BiException;
import com.stnts.bi.mapper.sys.*;
import com.stnts.bi.sys.common.Constants;
import com.stnts.bi.sys.common.SysConfig;
import com.stnts.bi.sys.feign.DataManagementClient;
import com.stnts.bi.sys.params.UserDmSubParam;
import com.stnts.bi.sys.params.UserOrgParam;
import com.stnts.bi.sys.params.UserRoleNewParam;
import com.stnts.bi.sys.params.UserRoleParam;
import com.stnts.bi.sys.service.UserService;
import com.stnts.bi.sys.utils.SysUtil;

import java.util.*;
import java.util.stream.Collectors;

import com.stnts.bi.sys.vos.TreeVO;
import com.stnts.bi.sys.vos.UserRoleVO;
import com.stnts.bi.vo.DmVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liang.zhang
 * @date 2020年3月29日
 * @desc TODO
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private SysConfig sysConfig;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private UserOrgMapper userOrgMapper;

    @Autowired
    private UserProductMapper userProductMapper;

    @Autowired
    private UserDmMapper userDmMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private SdkDataLevelMapper sdkDataLevelMapper;

    @Autowired
    private DataManagementClient dataManagementClient;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private UserProductFocusMapper userProductFocusMapper;

    @Autowired
    private OlapUserPermMapper olapUserPermMapper;

    @Override
    public ResultEntity<Page<UserEntity>> findUserListByUserId(Integer pageNo, Integer userId, List<Integer> roleIds, List<Integer> productIds) {

        List<UserEntity> users;
        try {
            Page<UserEntity> page = SysUtil.toPage(pageNo, this.sysConfig.getPageSize().intValue());
            page.setOrders(OrderItem.descs(new String[]{"is_admin", "oa_status", "created_at"}));
            users = this.userMapper.findUserListByName(page, userId, roleIds, productIds);
            page.setRecords(users);
            return ResultEntity.success(page);
        } catch (Exception e) {
            log.warn("findUserListByName failed, err: " + e.getMessage());
            return ResultEntity.exception(e.getMessage());
        }
    }

    @Override
    public ResultEntity<Page<UserEntity>> findUserListBySearch(Integer pageNo, Integer userId, List<Integer> roleIds, String departmentCode, Integer orgId, List<Integer> productIds) {
        List<UserEntity> users;
        try {
            Page<UserEntity> page = SysUtil.toPage(pageNo, this.sysConfig.getPageSize().intValue());
            page.setOrders(OrderItem.descs(new String[]{"is_admin", "oa_status", "created_at"}));
            users = this.userMapper.findUserListBySearch(page, userId, roleIds, departmentCode, orgId, productIds);
            page.setRecords(users);
            return ResultEntity.success(page);
        } catch (Exception e) {
            log.warn("findUserListBySearch failed, err: " + e.getMessage());
            return ResultEntity.exception(e.getMessage());
        }
    }

    @Override
    public ResultEntity<String> delRole(Integer userId) {

        int result = 0;
        try {
            result = this.userRoleMapper.deleteByUserId(userId);
        } catch (Exception e) {
            log.warn("delRole failed, err: " + e.getMessage());
            return ResultEntity.exception(e.getMessage());
        }
        return (result > 0) ? ResultEntity.success(null) : ResultEntity.failure(Constants.MSG_RETURN_USER_ROLE_DELFAIL);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultEntity<String> modRole(Integer userId, List<UserRoleParam> roles) {
        try {
            if (null == userId || roles.isEmpty()){
                return ResultEntity.param(Constants.MSG_PARAM_USER_ROLE_NOTNULL);
            }
            List<String> products = (List<String>) roles.stream().map(UserRoleParam::getProductIds)
                    .map(ids -> ids.split(",")).flatMap(Arrays::stream).filter(item -> StringUtils.isNotBlank(item)).collect(Collectors.toList());
            if (products.size() != 0 && products.stream().distinct().count() != products.size()){
                return ResultEntity.failure(Constants.MSG_PARAM_USER_ROLE_DUPPRODUCT);
            }
            if (products.size() > 1 && products.contains(Constants.KEY_PERM_ALL_PRODUCT)){
                return ResultEntity.failure(Constants.MSG_PARAM_PRODUCT_DUP);
            }
            this.userRoleMapper.deleteByUserId(userId);
            List<UserRoleEntity> roleEntitys = (List<UserRoleEntity>) roles.stream().map(
                    role -> new UserRoleEntity(userId, role.getRoleId(), role.getProductIds(), role.getProductNames()))
                    .collect(Collectors.toList());
            this.userRoleMapper.insertBatch(roleEntitys);
        } catch (Exception e) {
            log.warn("modRole failed, err: " + e.getMessage());
            throw e;
        }
        return ResultEntity.success(null);
    }

    @Override
    public ResultEntity<List<UserEntity>> findUsers(String cnname) {

        List<UserEntity> users;
        try {
            QueryWrapper<UserEntity> query = null;
            if (StringUtils.isNotBlank(cnname)) {
                query = new QueryWrapper<>();
                query.like("cnname", cnname);
            }
            users = this.userMapper.selectList(query);
            return ResultEntity.success(users);
        } catch (Exception e) {
            log.warn("findUsers failed, err: " + e.getMessage());
            return ResultEntity.exception(e.getMessage());
        }
    }

    @Override
    public ResultEntity<List<UserRoleEntity>> listRole(Integer userId) {

        List<UserRoleEntity> roles;
        try {
            roles = this.userRoleMapper.getRoleListByUserId(userId);
            return ResultEntity.success(roles);
        } catch (Exception e) {
            log.warn("listRole failed, err: " + e.getMessage());
            return ResultEntity.exception(e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultEntity<Boolean> delPerm(Integer userId) {

        try {
            UserEntity user = userMapper.findUserById(userId);
            if (user.getAdmin() == 1) {
                return ResultEntity.exception("超级管理员权限不允许删除");
            }
            this.userRoleMapper.deleteByUserId(userId);
            this.userOrgMapper.deleteByUserId(userId);
            this.userProductMapper.deleteByUserId(userId);
            this.userDmMapper.deleteByUserId(userId);
            return ResultEntity.success(true);
        } catch (Exception e) {
            log.warn("delRole failed, err: " + e.getMessage());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultEntity<Boolean> bindOrg(UserOrgParam userOrgParam) {

        try {
            if (!userOrgParam.valid()) {
                return ResultEntity.param("非法参数");
            }
            Integer userId = userOrgParam.getUserId();
            List<UserOrgEntity> list = userOrgParam.getOrgIds().stream().map(orgId -> {
                UserOrgEntity userOrgEntity = new UserOrgEntity();
                userOrgEntity.setUserId(userId);
                userOrgEntity.setOrgId(orgId);
                return userOrgEntity;
            }).collect(Collectors.toList());
            userOrgMapper.deleteByUserId(userId);
            if (CollectionUtil.isNotEmpty(list)) {
                userOrgMapper.insertBatch(list);
            }
            return ResultEntity.success(true);
        } catch (Exception e) {
            log.warn("bindOrg failed, err: " + e.getMessage());
            throw new BiException("绑定用户组织出错, 异常信息: " + e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultEntity<String> modRoleNew(UserRoleNewParam userRole) {

        try {
            if (!userRole.valid()) {
                return ResultEntity.param("参数不合法");
            }
            int userId = userRole.getUserId();
            //处理角色
            userRoleMapper.deleteByUserId(userId);
            List<Integer> roleIds = userRole.getRoleIds();
            if (CollectionUtil.isNotEmpty(roleIds)) {
                List<UserRoleEntity> userRoleList = roleIds.stream().map(roleId -> new UserRoleEntity(userId, roleId, null, null)).collect(Collectors.toList());
                userRoleMapper.insertBatch(userRoleList);
            }
            //处理产品线
            userProductMapper.deleteByUserId(userId);
            List<String> productIds = userRole.getProductIds();
            if (CollectionUtil.isNotEmpty(productIds)) {
                List<UserProductEntity> userProductList = productIds.stream().map(productId -> new UserProductEntity(userId, productId)).collect(Collectors.toList());
                userProductMapper.insertBatch(userProductList);
            }
            //处理数据管理
            userDmMapper.deleteByUserId(userId);
            List<UserDmSubParam> userDms = userRole.getDms();
            if (CollectionUtil.isNotEmpty(userDms)) {
                List<UserDmEntity> userDmList = userDms.stream().filter(ud -> ObjectUtil.isNotNull(ud.getDmType())).map(userDm -> new UserDmEntity(userId, userDm.getDmId(), userDm.getDmType(), userDm.getDmPid())).collect(Collectors.toList());
                userDmMapper.insertBatch(userDmList);
            }
            return ResultEntity.success(null);
        } catch (Exception e) {
            log.warn("编辑用户权限出错, 异常信息: {}", e.getMessage());
            throw new BiException("编辑用户权限出错, 异常信息: " + e.getMessage());
        }
    }

    @Override
    public ResultEntity<UserRoleVO> showRole(Integer userId) {

        try {

            UserRoleVO userRoleVO = new UserRoleVO();
            UserEntity user = userMapper.findUserById(userId);
            userRoleVO.setCnname(user.getCnname());
            userRoleVO.setDepartmentName(user.getDepartmentName());
            if (CollectionUtil.isNotEmpty(user.getOrgs())) {
                userRoleVO.setOrgNames(user.getOrgs().stream().map(OrgEntity::getOrgName).collect(Collectors.joining(Constants.SHOW_SPLIT)));
            }
            List<UserRoleEntity> userRoleEntityList = userRoleMapper.selectList(new LambdaQueryWrapper<UserRoleEntity>().eq(UserRoleEntity::getUserId, userId));
            List<Integer> roleIds = userRoleEntityList.stream().map(UserRoleEntity::getRoleId).collect(Collectors.toList());
            userRoleVO.setRoleIds(roleIds);


            //增加SDK属性
            List<TreeVO> products = fillProduct(userId);
            userRoleVO.setProducts(products);

            //数据管理属性
            List<TreeVO> dms = fillDm(user);
            userRoleVO.setDms(dms);

            return ResultEntity.success(userRoleVO);
        } catch (Exception e) {
            log.warn("编辑用户权限出错, 异常信息: {}", e.getMessage());
            throw new BiException("编辑用户权限出错, 异常信息: " + e.getMessage());
        }
    }

    @Override
    public ResultEntity<List<String>> checkedSdk(Integer userId) {

        try {
            List<ProductEntity> checkedProductList = productMapper.listProductByUser(userId);
            List<String> products = checkedProductList.stream().map(ProductEntity::getProductId).collect(Collectors.toList());
            return ResultEntity.success(products);
        } catch (Exception e) {
            log.warn("查看用户SDK已选出错, 异常信息: {}", e.getMessage());
            throw new BiException("查看用户SDK已选出错, 异常信息: " + e.getMessage());
        }
    }

    @Override
    public ResultEntity<List<String>> checkedDm(Integer userId) {
        try {

            List<UserDmEntity> dmEntityList = userDmMapper.selectList(new QueryWrapper<UserDmEntity>().lambda()
                    .eq(UserDmEntity::getUserId, userId));
            List<String> dmIds = dmEntityList.stream().map(dm -> {
                //3为用户
                if (dm.getDmType() == 3) {
                    return SysUtil.dmIndex(dm.getDmPid(), dm.getDmId());
                } else {
                    return dm.getDmId();
                }
            }).collect(Collectors.toList());
            return ResultEntity.success(dmIds);
        } catch (Exception e) {
            log.warn("查看用户数据管理已选出错, 异常信息: {}", e.getMessage());
            throw new BiException("查看用户数据管理已选出错, 异常信息: " + e.getMessage());
        }
    }

    @Override
    public ResultEntity<List<TreeVO>> sdk(Integer userId) {

        try {

            List<ProductEntity> listProductByUser = productMapper.listProductByUser(userId);

            //这里还必须考虑 OLAP 相关的 SDK权限
            List<String> biProIds = listProductByUser.stream().map(ProductEntity::getProductId).collect(Collectors.toList());
            List<OlapUserPermEntity> olapUserPermEntityList = olapUserPermMapper.selectList(new QueryWrapper<OlapUserPermEntity>().lambda().eq(OlapUserPermEntity::getUserId, userId));
            List<String> proIds = olapUserPermEntityList.stream().map(permId -> StrUtil.split(permId.getPermId(), "_")[0]).distinct().filter(proId -> !biProIds.contains(proId)).collect(Collectors.toList());
            if(!proIds.isEmpty()){
                List<ProductEntity> olapProductEntityList = productMapper.selectBatchIds(proIds);
                listProductByUser.addAll(olapProductEntityList);
            }

            if (CollectionUtil.isEmpty(listProductByUser)) {
                return ResultEntity.failure("该用户无SDK权限");
            }
            long count = listProductByUser.stream().filter(p -> p.getProductId().equals(Constants.KEY_PERM_ALL_PRODUCT)).count();
            if(count > 0){
                listProductByUser = productMapper.selectList(new QueryWrapper<ProductEntity>().ne("product_id", Constants.KEY_PERM_ALL_PRODUCT));
            }
//            if (listProductByUser.size() == 1 && listProductByUser.get(0).getProductId().equals(Constants.KEY_PERM_ALL_PRODUCT)) {
//            }
            List<Integer> levelLeafIdList = listProductByUser.stream().filter(p -> null != p.getLevelId()).map(ProductEntity::getLevelId).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(levelLeafIdList)) {
                return ResultEntity.failure("该用户无SDK权限");
            }
            List<SdkDataLevelEntity> levelLeafList = sdkDataLevelMapper.selectBatchIds(levelLeafIdList);
            List<Integer> pathIds = levelLeafList.stream().map(SdkDataLevelEntity::getPath).filter(StrUtil::isNotEmpty).map(path -> StrUtil.split(path, ".")).flatMap(Arrays::stream).filter(StrUtil::isNotEmpty).map(Integer::parseInt).distinct().collect(Collectors.toList());
            List<SdkDataLevelEntity> levelParentList = Collections.emptyList();
            if (CollectionUtil.isNotEmpty(pathIds)) {
                levelParentList = sdkDataLevelMapper.selectBatchIds(pathIds);
            }

            List<TreeVO> treePartOfProduct = listProductByUser.stream().map(p -> {
                TreeVO treeVO = new TreeVO();
                treeVO.setNodeId(p.getProductId());
                treeVO.setNodeName(p.getProductName());
                treeVO.setParentNodeId(String.valueOf(p.getLevelId()));
                return treeVO;
            }).collect(Collectors.toList());

            List<SdkDataLevelEntity> levelAll = new ArrayList<>();
            levelAll.addAll(levelLeafList);
            levelAll.addAll(levelParentList);

            List<TreeVO> treePartOfLevel = levelAll.stream().map(level -> {
                TreeVO treeVO = new TreeVO();
                treeVO.setNodeId(String.valueOf(level.getLevelId()));
                treeVO.setNodeName(level.getLevelName());
                treeVO.setParentNodeId(String.valueOf(level.getPid()));
                return treeVO;
            }).collect(Collectors.toList());

            List<TreeVO> nodeAll = new ArrayList<>();
            nodeAll.addAll(treePartOfProduct);
            nodeAll.addAll(treePartOfLevel);

//            List<TreeVO> treeVOList = nodeAll.stream().filter(node -> node.getParentNodeId().equals(Constants.ROOT_ID)).collect(Collectors.toList());
            List<TreeVO> treeVOList = nodeAll.stream().filter(node -> StrUtil.equals(node.getParentNodeId(), Constants.ROOT_ID)).collect(Collectors.toList());
            fill(treeVOList, nodeAll);

            return ResultEntity.success(treeVOList);
        } catch (Exception e) {
            log.warn("查看用户SDK权限出错, 异常信息: {}", e.getMessage());
            throw new BiException("查看用户SDK权限出错, 异常信息: " + e.getMessage());
        }
    }

    @Override
    public ResultEntity<List<UserDmEntity>> listDmByUserId(Integer userId) {

        try {
            List<UserDmEntity> list = userDmMapper.selectList(new LambdaQueryWrapper<UserDmEntity>().eq(UserDmEntity::getUserId, userId));
            return ResultEntity.success(list);
        } catch (Exception e) {
            log.warn("查询用户数据管理权限出错, 异常信息: {}", e.getMessage());
            throw new BiException("查询用户数据管理权限出错, 异常信息: " + e.getMessage());
        }
    }

    @Override
    public ResultEntity<Boolean> delDmByCcid(String ccid) {

        try {
            int delCnt = userDmMapper.delete(new QueryWrapper<UserDmEntity>().lambda()
                    .and(wrapper -> wrapper.eq(UserDmEntity::getDmType, 2)
                            .eq(UserDmEntity::getDmId, ccid))
                    .or(wrapper -> wrapper.eq(UserDmEntity::getDmType, 3)
                            .eq(UserDmEntity::getDmPid, ccid)));
            if (delCnt > 0){
                return ResultEntity.success(true);
            }
            return ResultEntity.failure("delete 0 rows");
        } catch (Exception e) {
            log.warn("删除用户数据管理CCID出错, 异常信息: {}", e.getMessage());
            throw new BiException("删除用户数据管理CCID出错, 异常信息: " + e.getMessage());
        }
    }

    @Override
    public ResultEntity<Boolean> focusProduct(UserProductFocusEntity userProductFocusEntity) {

        try{
            userProductFocusMapper.insertOrUpdate(userProductFocusEntity);
            return ResultEntity.success(true);
        }catch(Exception e){
            log.warn("关注产品线报错了", e);
            throw new BiException("关注产品线报错了", e.getMessage());
        }
    }

    @Override
    public ResultEntity<UserProductFocusEntity> getFocusProduct(Integer userId) {

        try{
            UserProductFocusEntity userProductFocusEntity = userProductFocusMapper.selectOne(new QueryWrapper<UserProductFocusEntity>().lambda().eq(UserProductFocusEntity::getUserId, userId));
            //需要验证当下是否拥有这个产品线的权限
            if(null != userProductFocusEntity){
                List<String> products = userProductMapper.listProductByUser(userId).stream().map(ProductEntity::getProductId).collect(Collectors.toList());
                //olap的产品线也要
                List<OlapUserPermEntity> olapUserPermEntityList = olapUserPermMapper.selectList(new QueryWrapper<OlapUserPermEntity>().lambda().eq(OlapUserPermEntity::getUserId, userId));
                List<String> proIds = olapUserPermEntityList.stream().map(permId -> StrUtil.split(permId.getPermId(), "_")[0]).distinct().collect(Collectors.toList());
                if(!proIds.isEmpty()){
                    List<String> olapProductEntityList = productMapper.selectBatchIds(proIds).stream().map(ProductEntity::getProductId).collect(Collectors.toList());
                    products.addAll(olapProductEntityList);
                }

                //拥有全部产品线  或者  拥有当前产品线
                if((CollectionUtil.contains(products, Constants.KEY_PERM_ALL_PRODUCT)) || CollectionUtil.contains(products, userProductFocusEntity.getProductId())){
                    return ResultEntity.success(userProductFocusEntity);
                }
            }
            return ResultEntity.success(null);
        }catch(Exception e){
            log.warn("查看关注产品线报错了", e);
            throw new BiException("查看关注产品线报错了", e.getMessage());
        }
    }

    private void fillTree(List<TreeVO> ccids, List<TreeVO> usrs) {

        if (CollectionUtil.isNotEmpty(ccids) && CollectionUtil.isNotEmpty(usrs)) {
            ccids.stream().forEach(ccid -> {
                List<TreeVO> list = usrs.stream().filter(usr -> StrUtil.equals(usr.getParentNodeId(), ccid.getNodeId())).collect(Collectors.toList());
                ccid.setChildren(list);
            });
        }
    }

    private List<TreeVO> fillDm(UserEntity user) {
        List<OrgEntity> userOrgs = user.getOrgs();
        //没有部门组织  这里就不展示了
//        if (CollectionUtil.isNotEmpty(userOrgs)) {
        //选中的
        List<String> departmentCodeList = new ArrayList<>();
        List<UserDmEntity> dmEntityList = userDmMapper.selectList(new QueryWrapper<UserDmEntity>().lambda()
                .eq(UserDmEntity::getUserId, user.getId()));
        List<String> departmentCodes = null;
        if (CollectionUtil.isNotEmpty(userOrgs)) {

            List<Integer> orgIds = userOrgs.stream().map(OrgEntity::getOrgId).collect(Collectors.toList());
            List<DepartmentEntity> departmentEntityList = departmentMapper.selectList(new LambdaQueryWrapper<DepartmentEntity>().in(DepartmentEntity::getOrgId, orgIds));
            departmentCodes = departmentEntityList.stream().map(DepartmentEntity::getCode).collect(Collectors.toList());
        }
        if (CollectionUtil.isNotEmpty(departmentCodes)) {
            departmentCodeList.addAll(departmentCodes);
        }
        departmentCodeList.add(user.getCode());
        ResultEntity<List<DmVO>> resultEntity = dataManagementClient.dms(null, departmentCodeList);
        List<DmVO> dmVOS = resultEntity.getData();
        if (CollectionUtil.isNotEmpty(dmVOS)) {
            //必须对应的部门有数据,不然不需要返回
//                    Map<String, Map<String, List<DmVO>>> collect = dmVOS.stream().collect(Collectors.groupingBy(DmVO::getDepartmentName,
//                            Collectors.groupingBy(DmVO::getCcidKey, Collectors.toList())));
//                    System.out.println(JSON.toJSONString(collect));
            List<TreeVO> departments = dmVOS.stream().map(dmVO -> {
                TreeVO treeVO = new TreeVO();
                treeVO.setNodeId(dmVO.getDepartmentCode());
                treeVO.setNodeName(dmVO.getDepartmentName());
                treeVO.setParentNodeId(Constants.ROOT_ID);
                treeVO.setTag(1);
                treeVO.setIndex(treeVO.getNodeId());
                return treeVO;
            }).distinct().collect(Collectors.toList());

            List<TreeVO> ccids = dmVOS.stream().map(dmVO -> {
                TreeVO treeVO = new TreeVO();
                treeVO.setNodeId(dmVO.getCcid());
                treeVO.setNodeName(dmVO.getCcidKey());
                treeVO.setParentNodeId(dmVO.getDepartmentCode());
                treeVO.setTag(2);
                treeVO.setIndex(treeVO.getNodeId());
                return treeVO;
            }).distinct().collect(Collectors.toList());

            List<TreeVO> usrs = dmVOS.stream().map(dmVO -> {
                TreeVO treeVO = new TreeVO();
                treeVO.setNodeId(String.valueOf(dmVO.getUserid()));
                treeVO.setNodeName(dmVO.getUsername());
                treeVO.setParentNodeId(dmVO.getCcid());
                treeVO.setTag(3);
                treeVO.setIndex(SysUtil.dmIndex(treeVO.getParentNodeId(), treeVO.getNodeId()));
                return treeVO;
            }).distinct().collect(Collectors.toList());
            fillTree(ccids, usrs);
            fillTree(departments, ccids);
            return departments;
        }
//        }
        return Collections.emptyList();
    }

    private List<TreeVO> fillProduct(int userId) {

        List<SdkDataLevelEntity> levelAll = sdkDataLevelMapper.selectList(new QueryWrapper<SdkDataLevelEntity>().orderByAsc("type", "idx").orderByDesc("updated_at"));
        List<ProductEntity> productList = productMapper.selectList(new LambdaQueryWrapper<ProductEntity>().isNotNull(ProductEntity::getLevelId));
        List<ProductEntity> checkedProductList = productMapper.listProductByUser(userId);
        List<String> productIds = checkedProductList.stream().map(ProductEntity::getProductId).collect(Collectors.toList());
        List<TreeVO> treeOfProductList = productList.stream().filter(product -> null != product.getLevelId()).map(product -> {
            TreeVO treeVO = new TreeVO();
            String productId = product.getProductId();
            treeVO.setNodeId(productId);
            treeVO.setNodeName(product.getProductName());
            boolean checked = productIds.contains(productId);
            treeVO.setChecked(checked);
            treeVO.setParentNodeId(String.valueOf(product.getLevelId()));
            treeVO.setTag(1);
            return treeVO;
        }).collect(Collectors.toList());

        //先找父亲不行，这里需求要先找儿子
        // 这里可以把 all过滤一层，通过产品绑定额数据层级找到 path
        List<Integer> leafLevelIds = treeOfProductList.stream().map(TreeVO::getParentNodeId).map(Integer::parseInt).distinct().collect(Collectors.toList());
        List<SdkDataLevelEntity> levelLeafList = levelAll.stream().filter(level -> leafLevelIds.contains(level.getLevelId())).collect(Collectors.toList());
        List<Integer> pathIds = levelLeafList.stream().map(SdkDataLevelEntity::getPath).map(path -> StrUtil.split(path, ".")).flatMap(Arrays::stream).filter(StrUtil::isNotEmpty).map(Integer::parseInt).distinct().collect(Collectors.toList());
        pathIds.addAll(leafLevelIds);
        List<SdkDataLevelEntity> levelParentList = sdkDataLevelMapper.selectBatchIds(pathIds);
        List<TreeVO> treeOfLevelList = levelParentList.stream().map(level -> {
            TreeVO treeVO = new TreeVO();
            treeVO.setNodeId(String.valueOf(level.getLevelId()));
            treeVO.setChecked(false);
            treeVO.setNodeName(level.getLevelName());
            treeVO.setParentNodeId(String.valueOf(level.getPid()));
            //这里tag = 0 表示 不可选  1表示可选   前端用
            treeVO.setTag(0);
            return treeVO;
        }).collect(Collectors.toList());

        List<TreeVO> treeAll = new ArrayList<>();
        treeAll.addAll(treeOfLevelList);
        treeAll.addAll(treeOfProductList);

        List<TreeVO> treeFirst = treeAll.stream().filter(tree -> tree.getParentNodeId().equals(Constants.ROOT_ID)).collect(Collectors.toList());
        fill(treeFirst, treeAll);
        return treeFirst;
    }

    private void fill(List<TreeVO> treeFirst, List<TreeVO> treeAll) {
        if (CollectionUtil.isNotEmpty(treeFirst)) {
            treeFirst.forEach(tree -> fillItem(tree, treeAll));
        }
    }

    private void fillItem(TreeVO tree, List<TreeVO> treeAll) {

        String nodeId = tree.getNodeId();
        List<TreeVO> children = treeAll.stream().filter(item -> item.getParentNodeId().equals(nodeId)).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(children)) {
            tree.setChildren(children);
            fill(children, treeAll);
        }
    }
}
