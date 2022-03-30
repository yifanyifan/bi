package com.stnts.bi.sys.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.OlapPermEntity;
import com.stnts.bi.entity.sys.OlapUserPermEntity;
import com.stnts.bi.entity.sys.UserEntity;
import com.stnts.bi.mapper.sys.OlapPermMapper;
import com.stnts.bi.mapper.sys.OlapUserPermMapper;
import com.stnts.bi.sys.common.SysConfig;
import com.stnts.bi.sys.service.OlapPermService;
import com.stnts.bi.sys.utils.UamsUtil;
import com.stnts.bi.sys.vos.olap.OlapPermItemVO;
import com.stnts.bi.sys.vos.olap.OlapPermModVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/1/12
 */
@Service
@Slf4j
public class OlapPermServiceImpl implements OlapPermService {

    @Autowired
    private SysConfig sysConfig;

    @Autowired
    private OlapUserPermMapper olapUserPermMapper;

    @Autowired
    private OlapPermMapper olapPermMapper;

    @Override
    public ResultEntity<List<OlapPermEntity>> modules(Integer userId) {

        try {
            List<OlapPermEntity> entityList = Collections.emptyList();
            //查询当前用户是否用户权限
            List<OlapPermEntity> pageList = listValidPage(userId);
            if (CollectionUtil.isNotEmpty(pageList)) {
                //这里走了个捷径
                List<String> permIdList = pageList.stream().map(OlapPermEntity::getPermId).map(id -> StringUtils.split(id, "_")[0]).distinct().collect(Collectors.toList());
                entityList = olapPermMapper.selectByPermList(permIdList, null);
            }
            return ResultEntity.success(entityList);
        } catch (Exception e) {
            log.warn("[BI-OLAP]查询用户模块信息出错, 异常信息: {}", e.getMessage());
            return ResultEntity.exception(e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultEntity<String> mod(OlapPermModVO olapPermModVO, String type) {

        try {
            if (!olapPermModVO.isReady()) {
                return ResultEntity.param("权限或用户不可为空");
            }
            List<OlapUserPermEntity> olapUserPermEntityList = new ArrayList<>();
            olapPermModVO.getPerms().stream().forEach(perm -> {
                olapPermModVO.getUsers().stream().forEach(user -> {
                    OlapUserPermEntity olapUserPermEntity = OlapUserPermEntity.builder().permId(perm).userId(user).build();
                    olapUserPermEntityList.add(olapUserPermEntity);
                });
            });
            if (StringUtils.equals(type, "one")) {
                //one: 编辑一个权限的用户, 需要先删除原来的，再插入
                String permId = olapPermModVO.getPerms().get(0);
                QueryWrapper<OlapUserPermEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("perm_id", permId);
                olapUserPermMapper.delete(queryWrapper);
                olapUserPermMapper.insertBatch(olapUserPermEntityList);
            } else {
                //many: 只管追加用户
                olapUserPermMapper.insertBatchIgnore(olapUserPermEntityList);
            }
            return ResultEntity.success(null);
        } catch (Exception e) {
            log.warn("[BI-OLAP]修改菜单用户权限出错, 异常信息: {}", e.getMessage());
            return ResultEntity.exception(e.getMessage());
        }
    }

    @Override
    public ResultEntity<List<Integer>> loadUserByPermId(String permId) {

        try {
            QueryWrapper<OlapUserPermEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("perm_id", permId);
            List<OlapUserPermEntity> olapUserPermEntities = olapUserPermMapper.selectList(queryWrapper);
            List<Integer> userIds = olapUserPermEntities.stream().map(OlapUserPermEntity::getUserId).collect(Collectors.toList());
            return ResultEntity.success(userIds);
        } catch (Exception e) {
            log.warn("[BI-OLAP]根据权限获取用户出错, 异常信息: {}", e.getMessage());
            return ResultEntity.exception(e.getMessage());
        }
    }

    @Override
    public ResultEntity<JSONArray> userTree() {

        try {
            long ts = Instant.now().toEpochMilli();
            JSONArray users = UamsUtil.listUserInfo(sysConfig.getEhomeAppId(), sysConfig.getEhomeKey(), ts, sysConfig.getEhomeUserApi());
            return ResultEntity.success(users);
        } catch (Exception e) {
            log.warn("[BI-OLAP]获取UAMS用户树出错, 异常信息: {}", e.getMessage());
            return ResultEntity.exception(e.getMessage());
        }
    }

    /**
     * 这里可以查一个有权限list  再查权限all  再过滤 拼装  不用查这么多次数据库  查了也无所谓
     * @param permId
     * @param userId
     * @param master
     * @return
     */
    @Override
    public ResultEntity<List<OlapPermItemVO>> list(String permId, Integer userId, Integer master) {

        try {

            List<OlapPermItemVO> vos = Collections.emptyList();
            //如果是master=1则表示是当前permId管理员
            userId = master == 1 ? null : userId;
            List<OlapPermEntity> validPageList = listValidPage(userId);
//            List<OlapPermEntity> validPageList = olapPermMapper.listValid(userId);
            validPageList = validPageList.stream().filter(perm -> {
                String rootId = StringUtils.split(perm.getPermId(), "_")[0];
                return StringUtils.equals(permId, rootId);
            }).collect(Collectors.toList());
            //确认有生效中的权限
            if (CollectionUtil.isNotEmpty(validPageList)) {
                //走捷径  permId按_截断第一段为 菜单ID
                //根目录ID集合
                List<String> rootPermIdList = validPageList.stream().map(OlapPermEntity::getPermId).map(perm -> StringUtils.split(perm, "_")[0]).distinct().collect(Collectors.toList());
                //目录ID集合
                List<String> menuPermIdList = validPageList.stream().map(OlapPermEntity::getParentPermId).distinct().collect(Collectors.toList());
                List<String> ids = new ArrayList<>();
                ids.addAll(rootPermIdList);
                ids.addAll(menuPermIdList);
                List<OlapPermEntity> permList = olapPermMapper.selectByPermList(ids, null);
                Map<String, OlapPermEntity> permMap = permList.stream().collect(Collectors.toMap(OlapPermEntity::getPermId, root -> root));
//                List<OlapPermEntity> rootList = olapPermMapper.selectByPermList(rootPermIdList, null);
//                List<OlapPermEntity> menuList = olapPermMapper.selectByPermList(menuPermIdList, null);
//                Map<String, OlapPermEntity> rootMap = rootList.stream().collect(Collectors.toMap(OlapPermEntity::getPermId, root -> root));
//                Map<String, OlapPermEntity> menuMap = menuList.stream().collect(Collectors.toMap(OlapPermEntity::getPermId, menu -> menu));
                vos = validPageList.stream().map(page -> {
                    OlapPermItemVO vo = new OlapPermItemVO();
                    vo.setPermId(page.getPermId());
                    vo.setPageName(page.getPermName());
                    vo.setCreatedAt(page.getCreatedAt());
                    List<String> users = Optional.ofNullable(page.getUsers()).orElse(Collections.emptyList()).stream().map(UserEntity::getCnname).collect(Collectors.toList());
                    vo.setUsers(users);
                    //找目录
                    String menuId = page.getParentPermId();
                    OlapPermEntity menu = permMap.get(menuId);
                    vo.setMenuName(menu.getPermName());
                    //找根目录
                    String rootId = menu.getParentPermId();
                    vo.setModuleName(permMap.get(rootId).getPermName());
                    return vo;
                }).collect(Collectors.toList());
            }
            return ResultEntity.success(vos);
        } catch (Exception e) {
            return ResultEntity.exception(e.getMessage());
        }
    }

    @Override
    public ResultEntity<String> del(String permId) {

        try{
            olapPermMapper.updateStatus(permId);
            return ResultEntity.success(null);
        }catch(Exception e){
            return ResultEntity.exception(e.getMessage());
        }
    }

    /**
     *
     * @param userId
     * @return
     */
    public List<OlapPermEntity> listValidPage(Integer userId) {

        List<OlapPermEntity> validPageList = Collections.emptyList();
        QueryWrapper<OlapUserPermEntity> userPermEntityQueryWrapper = new QueryWrapper<>();
        if(null != userId){
            userPermEntityQueryWrapper.eq("user_id", userId);
        }
        List<OlapUserPermEntity> olapUserPermEntities = olapUserPermMapper.selectList(userPermEntityQueryWrapper);
        if (CollectionUtil.isNotEmpty(olapUserPermEntities)) {
            //表示用户拥有权限，植入请求url 并组装权限树
            List<String> permIds = olapUserPermEntities.stream().map(OlapUserPermEntity::getPermId).collect(Collectors.toList());
            //查出的是页面集合
            validPageList = olapPermMapper.selectByPermListSimple(permIds);
        }
        return validPageList;
    }
}
