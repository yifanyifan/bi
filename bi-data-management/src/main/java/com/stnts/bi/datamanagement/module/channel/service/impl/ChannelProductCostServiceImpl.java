package com.stnts.bi.datamanagement.module.channel.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stnts.bi.common.BiSessionUtil;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelClass;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelCooperation;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelProductCost;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelProductCostCooperation;
import com.stnts.bi.datamanagement.module.channel.mapper.*;
import com.stnts.bi.datamanagement.module.channel.param.ChannelProductCostPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelProductCostCooperationService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelProductCostService;
import com.stnts.bi.datamanagement.module.channel.vo.ChannelClassNode;
import com.stnts.bi.datamanagement.module.channel.vo.ChannelProductCostVO;
import com.stnts.bi.datamanagement.util.DozerUtil;
import com.stnts.bi.entity.common.PageEntity;
import com.stnts.bi.entity.sys.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 产品分成 服务实现类
 *
 * @author 易樊
 * @since 2021-09-24
 */
@Slf4j
@Service
public class ChannelProductCostServiceImpl extends ServiceImpl<ChannelProductCostMapper, ChannelProductCost> implements ChannelProductCostService {

    @Autowired
    private ChannelProductCostMapper channelProductCostMapper;
    @Autowired
    private ChannelProductCostCooperationMapper channelProductCostCooperationMapper;
    @Autowired
    private ChannelProductCostCooperationService channelProductCostCooperationService;
    @Autowired
    private ChannelClassMapper channelClassMapper;
    @Autowired
    private ChannelProductMapper channelProductMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ChannelCooperationMapper channelCooperationMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveChannelProductCost(ChannelProductCost channelProductCost, HttpServletRequest request) throws Exception {
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();

        channelProductCost.setUserid(Long.valueOf(user.getId()));
        channelProductCost.setUsername(user.getCnname());
        super.save(channelProductCost);

        List<ChannelProductCostCooperation> channelProductCostCooperationList = new ArrayList<ChannelProductCostCooperation>();

        List<Map<String, Object>> selectRequest = channelProductCost.getSelectRequest();
        List<String> selectByAll = selectRequest.stream().filter(i -> "1".equals(String.valueOf(i.get("modeType")))).map(i -> String.valueOf(i.get("id"))).collect(Collectors.toList());
        List<String> selectByChannelClass = selectRequest.stream().filter(i -> "2".equals(String.valueOf(i.get("modeType")))).map(i -> String.valueOf(i.get("id"))).collect(Collectors.toList());
        List<String> selectByCCID = selectRequest.stream().filter(i -> "3".equals(String.valueOf(i.get("modeType")))).map(i -> String.valueOf(i.get("id"))).collect(Collectors.toList());
        List<String> selectByNo = selectRequest.stream().filter(i -> "4".equals(String.valueOf(i.get("modeType")))).map(i -> String.valueOf(i.get("id"))).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(selectByAll)) {
            getSub(selectByAll, "1", channelProductCost.getId(), channelProductCostCooperationList);
        }
        if (CollectionUtil.isNotEmpty(selectByChannelClass)) {
            getSub(selectByChannelClass, "2", channelProductCost.getId(), channelProductCostCooperationList);
        }
        if (CollectionUtil.isNotEmpty(selectByCCID)) {
            getSub(selectByCCID, "3", channelProductCost.getId(), channelProductCostCooperationList);
        }
        if (CollectionUtil.isNotEmpty(selectByNo)) {
            getSub(selectByNo, "4", channelProductCost.getId(), channelProductCostCooperationList);
        }
        if (CollectionUtil.isNotEmpty(channelProductCostCooperationList)) {
            channelProductCostCooperationService.saveBatch(channelProductCostCooperationList);
        }

        return true;
    }

    public void getSub(List<String> selectList, String modeType, Long channelProductCostId, List<ChannelProductCostCooperation> channelProductCostCooperationList) {
        for (String sub : selectList) {
            ChannelProductCostCooperation channelProductCostCooperation = new ChannelProductCostCooperation();
            channelProductCostCooperation.setChannelProductCostId(channelProductCostId);
            channelProductCostCooperation.setModeId(sub);
            channelProductCostCooperation.setModeType(modeType);

            channelProductCostCooperationList.add(channelProductCostCooperation);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateChannelProductCost(ChannelProductCost channelProductCost) throws Exception {
        ChannelProductCost channelProductCostDB = channelProductCostMapper.selectById(channelProductCost.getId());
        BeanUtils.copyProperties(channelProductCost, channelProductCostDB, "id");
        if (ObjectUtil.isEmpty(channelProductCostDB.getChannelRate())) {
            channelProductCostDB.setChannelRate(BigDecimal.ZERO);
        }
        super.updateById(channelProductCostDB);

        List<Map<String, Object>> selectRequest = channelProductCostDB.getSelectRequest();
        List<String> selectByAll = selectRequest.stream().filter(i -> "1".equals(String.valueOf(i.get("modeType")))).map(i -> String.valueOf(i.get("id"))).collect(Collectors.toList());
        List<String> selectByChannelClass = selectRequest.stream().filter(i -> "2".equals(String.valueOf(i.get("modeType")))).map(i -> String.valueOf(i.get("id"))).collect(Collectors.toList());
        List<String> selectByCCID = selectRequest.stream().filter(i -> "3".equals(String.valueOf(i.get("modeType")))).map(i -> String.valueOf(i.get("id"))).collect(Collectors.toList());
        List<String> selectByNo = selectRequest.stream().filter(i -> "4".equals(String.valueOf(i.get("modeType")))).map(i -> String.valueOf(i.get("id"))).collect(Collectors.toList());

        List<ChannelProductCostCooperation> channelProductCostCooperationDBList = channelProductCostCooperationMapper.selectList(new LambdaQueryWrapper<ChannelProductCostCooperation>().eq(ChannelProductCostCooperation::getChannelProductCostId, channelProductCost.getId()));
        Map<String, List<ChannelProductCostCooperation>> channelProductCostCooperationDBMap = channelProductCostCooperationDBList.stream().collect(Collectors.groupingBy(ChannelProductCostCooperation::getModeType));

        List<String> allList = new ArrayList<String>();
        List<ChannelProductCostCooperation> c1 = channelProductCostCooperationDBMap.get("1");
        if (CollectionUtil.isNotEmpty(c1)) {
            allList = c1.stream().map(ChannelProductCostCooperation::getModeId).collect(Collectors.toList());
        }
        List<String> channelTypeList = new ArrayList<String>();
        List<ChannelProductCostCooperation> c2 = channelProductCostCooperationDBMap.get("2");
        if (CollectionUtil.isNotEmpty(c2)) {
            channelTypeList = c2.stream().map(ChannelProductCostCooperation::getModeId).collect(Collectors.toList());
        }
        List<String> ccidList = new ArrayList<String>();
        List<ChannelProductCostCooperation> c3 = channelProductCostCooperationDBMap.get("3");
        if (CollectionUtil.isNotEmpty(c3)) {
            ccidList = c3.stream().map(ChannelProductCostCooperation::getModeId).collect(Collectors.toList());
        }
        List<String> noList = new ArrayList<String>();
        List<ChannelProductCostCooperation> c4 = channelProductCostCooperationDBMap.get("4");
        if (CollectionUtil.isNotEmpty(c4)) {
            noList = c4.stream().map(ChannelProductCostCooperation::getModeId).collect(Collectors.toList());
        }

        //判断多个用户同时操作，当前用户勾选的所有项，在当前部门下别的节点中，是否有匹配的【只需判断别的节点是否被其他人勾选，不用判断当前节点是否被其它用户勾选，若勾选了则直接用删除或新增即可】
        checkTree(channelProductCostDB, selectByAll, selectByChannelClass, selectByCCID, selectByNo);

        //全部
        toChannelProductCostCooperation(channelProductCostDB, allList, selectByAll, "1");
        //渠道分类
        toChannelProductCostCooperation(channelProductCostDB, channelTypeList, selectByChannelClass, "2");
        //CCID
        toChannelProductCostCooperation(channelProductCostDB, ccidList, selectByCCID, "3");
        //未知
        toChannelProductCostCooperation(channelProductCostDB, noList, selectByNo, "4");

        return true;
    }

    /**
     * 判断多个用户同时操作，当前用户勾选的所有项，在当前部门下别的节点中，是否有匹配的【只需判断别的节点是否被其他人勾选，不用判断当前节点是否被其它用户勾选，若勾选了则直接用删除或新增即可】
     *
     * @param channelProductCostDB
     * @param selectByAll
     * @param selectByChannelClass
     * @param selectByCCID
     * @param selectByNo
     */
    private void checkTree(ChannelProductCost channelProductCostDB, List<String> selectByAll, List<String> selectByChannelClass, List<String> selectByCCID, List<String> selectByNo) {
        if (CollectionUtil.isNotEmpty(selectByAll)) {
            Long count = channelProductCostMapper.countByProductCode(channelProductCostDB.getProductCode(), String.valueOf(channelProductCostDB.getId()));
            if (count > 0) {
                throw new BusinessException("【全部 选项】与其它Tab页冲突，请检查");
            }
        }

        ChannelProductCostPageParam channelProductCostPageParam = new ChannelProductCostPageParam();
        channelProductCostPageParam.setProductCode(channelProductCostDB.getProductCode());
        channelProductCostPageParam.setCostIdNo(String.valueOf(channelProductCostDB.getId()));
        if (CollectionUtil.isNotEmpty(selectByChannelClass) || CollectionUtil.isNotEmpty(selectByCCID)) {
            channelProductCostPageParam.setSelectByChannelClass(selectByChannelClass);
            channelProductCostPageParam.setSelectByCCID(selectByCCID);
            List<ChannelCooperation> channelCooperationList = channelProductCostMapper.getCCIDCostExt(channelProductCostPageParam);
            if (CollectionUtil.isNotEmpty(channelCooperationList)) {
                throw new BusinessException("【分类/CCID 选项】与其它Tab页冲突，请检查");
            }
        }

        if (CollectionUtil.isNotEmpty(selectByNo)) {
            channelProductCostPageParam.setOnlyNo("1");
            List<ChannelCooperation> channelCooperationList = channelProductCostMapper.getCCIDCostExt(channelProductCostPageParam);
            if (CollectionUtil.isNotEmpty(channelCooperationList)) {
                throw new BusinessException("【未知 选项】与其它Tab页冲突，请检查");
            }
        }
    }

    public void toChannelProductCostCooperation(ChannelProductCost channelProductCost, List<String> dbList, List<String> selectList, String modeType) {
        //删除
        List<String> dbTmpList = new ArrayList<String>(dbList);
        if (CollectionUtil.isNotEmpty(dbTmpList)) {
            dbTmpList.removeAll(selectList);
            if (CollectionUtil.isNotEmpty(dbTmpList)) {
                channelProductCostCooperationMapper.delete(new LambdaQueryWrapper<ChannelProductCostCooperation>().eq(ChannelProductCostCooperation::getChannelProductCostId, channelProductCost.getId()).in(ChannelProductCostCooperation::getModeId, dbTmpList));
            }
        }
        //新增
        if (CollectionUtil.isNotEmpty(selectList)) {
            selectList.removeAll(dbList);
            if (CollectionUtil.isNotEmpty(selectList)) {
                List<ChannelProductCostCooperation> channelProductCostCooperationListTemp = new ArrayList<ChannelProductCostCooperation>();
                for (String sb : selectList) {
                    ChannelProductCostCooperation channelProductCostCooperation = new ChannelProductCostCooperation();
                    channelProductCostCooperation.setChannelProductCostId(channelProductCost.getId());
                    channelProductCostCooperation.setModeId(sb);
                    channelProductCostCooperation.setModeType(modeType);
                    channelProductCostCooperationListTemp.add(channelProductCostCooperation);
                }
                channelProductCostCooperationService.saveBatch(channelProductCostCooperationListTemp);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteChannelProductCost(Long id) throws Exception {
        super.removeById(id);

        channelProductCostCooperationMapper.delete(new LambdaQueryWrapper<ChannelProductCostCooperation>().eq(ChannelProductCostCooperation::getChannelProductCostId, id));

        return true;
    }


    @Override
    public PageEntity<ChannelProductCost> getChannelProductCostPageList(ChannelProductCostPageParam channelProductCostPageParam) throws Exception {
        Page<ChannelProductCost> page = new Page<>(channelProductCostPageParam.getPageIndex(), channelProductCostPageParam.getPageSize());
        LambdaQueryWrapper<ChannelProductCost> wrapper = getLambdaQueryWrapper(channelProductCostPageParam);
        IPage<ChannelProductCost> iPage = channelProductCostMapper.selectPage(page, wrapper);
        return new PageEntity<ChannelProductCost>(iPage);
    }

    @Override
    public List<ChannelProductCost> getChannelProductCostList(ChannelProductCostPageParam channelProductCostPageParam) throws Exception {
        LambdaQueryWrapper<ChannelProductCost> wrapper = getLambdaQueryWrapper(channelProductCostPageParam);
        List<ChannelProductCost> ChannelProductCostList = channelProductCostMapper.selectList(wrapper);
        return ChannelProductCostList;
    }

    @Override
    public List<ChannelProductCostVO> getChannelProductCost(String productCode) {
        //查询所有Tab页
        List<ChannelProductCost> channelProductCostDBList = channelProductCostMapper.selectList(new LambdaQueryWrapper<ChannelProductCost>().eq(ChannelProductCost::getProductCode, productCode));
        List<Long> channelProductCostIdDBList = channelProductCostDBList.stream().map(ChannelProductCost::getId).collect(Collectors.toList());

        Map<Long, List<ChannelProductCostCooperation>> channelProductCostCooperationDBMap = new HashMap<Long, List<ChannelProductCostCooperation>>();
        if (CollectionUtil.isNotEmpty(channelProductCostIdDBList)) {
            //查询所有Tab页的勾选项
            List<ChannelProductCostCooperation> channelProductCostCooperationDBList = channelProductCostCooperationMapper.selectList(new LambdaQueryWrapper<ChannelProductCostCooperation>().in(ChannelProductCostCooperation::getChannelProductCostId, channelProductCostIdDBList));
            //勾选项按Tab页面拆分
            channelProductCostCooperationDBMap = channelProductCostCooperationDBList.stream().collect(Collectors.groupingBy(ChannelProductCostCooperation::getChannelProductCostId));
        }

        List<ChannelProductCostVO> channelProductCostVOList = new ArrayList<ChannelProductCostVO>();
        //遍历每个Tab页
        for (ChannelProductCost channelProductCost : channelProductCostDBList) {
            ChannelProductCostVO channelProductCostVO = DozerUtil.toBean(channelProductCost, ChannelProductCostVO.class);

            // -----------------------> 自己Tab页面按选择类型分
            List<String> selectByAll = new ArrayList<String>(); // 选择全部
            List<String> selectByChannelClass = new ArrayList<String>();    //选择渠道类型
            List<String> selectByCCID = new ArrayList<String>();    //选择CCID
            List<String> selectByNo = new ArrayList<String>();  //选择未知
            if (channelProductCostCooperationDBMap.containsKey(channelProductCost.getId())) {
                // 当前Tab页按选择类型分
                List<ChannelProductCostCooperation> channelProductCostCooperationList = channelProductCostCooperationDBMap.get(channelProductCost.getId());
                Map<String, List<ChannelProductCostCooperation>> channelProductCostCooperationMap = channelProductCostCooperationList.stream().collect(Collectors.groupingBy(ChannelProductCostCooperation::getModeType));

                splitList(channelProductCostCooperationMap, selectByAll, selectByChannelClass, selectByCCID, selectByNo);
            }
            // 处理树
            channelProductCostVO.setSelectByAll(selectByAll);
            channelProductCostVO.setSelectByChannelClass(selectByChannelClass);
            channelProductCostVO.setSelectByCCID(selectByCCID);
            channelProductCostVO.setSelectByNo(selectByNo);

            // -----------------------> 其它Tab页按选择类型分
            List<String> selectByAllOther = new ArrayList<String>(); // 选择全部
            List<String> selectByChannelClassOther = new ArrayList<String>();    //选择渠道类型
            List<String> selectByCCIDOther = new ArrayList<String>();    //选择CCID
            List<String> selectByNoOther = new ArrayList<String>();  //选择未知

            List<ChannelProductCostCooperation> other = new ArrayList<ChannelProductCostCooperation>();
            for (Map.Entry<Long, List<ChannelProductCostCooperation>> entry : channelProductCostCooperationDBMap.entrySet()) {
                Long key = entry.getKey();
                List<ChannelProductCostCooperation> value = entry.getValue();
                if (!key.equals(channelProductCost.getId())) {
                    other.addAll(value);
                }
            }
            Map<String, List<ChannelProductCostCooperation>> otherMap = other.stream().collect(Collectors.groupingBy(ChannelProductCostCooperation::getModeType));
            splitList(otherMap, selectByAllOther, selectByChannelClassOther, selectByCCIDOther, selectByNoOther);

            //设置父ID
            ChannelClassNode channelClassNode = this.getTreeStructure(productCode);
            DGFindSet(channelProductCostVO, channelClassNode, selectByChannelClassOther, selectByCCIDOther, selectByNoOther);

            // 处理树
            channelProductCostVO.setSelectByAllOther(selectByAllOther);
            channelProductCostVO.setSelectByChannelClassOther(selectByChannelClassOther);
            channelProductCostVO.setSelectByCCIDOther(selectByCCIDOther);
            channelProductCostVO.setSelectByNoOther(selectByNoOther);

            channelProductCostVO.setSelectByAll(CollectionUtil.isNotEmpty(channelProductCostVO.getSelectByAll()) ? channelProductCostVO.getSelectByAll().stream().filter(i -> !i.contains("999")).distinct().collect(Collectors.toList()) : null);
            channelProductCostVO.setSelectByChannelClass(CollectionUtil.isNotEmpty(channelProductCostVO.getSelectByChannelClass()) ? channelProductCostVO.getSelectByChannelClass().stream().filter(i -> !i.contains("999")).distinct().collect(Collectors.toList()) : null);
            channelProductCostVO.setSelectByCCID(CollectionUtil.isNotEmpty(channelProductCostVO.getSelectByCCID()) ? channelProductCostVO.getSelectByCCID().stream().filter(i -> !i.contains("999")).distinct().collect(Collectors.toList()) : null);
            channelProductCostVO.setSelectByNo(CollectionUtil.isNotEmpty(channelProductCostVO.getSelectByNo()) ? channelProductCostVO.getSelectByNo().stream().filter(i -> !i.contains("999")).distinct().collect(Collectors.toList()) : null);
            channelProductCostVO.setSelectByAllOther(CollectionUtil.isNotEmpty(channelProductCostVO.getSelectByAllOther()) ? channelProductCostVO.getSelectByAllOther().stream().filter(i -> !i.contains("999")).distinct().collect(Collectors.toList()) : null);
            channelProductCostVO.setSelectByChannelClassOther(CollectionUtil.isNotEmpty(channelProductCostVO.getSelectByChannelClassOther()) ? channelProductCostVO.getSelectByChannelClassOther().stream().filter(i -> !i.contains("999")).distinct().collect(Collectors.toList()) : null);
            channelProductCostVO.setSelectByCCIDOther(CollectionUtil.isNotEmpty(channelProductCostVO.getSelectByCCIDOther()) ? channelProductCostVO.getSelectByCCIDOther().stream().filter(i -> !i.contains("999")).distinct().collect(Collectors.toList()) : null);
            channelProductCostVO.setSelectByNoOther(CollectionUtil.isNotEmpty(channelProductCostVO.getSelectByNoOther()) ? channelProductCostVO.getSelectByNoOther().stream().filter(i -> !i.contains("999")).distinct().collect(Collectors.toList()) : null);
            channelProductCostVO.setSelectByAllOtherParent(CollectionUtil.isNotEmpty(channelProductCostVO.getSelectByAllOtherParent()) ? channelProductCostVO.getSelectByAllOtherParent().stream().filter(i -> !i.contains("999")).distinct().collect(Collectors.toList()) : null);
            channelProductCostVO.setSelectByChannelClassOtherParent(CollectionUtil.isNotEmpty(channelProductCostVO.getSelectByChannelClassOtherParent()) ? channelProductCostVO.getSelectByChannelClassOtherParent().stream().filter(i -> !i.contains("999")).distinct().collect(Collectors.toList()) : null);
            channelProductCostVO.setSelectByCCIDOtherParent(CollectionUtil.isNotEmpty(channelProductCostVO.getSelectByCCIDOtherParent()) ? channelProductCostVO.getSelectByCCIDOtherParent().stream().filter(i -> !i.contains("999")).distinct().collect(Collectors.toList()) : null);
            channelProductCostVO.setSelectByNoOtherParent(CollectionUtil.isNotEmpty(channelProductCostVO.getSelectByNoOtherParent()) ? channelProductCostVO.getSelectByNoOtherParent().stream().filter(i -> !i.contains("999")).distinct().collect(Collectors.toList()) : null);

            channelProductCostVOList.add(channelProductCostVO);
        }

        return channelProductCostVOList;
    }

    public void DGFindSet(ChannelProductCostVO channelProductCostVO, ChannelClassNode channelClassNode, List<String> selectByChannelClassOther, List<String> selectByCCIDOther, List<String> selectByNoOther) {
        // 求每个渠道类型的父级
        List<String> channelClassParentList = DGFind(channelClassNode, selectByChannelClassOther);
        if (CollectionUtil.isNotEmpty(channelClassParentList)) {
            channelClassParentList = channelClassParentList.stream().distinct().filter(i -> !selectByChannelClassOther.contains(i)).collect(Collectors.toList());
            channelProductCostVO.setSelectByChannelClassOtherParent(channelClassParentList);
        }
        // 求每个CCID的父级
        List<String> ccidParentList = DGFind(channelClassNode, selectByCCIDOther);
        if (CollectionUtil.isNotEmpty(ccidParentList)) {
            ccidParentList = ccidParentList.stream().distinct().filter(i -> !selectByCCIDOther.contains(i)).collect(Collectors.toList());
            channelProductCostVO.setSelectByCCIDOtherParent(ccidParentList);
        }
        // 未知的父级
        if (CollectionUtil.isNotEmpty(selectByNoOther)) {
            channelProductCostVO.setSelectByNoOtherParent(new ArrayList<String>(Arrays.asList("-1", "0")));
        }
    }

    public List<String> DGFind(ChannelClassNode channelClassNode, List<String> targetStrList) {
        List<String> parentAll = new ArrayList<String>();

        List<ChannelClassNode> channelClassNodeList = channelClassNode.getChannelClassNodeList();
        if (CollectionUtil.isNotEmpty(channelClassNodeList)) {
            //子节点所有值
            List<String> subListAll = new ArrayList<String>();

            for (ChannelClassNode channelClassNodeSub : channelClassNodeList) {
                List<String> idListBySub = DGFind(channelClassNodeSub, targetStrList);
                subListAll.addAll(idListBySub); // 当前节点的子集合
            }

            //子节点是否有值返回
            if (CollectionUtil.isNotEmpty(subListAll)) {
                parentAll.addAll(subListAll);
                parentAll.add(channelClassNode.getId());
            }
        }

        //当前节点是被勾选
        if (targetStrList.contains(channelClassNode.getId())) {
            parentAll.add(channelClassNode.getId());
        }
        return parentAll;
    }

    public void splitList(Map<String, List<ChannelProductCostCooperation>> channelProductCostCooperationMap, List<String> selectByAll, List<String> selectByChannelClass, List<String> selectByCCID, List<String> selectByNo) {
        if (channelProductCostCooperationMap.containsKey("1")) {
            List<String> modeId = channelProductCostCooperationMap.get("1").stream().map(ChannelProductCostCooperation::getModeId).collect(Collectors.toList());
            selectByAll.addAll(modeId);
        }
        if (channelProductCostCooperationMap.containsKey("2")) {
            List<String> modeId = channelProductCostCooperationMap.get("2").stream().map(ChannelProductCostCooperation::getModeId).collect(Collectors.toList());
            selectByChannelClass.addAll(modeId);
        }
        if (channelProductCostCooperationMap.containsKey("3")) {
            List<String> modeId = channelProductCostCooperationMap.get("3").stream().map(ChannelProductCostCooperation::getModeId).collect(Collectors.toList());
            selectByCCID.addAll(modeId);
        }
        if (channelProductCostCooperationMap.containsKey("4")) {
            List<String> modeId = channelProductCostCooperationMap.get("4").stream().map(ChannelProductCostCooperation::getModeId).collect(Collectors.toList());
            selectByNo.addAll(modeId);
        }
    }

    @Override
    public ChannelProductCostVO getTreeByNew(String productCode) {
        ChannelProductCostVO channelProductCostVO = new ChannelProductCostVO();

        //查询Tab页勾选项
        List<ChannelProductCostCooperation> channelProductCostCooperationDBList = channelProductCostCooperationMapper.selectListAll(productCode, null);
        Map<String, List<ChannelProductCostCooperation>> channelProductCostCooperationMap = channelProductCostCooperationDBList.stream().collect(Collectors.groupingBy(ChannelProductCostCooperation::getModeType));

        List<String> selectByAll = new ArrayList<String>();
        List<String> selectByChannelClass = new ArrayList<String>();
        List<String> selectByCCID = new ArrayList<String>();
        List<String> selectByNo = new ArrayList<String>();
        splitList(channelProductCostCooperationMap, selectByAll, selectByChannelClass, selectByCCID, selectByNo);

        //设置父ID
        ChannelClassNode channelClassNode = this.getTreeStructure(productCode);
        DGFindSet(channelProductCostVO, channelClassNode, selectByChannelClass, selectByCCID, selectByNo);

        channelProductCostVO.setSelectByAllOther(selectByAll);
        channelProductCostVO.setSelectByChannelClassOther(selectByChannelClass);
        channelProductCostVO.setSelectByCCIDOther(selectByCCID);
        channelProductCostVO.setSelectByNoOther(selectByNo);

        return channelProductCostVO;
    }

    @Override
    public ChannelProductCostVO getTreeForUpdate(String productCode, String costId) {
        ChannelProductCostVO channelProductCostVO = new ChannelProductCostVO();

        //查询Tab页勾选项（本节点勾选）
        List<ChannelProductCostCooperation> channelProductCostCooperationDBList = channelProductCostCooperationMapper.selectListAll(productCode, costId);
        Map<String, List<ChannelProductCostCooperation>> channelProductCostCooperationMap = channelProductCostCooperationDBList.stream().collect(Collectors.groupingBy(ChannelProductCostCooperation::getModeType));
        //查询Tab页勾选项（其它节点勾选）
        List<ChannelProductCostCooperation> channelProductCostCooperationDBList2 = channelProductCostCooperationMapper.selectListAll2(productCode, costId);
        Map<String, List<ChannelProductCostCooperation>> channelProductCostCooperationMap2 = channelProductCostCooperationDBList2.stream().collect(Collectors.groupingBy(ChannelProductCostCooperation::getModeType));

        List<String> selectByAll = new ArrayList<String>();
        List<String> selectByChannelClass = new ArrayList<String>();
        List<String> selectByCCID = new ArrayList<String>();
        List<String> selectByNo = new ArrayList<String>();
        splitList(channelProductCostCooperationMap, selectByAll, selectByChannelClass, selectByCCID, selectByNo);

        List<String> selectByAllOther = new ArrayList<String>();
        List<String> selectByChannelClassOther = new ArrayList<String>();
        List<String> selectByCCIDOther = new ArrayList<String>();
        List<String> selectByNoOther = new ArrayList<String>();
        splitList(channelProductCostCooperationMap2, selectByAllOther, selectByChannelClassOther, selectByCCIDOther, selectByNoOther);

        channelProductCostVO.setSelectByAll(selectByAll);
        channelProductCostVO.setSelectByChannelClass(selectByChannelClass);
        channelProductCostVO.setSelectByCCID(selectByCCID);
        channelProductCostVO.setSelectByNo(selectByNo);

        //设置父ID
        ChannelClassNode channelClassNode = this.getTreeStructure(productCode);
        DGFindSet(channelProductCostVO, channelClassNode, selectByChannelClassOther, selectByCCIDOther, selectByNoOther);

        channelProductCostVO.setSelectByAllOther(selectByAllOther);
        channelProductCostVO.setSelectByChannelClassOther(selectByChannelClassOther);
        channelProductCostVO.setSelectByCCIDOther(selectByCCIDOther);
        channelProductCostVO.setSelectByNoOther(selectByNoOther);

        return channelProductCostVO;
    }

    public List<ChannelClassNode> getDG(Map<String, List<ChannelClassNode>> channelClassNodeMap, Map<String, List<ChannelClassNode>> yes, String nodeId) {
        if (channelClassNodeMap.containsKey(nodeId)) {
            List<ChannelClassNode> channelClassNodesSub = channelClassNodeMap.get(nodeId);

            for (ChannelClassNode channelClassNode : channelClassNodesSub) {
                List<ChannelClassNode> listSub = getDG(channelClassNodeMap, yes, channelClassNode.getId());
                if (CollectionUtil.isNotEmpty(listSub)) {
                    channelClassNode.setChannelClassNodeList(listSub);
                } else {
                    if (yes.containsKey(channelClassNode.getId())) {
                        List<ChannelClassNode> channelClassNodeList = yes.get(channelClassNode.getId());
                        channelClassNodeList.stream().forEach(i -> i.setName("[" + i.getId() + "]" + i.getName()));
                        channelClassNode.setCcidList(channelClassNodeList);
                    }
                }
            }
            return channelClassNodesSub;
        }
        return new ArrayList<ChannelClassNode>();
    }

    @Override
    public ChannelClassNode getTree(String productCode) {
        List<ChannelClassNode> channelClassNodeList = new ArrayList<ChannelClassNode>();

        //树结构【用法1】
        // 1. 当前产品下有PID所对应部门 在 【渠道类型】 中ID
        List<String> idList = channelClassMapper.getChannelClassIdByProductCode(productCode);
        if (CollectionUtil.isNotEmpty(idList)) {
            // 2. 部门在【渠道类型】中的所有子节点
            List<ChannelClass> channelClassList = channelClassMapper.getDownLoadByIdList(idList);
            List<Long> channelClassIdList = channelClassList.stream().map(ChannelClass::getId).collect(Collectors.toList());
            // 3. 查【渠道类型】中子节点列表，并根据父级ID进行分组
            List<ChannelClassNode> listByChannelClassIdList = channelClassMapper.selectByChannelClassIdList(channelClassIdList, productCode);
            Map<String, List<ChannelClassNode>> channelClassNodeMap = listByChannelClassIdList.stream().collect(Collectors.groupingBy(ChannelClassNode::getParentId));
            // 4. 查对应已勾选的CCID
            List<ChannelClassNode> channelClassNodeYesList = channelClassMapper.ccidListByProductBatch(channelClassIdList, productCode);
            Map<String, List<ChannelClassNode>> yes = channelClassNodeYesList.stream().collect(Collectors.groupingBy(ChannelClassNode::getChannelClassId));
            // 5. 进行递归整理
            channelClassNodeList = getDG(channelClassNodeMap, yes, "-1");
            // 6. 去除部门层级（可能存在多个部门）
            List<ChannelClassNode> channelClassNodeListAll = new ArrayList<ChannelClassNode>();
            for (ChannelClassNode channelClassNode : channelClassNodeList) {
                List<ChannelClassNode> channelClassNodeListSub = channelClassNode.getChannelClassNodeList();
                if (CollectionUtil.isNotEmpty(channelClassNodeListSub)) {
                    channelClassNodeListAll.addAll(channelClassNodeListSub);
                }
            }
            channelClassNodeList = channelClassNodeListAll;
        }

        ChannelClassNode root = new ChannelClassNode();
        root.setId("-1");
        root.setName("全部");
        root.setModeType("1");
        root.setChannelClassNodeList(channelClassNodeList);

        //已在渠道类型勾选的CCID
        List<String> ccidAllList = getRootCCID(root);
        //获取全部CCID
        List<ChannelClassNode> ccidList = channelProductCostMapper.getAllCCIDByProduct(productCode);

        // 未知CCID集合
        ccidList = ccidList.stream().filter(i -> !ccidAllList.contains(i.getId())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(ccidList)) {
            ChannelClassNode other = new ChannelClassNode();
            other.setId("0");
            other.setName("未知");
            other.setModeType("4");
            other.setChannelClassNodeList(ccidList);

            channelClassNodeList.add(other);
        }

        return root;
    }

    /*@Override
    public Integer noSet(String productCode) {
        //树结构
        ChannelProductCostPageParam channelProductCostPageParam = new ChannelProductCostPageParam();
        channelProductCostPageParam.setProductCode(productCode);
        List<ChannelCooperation> channelCooperationList = channelProductCostMapper.getCCIDCostExt(channelProductCostPageParam);
        //获取全部CCID
        List<ChannelClassNode> ccidList = channelProductCostMapper.getAllCCIDByProduct(productCode);

        Integer num = ccidList.size() - channelCooperationList.size();

        return num;
    }*/

    @Override
    public List<ChannelClassNode> noSetList(String productCode) {
        //树结构
        ChannelProductCostPageParam channelProductCostPageParam = new ChannelProductCostPageParam();
        channelProductCostPageParam.setProductCode(productCode);
        List<ChannelCooperation> channelCooperationList = channelProductCostMapper.getCCIDCostExt(channelProductCostPageParam);
        //获取全部CCID
        List<ChannelClassNode> ccidList = channelProductCostMapper.getAllCCIDByProduct(productCode);

        List<String> ccidTreeList = channelCooperationList.stream().map(ChannelCooperation::getCcid).collect(Collectors.toList());
        ccidList = ccidList.stream().filter(i -> !ccidTreeList.contains(i.getId())).collect(Collectors.toList());
        ccidList.stream().forEach(i -> i.setChannelClassStr(i.getChannelClassStr().replace(",", "/")));

        return ccidList;
    }


    @Override
    public ChannelProductCostVO getChannelProductCostExt(ChannelProductCostPageParam channelProductCostPageParam) {
        ChannelProductCostVO channelProductCostVO = new ChannelProductCostVO();
        ChannelProductCost channelProductCost = channelProductCostMapper.selectOne(new LambdaQueryWrapper<ChannelProductCost>()
                .eq(ChannelProductCost::getProductCode, channelProductCostPageParam.getProductCode())
                .eq(ChannelProductCost::getId, channelProductCostPageParam.getCostId())
        );
        channelProductCostVO.setCostName(channelProductCost.getCostName());
        channelProductCostVO.setChannelShareType(channelProductCost.getChannelShareType());
        channelProductCostVO.setChannelShare(channelProductCost.getChannelShare());
        channelProductCostVO.setChannelShareStep(channelProductCost.getChannelShareStep());
        channelProductCostVO.setChannelRate(channelProductCost.getChannelRate());

        //关联CCID列表
        List<ChannelCooperation> channelCooperationList = channelProductCostMapper.getCCIDCostExt(channelProductCostPageParam);
        channelCooperationList.stream().forEach(i -> i.setChannelClassStr(i.getChannelClassStr().replace(",", "/")));

        channelProductCostVO.setChannelCooperationList(channelCooperationList);

        return channelProductCostVO;
    }

    @Override
    public ChannelClassNode getTreeStructure(String productCode) {
        //获取树
        ChannelClassNode root = getTree(productCode);
        //删除空节点
        deleteEmpty(root);

        return root;
    }

    /**
     * 删除空节点
     */
    public Boolean deleteEmpty(ChannelClassNode root) {
        List<ChannelClassNode> channelClassNodeList = root.getChannelClassNodeList();
        if (CollectionUtil.isNotEmpty(channelClassNodeList)) {
            Iterator<ChannelClassNode> iterator = channelClassNodeList.iterator();
            while (iterator.hasNext()) {
                ChannelClassNode channelClassNode = iterator.next();

                Boolean flag = deleteEmpty(channelClassNode);
                if (!flag) {
                    iterator.remove();
                }
            }
        }

        if (CollectionUtil.isEmpty(root.getChannelClassNodeList()) && !Arrays.asList("3", "4").contains(root.getModeType())) {
            return false;
        }

        return true;
    }

    /**
     * 获取根节点所有CCID
     */
    public List<String> getRootCCID(ChannelClassNode root) {
        List<String> ccidAll = new ArrayList<String>();

        if (CollectionUtil.isNotEmpty(root.getChannelClassNodeList())) {
            List<ChannelClassNode> channelClassNodeList = root.getChannelClassNodeList();
            Iterator iterator = channelClassNodeList.iterator();
            while (iterator.hasNext()) {
                ChannelClassNode channelClassNode = (ChannelClassNode) iterator.next();
                List<String> list = getRootCCID(channelClassNode);
                ccidAll.addAll(list);
            }
            return ccidAll;
        }
        if (CollectionUtil.isNotEmpty(root.getCcidList())) {
            //将ccidList 移到channelClassNodeList，便于前端递归
            List<ChannelClassNode> channelClassNodeList = root.getCcidList();

            List<ChannelClassNode> channelClassNodeListOld = CollectionUtil.isNotEmpty(root.getChannelClassNodeList()) ? root.getChannelClassNodeList() : new ArrayList<ChannelClassNode>();
            channelClassNodeListOld.addAll(channelClassNodeList);
            root.setChannelClassNodeList(channelClassNodeListOld);

            List<String> ccidList = channelClassNodeList.stream().map(i -> i.getId()).collect(Collectors.toList());

            root.setCcidList(null);
            return ccidList;
        }
        return new ArrayList<>();
    }

    private LambdaQueryWrapper<ChannelProductCost> getLambdaQueryWrapper(ChannelProductCostPageParam channelProductCostPageParam) {
        LambdaQueryWrapper<ChannelProductCost> wrapper = new LambdaQueryWrapper<>();
        return wrapper;
    }

}
