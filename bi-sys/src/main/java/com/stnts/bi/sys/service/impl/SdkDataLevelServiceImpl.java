package com.stnts.bi.sys.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.ProductEntity;
import com.stnts.bi.entity.sys.SdkDataLevelEntity;
import com.stnts.bi.exception.BiException;
import com.stnts.bi.mapper.sys.ProductMapper;
import com.stnts.bi.mapper.sys.SdkDataLevelMapper;
import com.stnts.bi.sys.common.Constants;
import com.stnts.bi.sys.service.SdkDataLevelService;
import javafx.scene.layout.BackgroundImage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/5/28
 */
@Service
@Slf4j
public class SdkDataLevelServiceImpl implements SdkDataLevelService {

    @Autowired
    private SdkDataLevelMapper sdkDataLevelMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public ResultEntity<List<SdkDataLevelEntity>> list(String productId) {

        //有个必选
        try {

            ProductEntity productEntity = productMapper.selectById(productId);
            Integer levelId = productEntity.getLevelId();
            List<SdkDataLevelEntity> list = sdkDataLevelMapper.selectList(new QueryWrapper<SdkDataLevelEntity>().orderByAsc("type", "idx").orderByDesc("updated_at"));
            List<SdkDataLevelEntity> levelFirst = list.stream().filter(entity -> entity.getPid() == -1).collect(Collectors.toList());
            fill(levelFirst, list, levelId);
            return ResultEntity.success(levelFirst);
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("数据层级列表查询出错, 异常信息: {}", e.getMessage());
            throw new BiException("数据层级新增出错, 异常信息: " + e.getMessage());
        }
    }

    private void fill(List<SdkDataLevelEntity> levelFirst, List<SdkDataLevelEntity> all, Integer levelId) {
        if (CollectionUtil.isNotEmpty(levelFirst)) {
            levelFirst.forEach(vo -> fillItem(vo, all, levelId));
        }
    }

    private void fillItem(SdkDataLevelEntity vo, List<SdkDataLevelEntity> all, Integer checkedId) {
        int levelId = vo.getLevelId();
        List<SdkDataLevelEntity> list = all.stream().filter(item -> item.getPid() == levelId).collect(Collectors.toList());
        if (null != checkedId && vo.getType() == Constants.LEVEL_TYPE_ROOT && checkedId == levelId) {
            vo.setChecked(true);
        }
        if (CollectionUtil.isNotEmpty(list)) {
            vo.setChildren(list);
            fill(list, all, checkedId);
        }
    }

    @Override
    public ResultEntity<SdkDataLevelEntity> add(SdkDataLevelEntity sdkDataLevelEntity) {

        try {
            if(StrUtil.isEmpty(sdkDataLevelEntity.getLevelName())){
                throw new BiException("数据层级名称不可为空");
            }
            sdkDataLevelMapper.insert(sdkDataLevelEntity);
            return ResultEntity.success(sdkDataLevelEntity);
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("数据层级新增出错, 异常信息: {}", e.getMessage());
            throw new BiException("数据层级新增出错, 异常信息: " + e.getMessage());
        }
    }

    @Override
    public ResultEntity<SdkDataLevelEntity> rename(SdkDataLevelEntity sdkDataLevelEntity) {

        try {
            sdkDataLevelMapper.updateById(sdkDataLevelEntity);
            return ResultEntity.success(sdkDataLevelEntity);
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("数据层级改名出错, 异常信息: {}", e.getMessage());
            throw new BiException("数据层级改名出错, 异常信息: " + e.getMessage());
        }
    }

    @Override
    public ResultEntity<SdkDataLevelEntity> drag(SdkDataLevelEntity sdkDataLevelEntity) {

        try {
//            sdkDataLevelMapper.updateById(sdkDataLevelEntity);
            List<SdkDataLevelEntity> all = new ArrayList<>();
            all.add(sdkDataLevelEntity);
            List<SdkDataLevelEntity> children = sdkDataLevelEntity.getChildren();
            if (CollectionUtil.isNotEmpty(children)) {
                //所有子节点都要更新
//                int pid = sdkDataLevelEntity.getLevelId();
//                List<SdkDataLevelEntity> list = children.stream().filter(item -> item.getPid() == pid).collect(Collectors.toList());
//                updateList(list, children, sdkDataLevelEntity);
                all.addAll(children);
            }
            sdkDataLevelMapper.updateBatch(all);
            return ResultEntity.success(sdkDataLevelEntity);
        } catch (Exception e) {
            log.warn("数据层级拖拽出错, 异常信息: {}", e.getMessage());
            throw e;
        }
    }

    private void updateList(List<SdkDataLevelEntity> list, List<SdkDataLevelEntity> children, SdkDataLevelEntity sdkDataLevelEntity) {

        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(item -> updateListItem(item, children, sdkDataLevelEntity));
        }
    }

    private void updateListItem(SdkDataLevelEntity item, List<SdkDataLevelEntity> all, SdkDataLevelEntity parent) {

        item.setPath(parent.getPath().concat(".").concat(String.valueOf(parent.getLevelId())));
        List<SdkDataLevelEntity> children = item.getChildren();
        if (CollectionUtil.isNotEmpty(children)) {
            updateList(children, all, item);
        }
    }

    @Override
    public ResultEntity<Boolean> del(SdkDataLevelEntity sdkDataLevelEntity) {

        try {
            /**
             * 如果层级绑定了产品, 则不能删除
             * 如果是叶子节点
             * 如果是普通节点, 继续找下面所有叶子节点
             */
            String pathPrefix = sdkDataLevelEntity.getPath().concat(".").concat(String.valueOf(sdkDataLevelEntity.getLevelId()));
            List<SdkDataLevelEntity> dirList = sdkDataLevelMapper.selectList(new QueryWrapper<SdkDataLevelEntity>().lambda()
                    .likeRight(SdkDataLevelEntity::getPath, pathPrefix).or()
                    .eq(SdkDataLevelEntity::getLevelId, sdkDataLevelEntity.getLevelId()));
            List<SdkDataLevelEntity> dirRootList = dirList.stream().filter(dir -> dir.getType() == 2).collect(Collectors.toList());
            List<Integer> dirIdList = dirList.stream().map(SdkDataLevelEntity::getLevelId).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(dirRootList)) {
                sdkDataLevelMapper.deleteBatchIds(dirIdList);
            } else {
                List<Integer> dirRootIdList = dirRootList.stream().map(SdkDataLevelEntity::getLevelId).collect(Collectors.toList());
                Integer selectCount = productMapper.selectCount(new QueryWrapper<ProductEntity>().lambda()
                        .in(ProductEntity::getLevelId, dirRootIdList));
                if (selectCount > 0) {
                    throw new BiException("数据层级被使用，无法删除。");
                } else {
                    sdkDataLevelMapper.deleteBatchIds(dirIdList);
                }
            }
            return ResultEntity.success(true);
        } catch (Exception e) {
            if (e instanceof BiException) {
                throw e;
            }
            log.warn("数据层级删除出错, 异常信息: {}", e.getMessage());
            throw new BiException("数据层级删除出错, 异常信息: " + e.getMessage());
        }
    }
}
