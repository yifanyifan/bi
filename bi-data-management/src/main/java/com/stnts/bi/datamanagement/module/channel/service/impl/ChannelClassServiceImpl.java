package com.stnts.bi.datamanagement.module.channel.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stnts.bi.common.BiSessionUtil;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.business.service.BusinessDictService;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelClass;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelClassCooperation;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelCooperation;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelClassCooperationMapper;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelClassMapper;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelCooperationMapper;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelPromotionMapper;
import com.stnts.bi.datamanagement.module.channel.param.ChannelClassPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelClassCooperationService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelClassService;
import com.stnts.bi.datamanagement.module.channel.vo.ChannelClassCooperationVO;
import com.stnts.bi.datamanagement.module.channel.vo.ChannelClassNode;
import com.stnts.bi.datamanagement.module.channel.vo.ChannelClassVO;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationBiService;
import com.stnts.bi.datamanagement.util.DozerUtil;
import com.stnts.bi.entity.common.PageEntity;
import com.stnts.bi.entity.sys.UserDmEntity;
import com.stnts.bi.entity.sys.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ???????????? ???????????????
 *
 * @author ??????
 * @since 2021-09-22
 */
@Slf4j
@Service
public class ChannelClassServiceImpl extends ServiceImpl<ChannelClassMapper, ChannelClass> implements ChannelClassService {
    @Autowired
    private ChannelClassMapper channelClassMapper;
    @Autowired
    private ChannelClassCooperationMapper channelClassCooperationMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private BusinessDictService businessDictService;
    @Autowired
    private ChannelCooperationMapper channelCooperationMapper;
    @Autowired
    private ChannelClassCooperationService channelClassCooperationService;
    @Autowired
    private ChannelPromotionMapper channelPromotionMapper;
    @Autowired
    private CooperationBiService cooperationBiService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveChannelClass(ChannelClass channelClass, HttpServletRequest request) throws Exception {
        // ?????? By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        channelClass.setUserid(Long.valueOf(user.getId()));
        channelClass.setUsername(user.getCnname());

        //????????????
        saveAndUpdateValid(channelClass);
        //???????????????
        departmentCl(channelClass, request);

        return super.save(channelClass);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateChannelClass(ChannelClass channelClass, HttpServletRequest request) throws Exception {
        //????????????
        saveAndUpdateValid(channelClass);
        //???????????????
        departmentCl(channelClass, request);

        super.updateById(channelClass);

        //List<ChannelClassCooperation> channelClassCooperationList = channelClassCooperationMapper.selectList(new LambdaQueryWrapper<ChannelClassCooperation>().eq(ChannelClassCooperation::getChannelClassId, channelClass.getId()));
        List<ChannelClassCooperation> channelClassCooperationList = channelClassCooperationMapper.selectList(new LambdaQueryWrapper<ChannelClassCooperation>()
                .like(ChannelClassCooperation::getChannelClassIdPath, channelClass.getId()));
        ChannelClass channelClassParent = channelClassMapper.selectById(channelClass.getParentId());
        if (CollectionUtil.isNotEmpty(channelClassCooperationList)) {
            ChannelClassVO result = getDepartmentAndPath(channelClass);

            for (ChannelClassCooperation channelClassCooperation : channelClassCooperationList) {
                channelClassCooperation.setDepartmentCode(result.getDepartmentCode());
                channelClassCooperation.setDepartmentName(result.getDepartmentName());
                List<String> channelClassIdPathList = new ArrayList<String>(Arrays.asList(channelClassCooperation.getChannelClassIdPath().split(",")));
                List<String> channelClassPathList = new ArrayList<String>(Arrays.asList(channelClassCooperation.getChannelClassPath().split(",")));

                Iterator<String> iterator = channelClassIdPathList.iterator();
                Iterator<String> iteratorPath = channelClassPathList.iterator();
                while (iterator.hasNext()) {
                    String idSub = iterator.next();
                    String pathSub = iteratorPath.next();
                    if (!String.valueOf(channelClass.getId()).equals(idSub)) {
                        iterator.remove();
                        iteratorPath.remove();
                    } else {
                        iterator.remove();
                        iteratorPath.remove();
                        break;
                    }
                }

                String classIdPathNew = result.getChannelClassIdPath() + (CollectionUtil.isNotEmpty(channelClassIdPathList) ? "," + String.join(",", channelClassIdPathList) : "");
                String classNamePathNew = result.getChannelClassPath() + (CollectionUtil.isNotEmpty(channelClassPathList) ? "," + String.join(",", channelClassPathList) : "");
                channelClassCooperation.setChannelClassIdPath(classIdPathNew);
                channelClassCooperation.setChannelClassPath(classNamePathNew);
            }

            channelClassCooperationService.saveOrUpdateBatch(channelClassCooperationList);
        }

        return true;
    }

    public static void main(String[] args) {
        String str = ",sd,we,df";
        System.out.println(str.substring(str.indexOf(",we,")));
    }

    public void saveAndUpdateValid(ChannelClass channelClass) {
        if (ObjectUtil.isEmpty(channelClass.getParentId())) {
            throw new BusinessException("parentId??????");
        }
        if (StringUtils.isBlank(channelClass.getCode())) {
            throw new BusinessException("code??????");
        }

        Integer count = channelClassMapper.selectCount(new LambdaQueryWrapper<ChannelClass>()
                .eq(ObjectUtil.isNotEmpty(channelClass.getParentId()), ChannelClass::getParentId, channelClass.getParentId())
                .eq(StringUtils.isNotBlank(channelClass.getCode()), ChannelClass::getCode, channelClass.getCode())
                .eq(StringUtils.isNotBlank(channelClass.getName()), ChannelClass::getName, channelClass.getName())
        );
        /*if (ObjectUtil.isNotEmpty(count) && count > 0) {
            throw new BusinessException("????????????????????????????????????");
        }*/
    }

    public void departmentCl(ChannelClass channelClass, HttpServletRequest request) {
        //????????????????????????
        ChannelClass channelClassParent = channelClassMapper.selectById(channelClass.getParentId());
        String newIdPath = (StringUtils.isNotBlank(channelClassParent.getNodeIdPath()) ? (channelClassParent.getNodeIdPath() + ",") : "") + channelClassParent.getId();
        String newNamePath = (StringUtils.isNotBlank(channelClassParent.getNodePath()) ? (channelClassParent.getNodePath() + ",") : "") + channelClassParent.getName();

        //???????????????????????????????????????????????????
        if (ObjectUtil.isNotEmpty(channelClass.getId())) {
            ChannelClass channelClassDB = channelClassMapper.selectById(channelClass.getId());
            if (ObjectUtil.isEmpty(channelClassDB)) {
                throw new BusinessException("????????????ID??????");
            }
            if (!channelClassDB.getCode().equals(channelClass.getCode())) {
                throw new BusinessException("??????????????????????????????");
            }

            String nowIdPath = (StringUtils.isNotBlank(channelClassDB.getNodeIdPath()) ? (channelClassDB.getNodeIdPath() + ",") : "") + channelClassDB.getId();
            String nowNamePath = (StringUtils.isNotBlank(channelClassDB.getNodePath()) ? (channelClassDB.getNodePath() + ",") : "") + channelClassDB.getName();

            // ?????????????????????????????????????????????nowIdPath??????????????????newIdPath
            channelClassMapper.updateSubPath(nowIdPath, newIdPath + "," + channelClassDB.getId(), nowNamePath, newNamePath + "," + channelClassDB.getName());
        }

        channelClass.setNodeIdPath(newIdPath);
        channelClass.setNodePath(newNamePath);

        List<ChannelClass> channelClassList = channelClassMapper.getUpLoad(channelClass.getParentId());
        String idPath = channelClassList.stream().map(i -> String.valueOf(i.getId())).collect(Collectors.joining(","));
        String path = channelClassList.stream().map(ChannelClass::getName).collect(Collectors.joining(","));
        channelClass.setNodeIdPath(idPath);
        channelClass.setNodePath(path);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteChannelClass(Long id) throws Exception {
        Long ccidNum = channelClassMapper.countByCCID(id);
        if (ccidNum != null && ccidNum > 0) {
            throw new BusinessException("?????????CCID???????????????");
        }

        //???????????????????????????????????????
        List<ChannelClass> channelClassList = channelClassMapper.getDownLoad(id);
        List<Long> channelClassIdList = channelClassList.stream().map(ChannelClass::getId).collect(Collectors.toList());

        if (CollectionUtil.isNotEmpty(channelClassIdList)) {
            this.removeByIds(channelClassIdList);

            // ??????????????????
            List<ChannelClassCooperation> channelClassCooperationList = channelClassCooperationMapper.selectByChannelClassId(channelClassIdList);
            if (CollectionUtil.isNotEmpty(channelClassCooperationList)) {
                channelClassCooperationMapper.deleteBatchIds(channelClassCooperationList);
            }
        }

        return true;
    }


    @Override
    public PageEntity<ChannelClassVO> getChannelClassPageList(ChannelClassPageParam channelClassPageParam, HttpServletRequest request) throws Exception {
        // ?????? By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        channelClassPageParam.setDepartmentCodeAllList(departmentCodeAllList);
        Map<Integer, List<UserDmEntity>> mapAll = businessDictService.getCCIDAndPidByPermissions(user);
        channelClassPageParam.setMapAll(mapAll);

        //???????????????????????????ChannelClassId
        List<Long> channelClassIdNumList = new ArrayList<Long>();
        if (StringUtils.isNotBlank(channelClassPageParam.getChannelClassIdNum())) {
            List<ChannelClass> channelClassList = channelClassMapper.getDownLoad(Long.valueOf(channelClassPageParam.getChannelClassIdNum()));
            channelClassIdNumList = channelClassList.stream().map(ChannelClass::getId).collect(Collectors.toList());
        }

        // ???????????????????????????????????????
        List<ChannelClassCooperation> channelClassCooperationList = channelClassCooperationMapper.selectList(new LambdaQueryWrapper<ChannelClassCooperation>()
                .in(CollectionUtil.isNotEmpty(departmentCodeAllList), ChannelClassCooperation::getDepartmentCode, departmentCodeAllList)
                .in(CollectionUtil.isNotEmpty(channelClassIdNumList), ChannelClassCooperation::getChannelClassId, channelClassIdNumList)
                .eq(StringUtils.isNotBlank(channelClassPageParam.getChannelClassId()), ChannelClassCooperation::getChannelClassId, channelClassPageParam.getChannelClassId())
                .eq(StringUtils.isNotBlank(channelClassPageParam.getDepartmentCode()), ChannelClassCooperation::getDepartmentCode, channelClassPageParam.getDepartmentCode())
        );
        channelClassPageParam.setSelectByChargeRule(channelClassCooperationList.stream().filter(i -> "1".equals(i.getModeType())).collect(Collectors.toList()));
        channelClassPageParam.setSelectByChannel(channelClassCooperationList.stream().filter(i -> "2".equals(i.getModeType())).collect(Collectors.toList()));
        channelClassPageParam.setSelectByCCID(channelClassCooperationList.stream().filter(i -> "3".equals(i.getModeType())).collect(Collectors.toList()));

        // ??????CCID??????
        Page<ChannelClass> page = new Page<>(channelClassPageParam.getPageIndex(), channelClassPageParam.getPageSize());
        List<OrderItem> orderItemList = channelClassPageParam.getOrders();
        if (CollectionUtil.isNotEmpty(orderItemList)) {
            orderColumn(orderItemList);
            page.setOrders(orderItemList);
        }
        IPage<ChannelClassVO> iPage = channelClassMapper.selectAll(page, channelClassPageParam, user);

        iPage.getRecords().stream().forEach(i -> i.setChannelClassPath(i.getChannelClassPath().replace(",", "/")));

        return new PageEntity<ChannelClassVO>(iPage);
    }

    public void orderColumn(List<OrderItem> orderItemList) {
        if (CollectionUtil.isNotEmpty(orderItemList)) {
            List<String> errorOrder = new ArrayList<String>();
            List<OrderItem> productNameAndApplicationNameAdd = new ArrayList<OrderItem>();

            Iterator<OrderItem> iterator = orderItemList.iterator();
            while (iterator.hasNext()) {
                OrderItem orderItem = iterator.next();
                if ("channelClassPath".equals(orderItem.getColumn())) {
                    orderItem.setColumn("channelClassPath");
                    continue;
                } else if ("departmentName".equals(orderItem.getColumn())) {
                    orderItem.setColumn("co.department_name");
                    continue;
                } else if ("ccid".equals(orderItem.getColumn())) {
                    orderItem.setColumn("co.ccid");
                    continue;
                } else if ("channelName".equals(orderItem.getColumn())) {
                    orderItem.setColumn("co.channel_name");
                    continue;
                } else if ("chargeRule".equals(orderItem.getColumn())) {
                    orderItem.setColumn("co.charge_rule");
                    continue;
                } else if ("pidNum".equals(orderItem.getColumn())) {
                    orderItem.setColumn("pidNum");
                    continue;
                } else if ("username".equals(orderItem.getColumn()) || "usernameSimple".equals(orderItem.getColumn())) {
                    orderItem.setColumn("cl.username");
                    continue;
                } else if ("createTime".equals(orderItem.getColumn())) {
                    orderItem.setColumn("cl.create_time");
                    continue;
                }
                errorOrder.add(orderItem.getColumn());
            }

            if (CollectionUtil.isNotEmpty(errorOrder)) {
                throw new BusinessException("?????????????????????" + org.apache.commons.lang.StringUtils.join(errorOrder, ","));
            }

            orderItemList.addAll(productNameAndApplicationNameAdd);
        }
    }

    /**
     * @param channelClassNodeMap   ???????????????????????????????????????????????????????????????????????????????????????ID????????????
     * @param channelClassLeftCount ??????????????????????????????????????????????????????????????????CCID??????
     * @param nodeId                -1
     * @return
     */
    public List<ChannelClassNode> getDG(Map<String, List<ChannelClassNode>> channelClassNodeMap, Map<String, Long> channelClassLeftCount, String nodeId) {
        if (channelClassNodeMap.containsKey(nodeId)) {
            List<ChannelClassNode> channelClassNodesSub = channelClassNodeMap.get(nodeId);

            for (ChannelClassNode channelClassNode : channelClassNodesSub) {
                List<ChannelClassNode> listSub = getDG(channelClassNodeMap, channelClassLeftCount, channelClassNode.getId());

                if (CollectionUtil.isNotEmpty(listSub)) {
                    channelClassNode.setChannelClassNodeList(listSub);
                    channelClassNode.setAssociatedCCIDNum(listSub.stream().mapToLong(ChannelClassNode::getAssociatedCCIDNum).sum());
                } else {
                    channelClassNode.setAssociatedCCIDNum(channelClassLeftCount.containsKey(channelClassNode.getId()) ? channelClassLeftCount.get(channelClassNode.getId()) : 0l);
                }
            }
            return channelClassNodesSub;
        }
        return new ArrayList<ChannelClassNode>();
    }

    @Override
    public List<ChannelClassNode> getChannelClassList(ChannelClassPageParam channelClassPageParam, HttpServletRequest request) throws Exception {
        // ?????? By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        channelClassPageParam.setDepartmentCodeAllList(departmentCodeAllList);

        //??????1
        //1. ???????????????????????????????????????????????????????????????????????????????????????ID????????????
        List<ChannelClassNode> channelClassList = channelClassMapper.selectAllList(channelClassPageParam);
        Map<String, List<ChannelClassNode>> channelClassMap = channelClassList.stream().collect(Collectors.groupingBy(ChannelClassNode::getParentId));
        //2. ??????????????????????????????????????????????????????????????????CCID??????
        List<ChannelClassNode> channelClassLeftAll = channelClassMapper.selectCountCCIDByLeaf(channelClassPageParam);
        Map<String, Long> channelClassLeftCount = channelClassLeftAll.stream().collect(Collectors.toMap(item -> item.getChannelClassId(), item -> item.getAssociatedCCIDNum()));
        //3. ??????????????????
        List<ChannelClassNode> channelClassNodeList = getDG(channelClassMap, channelClassLeftCount, "-1");
        return channelClassNodeList;
    }

    @Override
    public ChannelClassVO getChannelClass(Long id) {
        ChannelClassVO channelClassVO = new ChannelClassVO();
        channelClassVO.setId(id);
        ChannelClass channelClass = channelClassMapper.selectById(id);

        ChannelClassVO result = this.getDepartmentAndPath(channelClass);

        //1. ????????????????????????
        channelClassVO.setDepartmentCode(result.getDepartmentCode());
        channelClassVO.setDepartmentName(result.getDepartmentName());
        channelClassVO.setRemark(channelClass.getRemark());

        channelClassVO.setChannelClassPath(result.getChannelClassPath().replace(",", "/"));

        //2. ????????????????????????CCID????????????+???????????????
        clSelect(channelClassVO);
        //3. ??????CCID??????
        List<ChannelCooperation> ccidAllList = channelCooperationMapper.getAssociated(result.getDepartmentCode());
        //4. ??????tree
        clTree(ccidAllList, channelClassVO);
        //6. ???????????? ???????????????????????????????????????????????????????????????CCID???????????????????????????
        DGFindSub(channelClassVO);
        //5. ????????????ID???CCID?????????????????????????????????CCID??????????????????????????????
        DGFindFather(channelClassVO);

        //0 ?????? 999 2021-12-09 ??????
        channelClassVO.setSelectByChargeRule(CollectionUtil.isNotEmpty(channelClassVO.getSelectByChargeRule()) ? channelClassVO.getSelectByChargeRule().stream().filter(i -> !i.contains("999")).distinct().collect(Collectors.toList()) : null);
        channelClassVO.setSelectByChannel(CollectionUtil.isNotEmpty(channelClassVO.getSelectByChannel()) ? channelClassVO.getSelectByChannel().stream().filter(i -> !i.contains("999")).distinct().collect(Collectors.toList()) : null);
        channelClassVO.setSelectByCCID(CollectionUtil.isNotEmpty(channelClassVO.getSelectByCCID()) ? channelClassVO.getSelectByCCID().stream().filter(i -> !i.contains("999")).distinct().collect(Collectors.toList()) : null);
        channelClassVO.setSelectByChargeRuleOther(CollectionUtil.isNotEmpty(channelClassVO.getSelectByChargeRuleOther()) ? channelClassVO.getSelectByChargeRuleOther().stream().filter(i -> !i.contains("999")).distinct().collect(Collectors.toList()) : null);
        channelClassVO.setSelectByChannelOther(CollectionUtil.isNotEmpty(channelClassVO.getSelectByChannelOther()) ? channelClassVO.getSelectByChannelOther().stream().filter(i -> !i.contains("999")).distinct().collect(Collectors.toList()) : null);
        channelClassVO.setSelectByCCIDOther(CollectionUtil.isNotEmpty(channelClassVO.getSelectByCCIDOther()) ? channelClassVO.getSelectByCCIDOther().stream().filter(i -> !i.contains("999")).distinct().collect(Collectors.toList()) : null);
        channelClassVO.setSelectByCCIDOtherChargeRuleParent(CollectionUtil.isNotEmpty(channelClassVO.getSelectByCCIDOtherChargeRuleParent()) ? channelClassVO.getSelectByCCIDOtherChargeRuleParent().stream().filter(i -> !i.contains("999")).distinct().collect(Collectors.toList()) : null);
        channelClassVO.setSelectByCCIDOtherChannelParent(CollectionUtil.isNotEmpty(channelClassVO.getSelectByCCIDOtherChannelParent()) ? channelClassVO.getSelectByCCIDOtherChannelParent().stream().filter(i -> !i.contains("999")).distinct().collect(Collectors.toList()) : null);

        return channelClassVO;
    }

    /**
     * ???????????? ???????????????????????????????????????????????????????????????CCID???????????????????????????
     */
    private void DGFindSub(ChannelClassVO channelClassVO) {
        List<ChannelClassNode> treeByChannelList = channelClassVO.getTreeByChannel();
        List<ChannelClassNode> treeByChargeRuleList = channelClassVO.getTreeByChargeRule();

        List<String> selectByChannel = channelClassVO.getSelectByChannel();
        List<String> selectByChargeRule = channelClassVO.getSelectByChargeRule();
        List<String> selectByCCID = channelClassVO.getSelectByCCID();

        List<String> selectByChannelOther = channelClassVO.getSelectByChannelOther();
        List<String> selectByChargeRuleOther = channelClassVO.getSelectByChargeRuleOther();
        List<String> selectByCCIDOther = channelClassVO.getSelectByCCIDOther();


        List<ChannelClassNode> l1 = DGSub(treeByChannelList, selectByChannel);
        List<ChannelClassNode> l2 = DGSub(treeByChargeRuleList, selectByChargeRule);
        if (CollectionUtil.isNotEmpty(l1)) {
            selectByCCID.addAll(l1.stream().map(ChannelClassNode::getId).collect(Collectors.toList()));
        }
        if (CollectionUtil.isNotEmpty(l2)) {
            selectByCCID.addAll(l2.stream().map(ChannelClassNode::getId).collect(Collectors.toList()));
        }

        List<ChannelClassNode> l3 = DGSub(treeByChannelList, selectByChannelOther);
        List<ChannelClassNode> l4 = DGSub(treeByChargeRuleList, selectByChargeRuleOther);
        if (CollectionUtil.isNotEmpty(l3)) {
            selectByCCIDOther.addAll(l3.stream().map(ChannelClassNode::getId).collect(Collectors.toList()));
        }
        if (CollectionUtil.isNotEmpty(l4)) {
            selectByCCIDOther.addAll(l4.stream().map(ChannelClassNode::getId).collect(Collectors.toList()));
        }
    }

    /**
     * ??????????????????CCID??????
     */
    public List<ChannelClassNode> DGSub(List<ChannelClassNode> channelClassNodeList, List<String> typeList) {
        List<ChannelClassNode> all = new ArrayList<ChannelClassNode>();
        for (ChannelClassNode channelClassNode : channelClassNodeList) {
            List<ChannelClassNode> channelClassNodeListSub = channelClassNode.getChannelClassNodeList();

            if (typeList.contains(channelClassNode.getId())) {
                all.addAll(channelClassNodeListSub);
            } else if (CollectionUtil.isNotEmpty(channelClassNodeListSub)) {
                List<ChannelClassNode> result = DGSub(channelClassNodeListSub, typeList);
                all.addAll(result);
            }
        }

        return all;
    }


    /**
     * CCID?????????????????????????????????CCID???????????????????????????
     */
    private void DGFindFather(ChannelClassVO channelClassVO) {
        List<ChannelClassNode> treeByChargeRule = channelClassVO.getTreeByChargeRule();
        List<ChannelClassNode> treeByChannel = channelClassVO.getTreeByChannel();
        List<String> selectByCCIDOther = channelClassVO.getSelectByCCIDOther();

        List<String> selectByCCIDOtherChargeRuleParent = new ArrayList<>();
        List<String> selectByCCIDOtherChannelParent = new ArrayList<>();

        for (ChannelClassNode channelClassNode : treeByChargeRule) {
            List<ChannelClassNode> channelClassNodeList = channelClassNode.getChannelClassNodeList();
            Long count = channelClassNodeList.stream().filter(i -> selectByCCIDOther.contains(i.getId())).count();
            if (count > 0) {
                selectByCCIDOtherChargeRuleParent.add(channelClassNode.getId());
            }
        }
        for (ChannelClassNode channelClassNode : treeByChannel) {
            List<ChannelClassNode> channelClassNodeList = channelClassNode.getChannelClassNodeList();
            Long count = channelClassNodeList.stream().filter(i -> selectByCCIDOther.contains(i.getId())).count();
            if (count > 0) {
                selectByCCIDOtherChannelParent.add(channelClassNode.getId());
            }
        }
        channelClassVO.setSelectByCCIDOtherChargeRuleParent(selectByCCIDOtherChargeRuleParent);
        channelClassVO.setSelectByCCIDOtherChannelParent(selectByCCIDOtherChannelParent);
    }

    public ChannelClassVO getDepartmentAndPath(ChannelClass channelClass) {
        ChannelClassVO channelClassVO = new ChannelClassVO();

        List<String> nodeIdPathList = new ArrayList<String>(Arrays.asList(channelClass.getNodeIdPath().split(",")));
        List<String> nodePathList = new ArrayList<String>(Arrays.asList(channelClass.getNodePath().split(",")));

        String departmentCode = channelClass.getCode();
        String departmentName = nodePathList.get(0);
        nodeIdPathList = nodeIdPathList.subList(1, nodeIdPathList.size());
        nodeIdPathList.add(String.valueOf(channelClass.getId()));
        nodePathList = nodePathList.subList(1, nodePathList.size());
        nodePathList.add(channelClass.getName());

        channelClassVO.setDepartmentCode(departmentCode);
        channelClassVO.setDepartmentName(departmentName);
        channelClassVO.setChannelClassIdPath(StringUtils.join(nodeIdPathList, ","));
        channelClassVO.setChannelClassPath(StringUtils.join(nodePathList, ","));

        return channelClassVO;
    }

    @Override
    public boolean updateAssociated(ChannelClassVO channelClassVO) {
        ChannelClass channelClass = channelClassMapper.selectById(channelClassVO.getId());
        channelClass.setRemark(channelClassVO.getRemark());
        channelClassMapper.updateById(channelClass);

        ChannelClassVO result = getDepartmentAndPath(channelClass);

        /*List<ChannelClass> channelClassList = channelClassMapper.getUpLoad(channelClassVO.getId());
        ChannelClass channelClassDepart = channelClassList.stream().filter(i -> -1 == i.getParentId()).findFirst().get();
        String channelClassIdPath = channelClassList.stream().filter(i -> -1 != i.getParentId()).map(i -> String.valueOf(i.getId())).collect(Collectors.joining(","));
        String channelClassPath = channelClassList.stream().filter(i -> -1 != i.getParentId()).map(ChannelClass::getName).collect(Collectors.joining(","));*/

        channelClassVO.setDepartmentCode(result.getDepartmentCode());
        channelClassVO.setDepartmentName(result.getDepartmentName());
        channelClassVO.setChannelClassIdPath(result.getChannelClassIdPath());
        channelClassVO.setChannelClassPath(result.getChannelClassPath());

        List<ChannelClassNode> selectAll = channelClassVO.getSelectOK();
        //???????????????????????????
        List<String> selectByChargeRule = selectAll.stream().filter(i -> "1".equals(i.getModeType())).map(i -> i.getId()).collect(Collectors.toList());
        //?????????????????????
        List<String> selectByChannel = selectAll.stream().filter(i -> "2".equals(i.getModeType())).map(i -> i.getId()).collect(Collectors.toList());
        //???????????????CCID
        List<String> selectByCCID = selectAll.stream().filter(i -> "3".equals(i.getModeType())).map(i -> i.getId()).collect(Collectors.toList());

        // ????????????
        List<ChannelClassCooperation> channelClassCooperationList = channelClassCooperationMapper.selectList(new LambdaQueryWrapper<ChannelClassCooperation>().eq(ChannelClassCooperation::getChannelClassId, channelClassVO.getId()));
        List<String> dbByChargeRule = channelClassCooperationList.stream().filter(i -> "1".equals(i.getModeType())).map(ChannelClassCooperation::getModeId).collect(Collectors.toList());
        List<String> dbByChannel = channelClassCooperationList.stream().filter(i -> "2".equals(i.getModeType())).map(ChannelClassCooperation::getModeId).collect(Collectors.toList());
        List<String> dbByCCID = channelClassCooperationList.stream().filter(i -> "3".equals(i.getModeType())).map(ChannelClassCooperation::getModeId).collect(Collectors.toList());

        //???????????????CCID ??? ????????????+?????????????????? ?????? ????????????????????????????????????????????????CCID??????????????????????????????CCID????????????????????????
        if (CollectionUtil.isNotEmpty(selectByCCID)) {
            List<ChannelCooperation> channelCooperationList = channelCooperationMapper.selectList(new LambdaQueryWrapper<ChannelCooperation>().in(ChannelCooperation::getCcid, selectByCCID));
            selectByCCID = channelCooperationList.stream().filter(i -> !selectByChannel.contains(String.valueOf(i.getChannelId())) && !selectByChargeRule.contains(i.getChargeRule())).map(ChannelCooperation::getCcid).collect(Collectors.toList());
        }

        //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        checkTree(channelClass, selectByChargeRule, selectByChannel, selectByCCID);

        // ??????????????????
        toChannelClassCooperation(channelClassVO, dbByChargeRule, selectByChargeRule, "1");
        //??????????????????
        toChannelClassCooperation(channelClassVO, dbByChannel, selectByChannel, "2");
        //??????CCID??????
        toChannelClassCooperation(channelClassVO, dbByCCID, selectByCCID, "3");

        return true;
    }

    /**
     * ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param channelClass
     * @param selectByChargeRule
     * @param selectByChannel
     * @param selectByCCID
     */
    public void checkTree(ChannelClass channelClass, List<String> selectByChargeRule, List<String> selectByChannel, List<String> selectByCCID) {
        List<String> errorList = new ArrayList<String>();

        // ????????????
        if (CollectionUtil.isNotEmpty(selectByChargeRule)) {
            ChannelClassCooperationVO channelClassCooperationVO = new ChannelClassCooperationVO();
            channelClassCooperationVO.setDepartmentCode(channelClass.getCode());
            channelClassCooperationVO.setChannelClassId(channelClass.getId());
            channelClassCooperationVO.setSelectByChargeRule(selectByChargeRule);

            List<ChannelClassCooperationVO> channelClassCooperationVOList = channelClassCooperationMapper.getRepeatSelect(channelClassCooperationVO);

            if (CollectionUtil.isNotEmpty(channelClassCooperationVOList)) {
                Map<String, List<ChannelClassCooperationVO>> channelClassCooperationVOMap = channelClassCooperationVOList.stream().collect(Collectors.groupingBy(i -> i.getNodePath() + "_" + i.getModeId()));

                for (Map.Entry<String, List<ChannelClassCooperationVO>> entry : channelClassCooperationVOMap.entrySet()) {
                    String mapKey = entry.getKey();
                    List<ChannelClassCooperationVO> mapValue = entry.getValue();
                    Map<String, List<String>> ccidMap = mapValue.stream().collect(Collectors.groupingBy(i -> i.getChargeRule(), Collectors.mapping(ChannelClassCooperationVO::getCcid, Collectors.toList())));
                    for (Map.Entry<String, List<String>> entrySub : ccidMap.entrySet()) {
                        errorList.add(entrySub.getKey() + "??????CCID??????" + mapKey + "???????????????");
                        // errorList.add(entrySub.getKey() + "(" + StringUtils.join(entrySub.getValue(), ",") + ")???" + mapKey + "????????????");
                    }
                }
            }
        }
        // ??????ID
        if (CollectionUtil.isNotEmpty(selectByChannel)) {
            ChannelClassCooperationVO channelClassCooperationVO = new ChannelClassCooperationVO();
            channelClassCooperationVO.setDepartmentCode(channelClass.getCode());
            channelClassCooperationVO.setChannelClassId(channelClass.getId());
            channelClassCooperationVO.setSelectByChannel(selectByChannel);

            List<ChannelClassCooperationVO> channelClassCooperationVOList = channelClassCooperationMapper.getRepeatSelect(channelClassCooperationVO);
            if (CollectionUtil.isNotEmpty(channelClassCooperationVOList)) {
                Map<String, List<ChannelClassCooperationVO>> channelClassCooperationVOMap = channelClassCooperationVOList.stream().collect(Collectors.groupingBy(i -> i.getNodePath() + "_" + i.getModeId()));

                for (Map.Entry<String, List<ChannelClassCooperationVO>> entry : channelClassCooperationVOMap.entrySet()) {
                    String mapKey = entry.getKey();
                    List<ChannelClassCooperationVO> mapValue = entry.getValue();
                    Map<String, List<String>> ccidMap = mapValue.stream().collect(Collectors.groupingBy(i -> i.getChannelName(), Collectors.mapping(ChannelClassCooperationVO::getCcid, Collectors.toList())));
                    for (Map.Entry<String, List<String>> entrySub : ccidMap.entrySet()) {
                        errorList.add(entrySub.getKey() + "??????CCID??????" + mapKey + "???????????????");
                        // errorList.add(entrySub.getKey() + "(" + StringUtils.join(entrySub.getValue(), ",") + ")???" + mapKey + "????????????");
                    }
                }
            }
        }
        // CCID
        if (CollectionUtil.isNotEmpty(selectByCCID)) {
            ChannelClassCooperationVO channelClassCooperationVO = new ChannelClassCooperationVO();
            channelClassCooperationVO.setDepartmentCode(channelClass.getCode());
            channelClassCooperationVO.setChannelClassId(channelClass.getId());
            channelClassCooperationVO.setSelectByCCID(selectByCCID);

            List<ChannelClassCooperationVO> channelClassCooperationVOList = channelClassCooperationMapper.getRepeatSelect(channelClassCooperationVO);
            if (CollectionUtil.isNotEmpty(channelClassCooperationVOList)) {
                Map<String, List<ChannelClassCooperationVO>> channelClassCooperationVOMap = channelClassCooperationVOList.stream().collect(Collectors.groupingBy(i -> i.getNodePath() + "_" + i.getModeId()));

                for (Map.Entry<String, List<ChannelClassCooperationVO>> entry : channelClassCooperationVOMap.entrySet()) {
                    String mapKey = entry.getKey();
                    List<ChannelClassCooperationVO> mapValue = entry.getValue();
                    List<String> ccidNoList = mapValue.stream().map(ChannelClassCooperationVO::getCcid).collect(Collectors.toList());
                    errorList.add(StringUtils.join(ccidNoList, ",") + "??????CCID??????" + mapKey + "???????????????");
                }
            }
        }
        if (CollectionUtil.isNotEmpty(errorList)) {
            throw new BusinessException("?????????" + StringUtils.join(errorList, ","));
        }
    }

    @Override
    public Map<String, Object> searchAll(ChannelClassPageParam param, HttpServletRequest request) {
        // ?????? By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        param.setDepartmentCodeAllList(departmentCodeAllList);

        List<ChannelClassCooperation> channelClassCooperationList = channelClassCooperationMapper.searchAll(param);

        //??????
        Set<Map<String, String>> departmentList = new HashSet<Map<String, String>>();
        //??????
        Set<Map<String, String>> channelClassList = new HashSet<Map<String, String>>();

        for (ChannelClassCooperation channelClassCooperation : channelClassCooperationList) {
            if (StringUtils.isNotBlank(channelClassCooperation.getDepartmentCode())) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("code", channelClassCooperation.getDepartmentCode());
                map.put("name", channelClassCooperation.getDepartmentName());
                departmentList.add(map);
            }
            if (channelClassCooperation.getChannelClassPath() != null) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("channelClassId", String.valueOf(channelClassCooperation.getChannelClassId()));
                map.put("channelClassPath", channelClassCooperation.getChannelClassPath().replace(",", "/"));
                channelClassList.add(map);
            }
        }

        departmentList = departmentList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(i -> i.get("code")))), HashSet::new));
        channelClassList = channelClassList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(i -> i.get("channelClassId")))), HashSet::new));

        Map<String, Object> mapAlls = new HashMap<String, Object>();
        mapAlls.put("department", departmentList);
        mapAlls.put("channelClass", channelClassList);

        return mapAlls;
    }

    @Override
    public List<ChannelClass> getPullList(ChannelClassPageParam channelClassPageParam) {
        String departmentCode = channelClassPageParam.getDepartmentCode();
        if (StringUtils.isBlank(departmentCode)) {
            throw new BusinessException("departmentCode????????????????????????");
        }

        List<ChannelClass> channelClassList = channelClassMapper.getPullList(channelClassPageParam);

        return channelClassList;
    }

    public void toChannelClassCooperation(ChannelClassVO channelClassVO, List<String> dbList, List<String> selectList, String modeType) {
        //??????
        List<String> dbTmpList = new ArrayList<String>(dbList);
        if (CollectionUtil.isNotEmpty(dbTmpList)) {
            dbTmpList.removeAll(selectList);
            if (CollectionUtil.isNotEmpty(dbTmpList)) {
                channelClassCooperationMapper.delete(new LambdaQueryWrapper<ChannelClassCooperation>().eq(ChannelClassCooperation::getChannelClassId, channelClassVO.getId()).in(ChannelClassCooperation::getModeId, dbTmpList));
            }
        }
        //??????
        if (CollectionUtil.isNotEmpty(selectList)) {
            selectList.removeAll(dbList);
            if (CollectionUtil.isNotEmpty(selectList)) {
                List<ChannelClassCooperation> channelClassCooperationListTemp = new ArrayList<ChannelClassCooperation>();
                for (String sb : selectList) {
                    ChannelClassCooperation channelClassCooperation = new ChannelClassCooperation();
                    channelClassCooperation.setChannelClassId(channelClassVO.getId());
                    channelClassCooperation.setModeId(sb);
                    channelClassCooperation.setModeType(modeType);
                    channelClassCooperation.setDepartmentCode(channelClassVO.getDepartmentCode());
                    channelClassCooperation.setDepartmentName(channelClassVO.getDepartmentName());
                    channelClassCooperation.setChannelClassIdPath(channelClassVO.getChannelClassIdPath());
                    channelClassCooperation.setChannelClassPath(channelClassVO.getChannelClassPath());
                    channelClassCooperationListTemp.add(channelClassCooperation);
                }
                channelClassCooperationService.saveBatch(channelClassCooperationListTemp);
            }
        }
    }

    /**
     * ????????????????????????CCID????????????+???????????????
     */
    public void clSelect(ChannelClassVO channelClassVO) {
        List<ChannelClassCooperation> channelClassCooperationNodeList = channelClassCooperationMapper.selectList(new LambdaQueryWrapper<ChannelClassCooperation>()
                .eq(ChannelClassCooperation::getDepartmentCode, channelClassVO.getDepartmentCode())
                .eq(ChannelClassCooperation::getChannelClassId, channelClassVO.getId())
        );
        List<ChannelClassCooperation> channelClassCooperationOtherList = channelClassCooperationMapper.selectList(new LambdaQueryWrapper<ChannelClassCooperation>()
                .eq(ChannelClassCooperation::getDepartmentCode, channelClassVO.getDepartmentCode())
                .ne(ChannelClassCooperation::getChannelClassId, channelClassVO.getId())
        );

        // ???????????????(CPS/CPA/CPD???)
        List<String> selectByChargeRule = channelClassCooperationNodeList.stream().filter(i -> "1".equals(i.getModeType())).map(ChannelClassCooperation::getModeId).distinct().collect(Collectors.toList());
        // ?????????
        List<String> selectByChannel = channelClassCooperationNodeList.stream().filter(i -> "2".equals(i.getModeType())).map(ChannelClassCooperation::getModeId).distinct().collect(Collectors.toList());
        // ???CCID
        List<String> selectByCCID = channelClassCooperationNodeList.stream().filter(i -> "3".equals(i.getModeType())).map(ChannelClassCooperation::getModeId).distinct().collect(Collectors.toList());

        channelClassVO.setSelectByChargeRule(selectByChargeRule);
        channelClassVO.setSelectByChannel(selectByChannel);
        channelClassVO.setSelectByCCID(selectByCCID);

        // ???????????????(CPS/CPA/CPD???)
        List<String> selectByChargeRuleOther = channelClassCooperationOtherList.stream().filter(i -> "1".equals(i.getModeType())).map(ChannelClassCooperation::getModeId).distinct().collect(Collectors.toList());
        // ?????????
        List<String> selectByChannelOther = channelClassCooperationOtherList.stream().filter(i -> "2".equals(i.getModeType())).map(ChannelClassCooperation::getModeId).distinct().collect(Collectors.toList());
        // ???CCID
        List<String> selectByCCIDOther = channelClassCooperationOtherList.stream().filter(i -> "3".equals(i.getModeType())).map(ChannelClassCooperation::getModeId).distinct().collect(Collectors.toList());

        channelClassVO.setSelectByChargeRuleOther(selectByChargeRuleOther);
        channelClassVO.setSelectByChannelOther(selectByChannelOther);
        channelClassVO.setSelectByCCIDOther(selectByCCIDOther);
    }

    public void clTree(List<ChannelCooperation> ccidDBList, ChannelClassVO channelClassVO) {
        List<ChannelCooperation> channelCooperationListByChargeRule = DozerUtil.toBeanList(ccidDBList, ChannelCooperation.class);
        List<ChannelCooperation> channelCooperationListByChannel = DozerUtil.toBeanList(ccidDBList, ChannelCooperation.class);

        // ???????????????
        Map<String, List<ChannelCooperation>> treeByChargeRule = channelCooperationListByChargeRule.stream().distinct().collect(Collectors.groupingBy(ChannelCooperation::getChargeRule));
        List<ChannelClassNode> treeByChargeRuleList = new ArrayList<ChannelClassNode>();
        for (Map.Entry<String, List<ChannelCooperation>> entry : treeByChargeRule.entrySet()) {
            List<ChannelCooperation> value = entry.getValue();

            List<ChannelClassNode> mapResponseList = new ArrayList<ChannelClassNode>();
            for (ChannelCooperation channelCooperation : value) {
                ChannelClassNode channelClassNode = new ChannelClassNode();
                channelClassNode.setId(channelCooperation.getCcid());
                channelClassNode.setName(channelCooperation.getCcid() + "[" + channelCooperation.getChannelName() + "]");
                channelClassNode.setModeType("3");
                mapResponseList.add(channelClassNode);
            }
            //???999 - ?????????????????? 20211125
            ChannelClassNode channelClassNode = new ChannelClassNode();
            channelClassNode.setId("999-" + entry.getKey());
            channelClassNode.setName("999-" + entry.getKey());
            mapResponseList.add(channelClassNode);

            ChannelClassNode parentNode = new ChannelClassNode();
            parentNode.setId(entry.getKey());
            parentNode.setName(entry.getKey());
            parentNode.setChannelClassNodeList(mapResponseList);
            parentNode.setModeType("1");

            treeByChargeRuleList.add(parentNode);
        }

        // ?????????
        Map<Long, List<ChannelCooperation>> treeByChannel = channelCooperationListByChannel.stream().distinct().collect(Collectors.groupingBy(ChannelCooperation::getChannelId));
        List<ChannelClassNode> treeByChannelList = new ArrayList<ChannelClassNode>();
        for (Map.Entry<Long, List<ChannelCooperation>> entry : treeByChannel.entrySet()) {
            List<ChannelCooperation> value = entry.getValue();

            List<ChannelClassNode> mapResponseList = new ArrayList<ChannelClassNode>();
            for (ChannelCooperation channelCooperation : value) {
                ChannelClassNode channelClassNode = new ChannelClassNode();
                channelClassNode.setId(channelCooperation.getCcid());
                channelClassNode.setName(channelCooperation.getCcid());
                channelClassNode.setModeType("3");
                mapResponseList.add(channelClassNode);
            }
            //???999 - ?????????????????? 20211125
            ChannelClassNode channelClassNode = new ChannelClassNode();
            channelClassNode.setId("999-" + entry.getKey());
            channelClassNode.setName("999-" + entry.getKey());
            mapResponseList.add(channelClassNode);

            ChannelClassNode parentNode = new ChannelClassNode();
            parentNode.setId(String.valueOf(entry.getKey()));
            parentNode.setName(value.get(0).getChannelName());
            parentNode.setChannelClassNodeList(mapResponseList);
            parentNode.setModeType("2");

            treeByChannelList.add(parentNode);
        }

        channelClassVO.setTreeByChargeRule(treeByChargeRuleList);
        channelClassVO.setTreeByChannel(treeByChannelList);
    }

    /**
     * ??????ccid??????????????????
     *
     * @param ccidDBList
     * @param channelClassVO
     */
    /*public void clTree(List<ChannelCooperation> ccidDBList, ChannelClassVO channelClassVO) {
        // ?????????????????????
        List<String> selectByChargeRule = channelClassVO.getSelectByChargeRule();
        List<String> selectByChannel = channelClassVO.getSelectByChannel();
        List<String> selectByCCID = channelClassVO.getSelectByCCID();
        // ????????????????????????
        List<String> selectByChargeRuleOther = channelClassVO.getSelectByChargeRuleOther();
        List<String> selectByChannelOther = channelClassVO.getSelectByChannelOther();
        List<String> selectByCCIDOther = channelClassVO.getSelectByCCIDOther();

        //???????????????????????????????????????CCID
        List<String> selectByChargeRuleCCID = new ArrayList<String>();
        //?????????????????????????????????CCID
        List<String> selectByChannelCCID = new ArrayList<String>();
        //??????????????????????????????????????????CCID
        List<String> selectByChargeRuleOtherCCID = new ArrayList<String>();
        //????????????????????????????????????CCID
        List<String> selectByChannelOtherCCID = new ArrayList<String>();
        if (CollectionUtil.isNotEmpty(selectByChargeRule)) {
            List<ChannelCooperation> channelCooperationList = channelCooperationMapper.selectList(new LambdaQueryWrapper<ChannelCooperation>().eq(ChannelCooperation::getDepartmentCode, channelClassVO.getDepartmentCode()).in(ChannelCooperation::getChargeRule, selectByChargeRule));
            if (CollectionUtil.isNotEmpty(channelCooperationList)) {
                selectByChargeRuleCCID = channelCooperationList.stream().map(ChannelCooperation::getCcid).collect(Collectors.toList());
            }
        }
        if (CollectionUtil.isNotEmpty(selectByChannel)) {
            List<ChannelCooperation> channelCooperationList = channelCooperationMapper.selectList(new LambdaQueryWrapper<ChannelCooperation>().eq(ChannelCooperation::getDepartmentCode, channelClassVO.getDepartmentCode()).in(ChannelCooperation::getChannelId, selectByChannel));
            if (CollectionUtil.isNotEmpty(channelCooperationList)) {
                selectByChannelCCID = channelCooperationList.stream().map(ChannelCooperation::getCcid).collect(Collectors.toList());
            }
        }
        if (CollectionUtil.isNotEmpty(selectByChargeRuleOther)) {
            List<ChannelCooperation> channelCooperationList = channelCooperationMapper.selectList(new LambdaQueryWrapper<ChannelCooperation>().eq(ChannelCooperation::getDepartmentCode, channelClassVO.getDepartmentCode()).in(ChannelCooperation::getChargeRule, selectByChargeRuleOther));
            if (CollectionUtil.isNotEmpty(channelCooperationList)) {
                selectByChargeRuleOtherCCID = channelCooperationList.stream().map(ChannelCooperation::getCcid).collect(Collectors.toList());
            }
        }
        if (CollectionUtil.isNotEmpty(selectByChannelOther)) {
            List<ChannelCooperation> channelCooperationList = channelCooperationMapper.selectList(new LambdaQueryWrapper<ChannelCooperation>().eq(ChannelCooperation::getDepartmentCode, channelClassVO.getDepartmentCode()).in(ChannelCooperation::getChannelId, selectByChannelOther));
            if (CollectionUtil.isNotEmpty(channelCooperationList)) {
                selectByChannelOtherCCID = channelCooperationList.stream().map(ChannelCooperation::getCcid).collect(Collectors.toList());
            }
        }

        //????????????????????????CCID????????????????????????????????????CCID????????????
        for (ChannelCooperation channelCooperation : ccidDBList) {
            channelCooperation.setSelect(selectByCCIDOther.contains(channelCooperation.getCcid()) ? "3" : selectByCCID.contains(channelCooperation.getCcid()) ? "1" : "2");
        }

        // ------------------------------- ????????? ----------------------------------
        List<ChannelCooperation> channelCooperationListByChargeRule = DozerUtil.toBeanList(ccidDBList, ChannelCooperation.class);
        List<ChannelCooperation> channelCooperationListByChannel = DozerUtil.toBeanList(ccidDBList, ChannelCooperation.class);

        // ???????????????
        Map<String, List<ChannelCooperation>> treeByChargeRule = channelCooperationListByChargeRule.stream().distinct().collect(Collectors.groupingBy(ChannelCooperation::getChargeRule));
        List<Map<String, Object>> treeByChargeRuleList = new ArrayList<Map<String, Object>>();
        for (Map.Entry<String, List<ChannelCooperation>> entry : treeByChargeRule.entrySet()) {
            List<ChannelCooperation> value = entry.getValue();

            //??????????????????CCID??????
            List<Map<String, String>> mapResponseList = new ArrayList<Map<String, String>>();
            for (ChannelCooperation channelCooperation : value) {
                //?????????CCID????????????
                if (selectByCCIDOther.contains(channelCooperation.getCcid()) || selectByCCID.contains(channelCooperation.getCcid())) {
                    continue;
                }

                Map<String, String> mapResponse = new HashMap<String, String>();
                mapResponse.put("key", String.valueOf(channelCooperation.getCcid()));
                mapResponse.put("value", String.valueOf(channelCooperation.getCcid()));
                mapResponse.put("select", selectByChannelOtherCCID.contains(channelCooperation.getCcid()) ? "3" : selectByChannelCCID.contains(channelCooperation.getCcid()) ? "1" : "2");
                mapResponse.put("modeType", "3");
                mapResponseList.add(mapResponse);
            }

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("key", entry.getKey()); // ??????ID
            map.put("value", entry.getKey()); // ????????????
            map.put("list", mapResponseList);
            //?????????????????????????????????????????????
            Long count3 = mapResponseList.stream().filter(i -> "3".equals(i.get("select"))).count();
            map.put("select", selectByChargeRuleOther.contains(entry.getKey()) || count3 > 0 ? "3" : selectByChargeRule.contains(entry.getKey()) ? "1" : "2");
            map.put("modeType", "1");
            treeByChargeRuleList.add(map);
        }

        // ?????????
        Map<String, List<ChannelCooperation>> treeByChannel = channelCooperationListByChannel.stream().distinct().collect(Collectors.groupingBy(ChannelCooperation::getChannelName));
        List<Map<String, Object>> treeByChannelList = new ArrayList<Map<String, Object>>();
        for (Map.Entry<String, List<ChannelCooperation>> entry : treeByChannel.entrySet()) {
            List<ChannelCooperation> value = entry.getValue();

            //????????????????????????CCID??????
            List<Map<String, String>> mapResponseList = new ArrayList<Map<String, String>>();
            for (ChannelCooperation channelCooperation : value) {
                //?????????CCID????????????
                if (selectByCCIDOther.contains(channelCooperation.getCcid()) || selectByCCID.contains(channelCooperation.getCcid())) {
                    continue;
                }

                Map<String, String> mapResponse = new HashMap<String, String>();
                mapResponse.put("key", String.valueOf(channelCooperation.getCcid()));
                mapResponse.put("value", String.valueOf(channelCooperation.getCcid()));
                mapResponse.put("select", selectByChargeRuleOtherCCID.contains(channelCooperation.getCcid()) ? "3" : selectByChargeRuleCCID.contains(channelCooperation.getCcid()) ? "1" : "2");
                mapResponse.put("modeType", "3");
                mapResponseList.add(mapResponse);
            }

            Map<String, Object> map = new HashMap<String, Object>();
            Long channelId = value.get(0).getChannelId();
            map.put("key", channelId); // ??????ID
            map.put("value", entry.getKey()); // ????????????
            map.put("list", mapResponseList);
            //?????????????????????????????????????????????
            Long count3 = mapResponseList.stream().filter(i -> "3".equals(i.get("select"))).count();
            map.put("select", selectByChannelOther.contains(String.valueOf(channelId)) || count3 > 0 ? "3" : selectByChannel.contains(String.valueOf(channelId)) ? "1" : "2");
            map.put("modeType", "2");
            treeByChannelList.add(map);
        }

        channelClassVO.setTreeByChargeRule(treeByChargeRuleList);
        channelClassVO.setTreeByChannel(treeByChannelList);
    }*/
}
