package com.stnts.bi.datamanagement.module.channel.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stnts.bi.common.BiSessionUtil;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.business.service.BusinessDictService;
import com.stnts.bi.datamanagement.module.channel.entity.*;
import com.stnts.bi.datamanagement.module.channel.mapper.*;
import com.stnts.bi.datamanagement.module.channel.param.ChannelChildPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelBaseIdService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelChildService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelPromotionAllService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelPromotionPositionService;
import com.stnts.bi.datamanagement.module.channel.vo.ChannelChildVO;
import com.stnts.bi.datamanagement.module.exportdata.param.ExportDataParam;
import com.stnts.bi.entity.common.PageEntity;
import com.stnts.bi.entity.sys.UserDmEntity;
import com.stnts.bi.entity.sys.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * ????????? ???????????????
 *
 * @author ?????????
 * @since 2021-02-04
 */
@Slf4j
@Service
public class ChannelChildServiceImpl extends ServiceImpl<ChannelChildMapper, ChannelChild> implements ChannelChildService {

    @Autowired
    private ChannelChildMapper channelChildMapper;

    @Autowired
    private ChannelPromotionMapper channelPromotionMapper;

    @Autowired
    private ChannelPromotionPositionService channelPromotionPositionService;

    @Autowired
    private ChannelPromotionPositionMapper channelPromotionPositionMapper;

    @Autowired
    private ChannelMapper channelMapper;

    @Autowired
    private ChannelBaseIdService channelBaseIdService;

    @Autowired
    private ChannelApplicationMapper channelApplicationMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private BusinessDictService businessDictService;

    @Autowired
    private ChannelPromotionAllService channelPromotionAllService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<Object, Object> saveChannelChild(ChannelChild channelChild) throws Exception {
        try {
            String subChannelId = channelBaseIdService.getNewSubChannelID(channelChild.getChannelId());
            channelChild.setSubChannelId(subChannelId);
            super.save(channelChild);
            Map<Object, Object> map = MapUtil.builder().put("subChannelId", subChannelId).build();
            return map;
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException) {
                throw new BusinessException("????????????????????????????????????");
            } else if (e instanceof DataIntegrityViolationException) {
                throw new BusinessException("???????????????2-50?????????");
            } else {
                throw e;
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateChannelChild(ChannelChild channelChild) throws Exception {
        try {
            //???????????????
            List<ChannelPromotionPosition> channelPromotionPositionList = channelChild.getChannelPromotionPositionList();
            List<Long> ppIdListByParam = channelPromotionPositionList.stream().map(ChannelPromotionPosition::getPpId).collect(Collectors.toList());

            //1. ??????????????????????????????
            if (channelPromotionPositionList.size() > 0) {
                List<ChannelPromotionPosition> cppListByDB = channelPromotionPositionMapper.selectList(new QueryWrapper<ChannelPromotionPosition>().lambda()
                        .eq(ChannelPromotionPosition::getChannelId, channelChild.getChannelId())
                        .notIn(ChannelPromotionPosition::getPpId, ppIdListByParam)
                );
                List<String> cppNameByDB = cppListByDB.stream().map(ChannelPromotionPosition::getPpName).distinct().collect(Collectors.toList());
                List<String> cppNameByParam = channelPromotionPositionList.stream().map(ChannelPromotionPosition::getPpName).distinct().collect(Collectors.toList());
                cppNameByDB.retainAll(cppNameByParam);
                if (cppNameByDB.size() > 0) {
                    throw new BusinessException("?????????????????????????????????????????????????????????????????????" + cppNameByDB);
                }
                if (channelPromotionPositionList.size() > cppNameByParam.size()) {
                    throw new BusinessException("???????????????????????????????????????");
                }

                channelPromotionPositionService.saveOrUpdateBatch(channelPromotionPositionList);
            }
            List<ChannelPromotionPosition> channelPromotionPositionListByDB = channelPromotionPositionService.list(new QueryWrapper<ChannelPromotionPosition>().lambda()
                    .eq(ChannelPromotionPosition::getSubChannelId, channelChild.getSubChannelId())
                    .eq(ChannelPromotionPosition::getPpFlag, 2)
            );
            List<Long> ppIdListByDB = channelPromotionPositionListByDB.stream().map(ChannelPromotionPosition::getPpId).collect(Collectors.toList());

            //??????????????????????????????
            ppIdListByDB.removeAll(ppIdListByParam);
            channelPromotionPositionService.removeByIds(ppIdListByDB);

            super.updateById(channelChild);

            //?????????
            channelPromotionAllService.updateChannelChildThread(channelChild);

            return true;
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException) {
                throw new BusinessException("????????????????????????????????????");
            } else if (e instanceof DataIntegrityViolationException) {
                throw new BusinessException("???????????????2-50?????????");
            } else {
                throw e;
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteChannelChild(Long id) throws Exception {

        ChannelChild channelChild = channelChildMapper.selectById(id);
        Integer count = channelPromotionMapper.selectCount(new QueryWrapper<ChannelPromotion>().lambda().eq(ChannelPromotion::getSubChannelId, channelChild.getSubChannelId()));
        if (count > 0) {
            throw new BusinessException("??????PID,????????????");
        }
        //?????????????????????
        channelPromotionPositionMapper.delete(new QueryWrapper<ChannelPromotionPosition>().lambda()
                .eq(ChannelPromotionPosition::getSubChannelId, channelChild.getSubChannelId())
                .eq(ChannelPromotionPosition::getPpFlag, 2)
        );
        return super.removeById(id);
    }


    @Override
    public PageEntity<ChannelChild> getChannelChildPageList(ChannelChildPageParam channelChildPageParam) throws Exception {
        Page<ChannelChild> page = new Page<>(channelChildPageParam.getPageIndex(), channelChildPageParam.getPageSize());
        List<ChannelChild> channelChildList = channelChildMapper.selectListBySQL(page, channelChildPageParam);

        List<String> subSchannelIdList = channelChildList.stream().map(ChannelChild::getSubChannelId).collect(Collectors.toList());

        if (subSchannelIdList.size() > 0) {
            List<ChannelPromotionPosition> channelChildPPCount = channelPromotionPositionMapper.selectList(new QueryWrapper<ChannelPromotionPosition>().select("sub_channel_id", "count(1) subChannelPPIDNumber").lambda()
                    .in(ChannelPromotionPosition::getSubChannelId, subSchannelIdList)
                    .eq(ChannelPromotionPosition::getPpFlag, 2)
                    .groupBy(ChannelPromotionPosition::getSubChannelId)
            );
            Map<String, Long> ccTemp = channelChildPPCount.stream().collect(Collectors.toMap(item -> item.getSubChannelId(), item -> item.getSubChannelPPIDNumber()));

            for (ChannelChild channelChild : channelChildList) {
                channelChild.setSubChannelPPIDNumber(ccTemp.containsKey(channelChild.getSubChannelId()) ? ccTemp.get(channelChild.getSubChannelId()) : 0l);
            }
        }

        return new PageEntity<>(page, channelChildList);
    }

    @Override
    public List<ChannelChild> getChannelChildList(ChannelChildPageParam channelChildPageParam) throws Exception {
        List<ChannelChild> channelChildList = channelChildMapper.selectListBySQL(null, channelChildPageParam);

        return channelChildList;
    }

    @Override
    public List<ChannelChild> getChannelChildListByPP(ChannelChildPageParam channelChildPageParam) {
        List<ChannelChild> channelChildList = channelChildMapper.getChannelChildListByPP(channelChildPageParam.getChannelId());
        for (int i = 1; i <= channelChildList.size(); i++) {
            ChannelChild channelChild = channelChildList.get(i - 1);
            if (ObjectUtil.isNotEmpty(channelChild.getPpStatus())) {
                channelChild.setPpStatusStr(channelChild.getPpStatus() == 1 ? "??????" : "??????");
            }
            channelChild.setId(Long.valueOf(i));
        }
        return channelChildList;
    }

    @Override
    public List<ChannelChild> getChannelChildListByCcid(ChannelChildPageParam channelChildPageParam, HttpServletRequest request) throws Exception {
        // ?????? By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        channelChildPageParam.setDepartmentCodeAllList(departmentCodeAllList);
        Map<Integer, List<UserDmEntity>> mapAll = businessDictService.getCCIDAndPidByPermissions(user);
        channelChildPageParam.setMapAll(mapAll);

        List<ChannelChild> channelChildList = channelChildMapper.listChannelChildByCcid(channelChildPageParam, user);

        List<String> applicationIdList = channelChildList.stream().filter(i -> StringUtils.isNotBlank(i.getApplicationId())).map(ChannelChild::getApplicationId).distinct().collect(Collectors.toList());
        Map<String, String> channelChildrenMap = new HashMap<String, String>();
        if (CollectionUtil.isNotEmpty(applicationIdList)) {
            List<ChannelApplication> channelChildren = channelApplicationMapper.getApplicationNameByChild(applicationIdList);
            channelChildrenMap = channelChildren.stream().collect(Collectors.toMap(s -> String.valueOf(s.getId()), s -> s.getApplicationName()));
        }

        for (ChannelChild channelChild : channelChildList) {
            if (channelChildrenMap.containsKey(channelChild.getApplicationId())) {
                channelChild.setApplicationName(channelChildrenMap.get(channelChild.getApplicationId()));
            }

            channelChild.setCcid(channelChildPageParam.getCcid());
        }

        /*List<ChannelApplication> channelChildren = channelApplicationMapper.getApplicationNameByChild(channelChildList);
        Map<String, String> channelChildrenMap = channelChildren.stream().collect(Collectors.toMap(s -> s.getProductCode() + s.getId(), s -> s.getApplicationName()));

        for (ChannelChild channelChild : channelChildList) {
            String key = channelChild.getProductCode() + channelChild.getApplicationId();
            if (channelChildrenMap.containsKey(key)) {
                channelChild.setApplicationName(channelChildrenMap.get(key));
            }

            channelChild.setCcid(channelChildPageParam.getCcid());
        }*/

        return channelChildList;
    }

    @Override
    public ChannelChild getByIdExt(Long id) {
        ChannelChild channelChild = channelChildMapper.selectOne(new QueryWrapper<ChannelChild>().lambda().eq(ChannelChild::getId, id));
        List<ChannelPromotionPosition> channelPromotionPositionList = channelPromotionPositionMapper.selectList(new QueryWrapper<ChannelPromotionPosition>().lambda()
                .eq(ChannelPromotionPosition::getSubChannelId, channelChild.getSubChannelId())
                .eq(ChannelPromotionPosition::getPpFlag, 2)
        );

        //????????????Pid??????
        List<Long> ppidList = channelPromotionPositionList.stream().map(ChannelPromotionPosition::getPpId).distinct().collect(Collectors.toList());
        if (ppidList.size() > 0) {
            List<ChannelPromotion> channelPromotionList = channelPromotionMapper.selectList(new QueryWrapper<ChannelPromotion>().select("pp_id, count(1) pidNum").lambda().in(ChannelPromotion::getPpId, ppidList).groupBy(ChannelPromotion::getPpId));
            Map<Long, Integer> channelPromotionMap = channelPromotionList.stream().collect(Collectors.toMap(item -> item.getPpId(), item -> item.getPidNum()));
            for (ChannelPromotionPosition channelPromotionPosition : channelPromotionPositionList) {
                Long ppid = channelPromotionPosition.getPpId();
                if (channelPromotionMap.containsKey(ppid)) {
                    channelPromotionPosition.setPpIdPIDNumber(Long.valueOf(channelPromotionMap.get(ppid)));
                }
            }
        }

        channelChild.setChannelPromotionPositionList(channelPromotionPositionList);

        return channelChild;
    }

    @Override
    public List<ChannelChild> selectListByExcel(List<ExportDataParam> channelChildByExcel) {
        List<ChannelChild> channelChildList = channelChildMapper.selectListByExcel(channelChildByExcel);
        return channelChildList;
    }

    @Override
    public ChannelChildVO saveChannelChildGeneral(ChannelChild channelChildParam) throws Exception {
        //????????????
        emptyParam(channelChildParam);

        Long channelId = channelChildParam.getChannelId();
        Channel channel = channelMapper.selectOne(new LambdaQueryWrapper<Channel>().eq(Channel::getChannelId, channelId));
        if (ObjectUtil.isEmpty(channel)) {
            throw new BusinessException("???????????????");
        }
        ChannelChild channelChildDB = channelChildMapper.selectOne(new LambdaQueryWrapper<ChannelChild>().eq(ChannelChild::getChannelId, channelId).eq(ChannelChild::getSubChannelName, channelChildParam.getSubChannelName()));
        if (ObjectUtil.isNotEmpty(channelChildDB)) {
            throw new BusinessException("?????????????????????");
        }
        channelChildParam.setChannelId(channel.getChannelId());

        Map<Object, Object> result = this.saveChannelChild(channelChildParam);

        String subChannelId = String.valueOf(result.get("subChannelId"));
        ChannelChildVO channelChildVO = new ChannelChildVO();
        BeanUtils.copyProperties(channel, channelChildVO);
        channelChildVO.setSubChannelId(subChannelId);
        channelChildVO.setSubChannelName(channelChildParam.getSubChannelName());

        ChannelChild channelChild = this.getOne(new LambdaQueryWrapper<ChannelChild>().eq(ChannelChild::getSubChannelId, subChannelId));
        channelChildVO.setCreateTime(channelChild.getCreateTime());
        channelChildVO.setUpdateTime(channelChild.getUpdateTime());

        return channelChildVO;
    }

    @Override
    public List<ChannelChildVO> channelChildListGeneral(ChannelChildVO channelChildVO) {
        List<ChannelChildVO> channelChildResult = channelChildMapper.channelChildListGeneral(channelChildVO);
        channelChildResult = channelChildResult.stream().filter(i -> ObjectUtil.isNotEmpty(i.getChannelId())).collect(Collectors.toList());
        return channelChildResult;
    }

    private void emptyParam(ChannelChild channelChild) {
        List<String> errorMsg = new ArrayList<String>();
        if (ObjectUtil.isEmpty(channelChild.getChannelId())) {
            errorMsg.add("??????ID??????");
        }
        if (StringUtils.isBlank(channelChild.getSubChannelName())) {
            errorMsg.add("?????????????????????");
        } else {
            Pattern pattern = Pattern.compile("[^\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w-_()]");
            Matcher matcher = pattern.matcher(channelChild.getSubChannelName());
            if (matcher.find()) {
                errorMsg.add("?????????????????????????????????????????????????????????????????????");
            }
        }
        if (errorMsg.size() > 0) {
            throw new BusinessException(String.join(",", errorMsg));
        }
    }

    private LambdaQueryWrapper<ChannelChild> getLambdaQueryWrapper(ChannelChildPageParam channelChildPageParam) {
        LambdaQueryWrapper<ChannelChild> wrapper = new LambdaQueryWrapper<>();
        // update yf by 20210717(?????????)
        wrapper.eq(StrUtil.isNotEmpty(channelChildPageParam.getCcid()), ChannelChild::getCcid, channelChildPageParam.getCcid());
        //wrapper.inSql(StrUtil.isNotEmpty(channelChildPageParam.getCcid()), ChannelChild::getChannelId, "select c.channel_id from dm_channel_cooperation c where c.ccid = '" + channelChildPageParam.getCcid() + "'");
        wrapper.eq(ObjectUtil.isNotNull(channelChildPageParam.getChannelId()), ChannelChild::getChannelId, channelChildPageParam.getChannelId());
        if (StrUtil.isNotEmpty(channelChildPageParam.getDepartmentCode())) {
            List<Long> channelList = channelMapper.selectList(new QueryWrapper<Channel>().select("channel_id")
                    .lambda().eq(Channel::getDepartmentCode, channelChildPageParam.getDepartmentCode())).stream().map(Channel::getChannelId).collect(Collectors.toList());
            wrapper.in(CollectionUtil.isNotEmpty(channelList), ChannelChild::getChannelId, channelList);
        }
        return wrapper;
    }
}
