package com.stnts.bi.datamanagement.module.cooperation.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stnts.bi.datamanagement.constant.DataSourceConstant;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.channel.entity.Channel;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelPromotionAll;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelCooperationMapper;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelPromotionAllMapper;
import com.stnts.bi.datamanagement.module.channel.service.ChannelService;
import com.stnts.bi.datamanagement.module.cooperation.entity.Cooperation;
import com.stnts.bi.datamanagement.module.cooperation.entity.CooperationCrm;
import com.stnts.bi.datamanagement.module.cooperation.enums.CooperationTypeEnum;
import com.stnts.bi.datamanagement.module.cooperation.enums.IsProtectionEnum;
import com.stnts.bi.datamanagement.module.cooperation.mapper.CooperationCrmMapper;
import com.stnts.bi.datamanagement.module.cooperation.mapper.CooperationMapper;
import com.stnts.bi.datamanagement.module.cooperation.param.CooperationAddApiParam;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationBiService;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationCrmService;
import com.stnts.bi.datamanagement.module.cooperation.vo.UserVO;
import com.stnts.bi.datamanagement.util.DozerUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * ???????????????CRM??? ???????????????
 * </p>
 *
 * @author yifan
 * @since 2021-07-29
 */
@Service
public class CooperationCrmServiceImpl extends ServiceImpl<CooperationCrmMapper, CooperationCrm> implements CooperationCrmService {
    @Autowired
    private CooperationMapper cooperationMapper;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private ChannelCooperationMapper channelCooperationMapper;
    @Autowired
    private CooperationCrmMapper cooperationCrmMapper;
    @Autowired
    private ChannelPromotionAllMapper channelPromotionAllMapper;
    @Autowired
    private CooperationBiService cooperationBiService;

    @Override
    public Map<String, String> addCompany(CooperationAddApiParam cooperationAddApiParam) throws Exception {
        List<String> errorMsg = new ArrayList<String>();
        if (StringUtils.isBlank(cooperationAddApiParam.getUserId())) {
            errorMsg.add("????????????????????????");
        } else {
            List<UserVO> userVOList = cooperationBiService.queryUserTree();
            List<UserVO> userVOListParam = userVOList.stream().filter(i -> cooperationAddApiParam.getUserId().equals(i.getCardNumber())).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(userVOListParam)) {
                errorMsg.add("?????????????????????");
            } else {
                UserVO userVO = userVOListParam.get(0);
                cooperationAddApiParam.setUserId(String.valueOf(userVO.getId()));
                cooperationAddApiParam.setUserName(userVO.getCnname());
            }
        }
        /*if (StringUtils.isBlank(cooperationAddApiParam.getCompanyId()) && StringUtils.isNotBlank(cooperationAddApiParam.getChannelId())) {
            errorMsg.add("???????????????????????????");
        }*/
        if (StringUtils.isBlank(cooperationAddApiParam.getCompanyName())) {
            errorMsg.add("????????????????????????");
        }
        if (StringUtils.isBlank(cooperationAddApiParam.getChannelName())) {
            errorMsg.add("????????????????????????");
        }
        if (ObjectUtils.isNotEmpty(cooperationAddApiParam.getCooperationType()) && ObjectUtils.isEmpty(CooperationTypeEnum.getByKey(cooperationAddApiParam.getCooperationType()))) {
            errorMsg.add("????????????????????????1??????????????????2?????????????????????");
        }
        if (StringUtils.isNotBlank(cooperationAddApiParam.getCompanyTaxkey()) && !cooperationAddApiParam.getCompanyTaxkey().matches("^[a-zA-Z0-9]{15,19}$")) {
            errorMsg.add("?????????15~19??????????????????????????????");
        }
        if (ObjectUtils.isNotEmpty(cooperationAddApiParam.getIsProtection()) && ObjectUtils.isEmpty(IsProtectionEnum.getByKey(cooperationAddApiParam.getIsProtection()))) {
            errorMsg.add("?????????????????????0???????????????1??????????????????2??????????????????");
        }
        if (ObjectUtils.isNotEmpty(cooperationAddApiParam.getIsTest()) && 0 != cooperationAddApiParam.getIsTest() && 1 != cooperationAddApiParam.getIsTest()) {
            errorMsg.add("???????????????????????????0????????????1?????????");
        }
        if (ObjectUtils.isNotEmpty(cooperationAddApiParam.getInSystem()) && 1 != cooperationAddApiParam.getInSystem() && 2 != cooperationAddApiParam.getInSystem()) {
            errorMsg.add("?????????????????????????????????1?????????2?????????");
        }
        if (ObjectUtils.isNotEmpty(cooperationAddApiParam.getNatureContract()) && 1 != cooperationAddApiParam.getNatureContract() && 2 != cooperationAddApiParam.getNatureContract()) {
            errorMsg.add("?????????????????????1??????????????????2??????????????????");
        }
        if (CollectionUtil.isNotEmpty(errorMsg)) {
            throw new BusinessException(StringUtil.join(errorMsg.toArray(), "???"));
        }

        List<Long> deleteCooperationId = new ArrayList<Long>();
        Cooperation cooperation = new Cooperation();
        Channel channel = new Channel();
        try {
            // ????????????
            if (StringUtils.isBlank(cooperationAddApiParam.getCompanyId())) {
                CooperationCrm cooperationCrm = new CooperationCrm();
                BeanUtils.copyProperties(cooperationAddApiParam, cooperationCrm);

                List<Cooperation> existCooperation = cooperationMapper.selectList(new QueryWrapper<Cooperation>().lambda()
                        .eq(Cooperation::getCompanyName, cooperationAddApiParam.getCompanyName())
                        .eq(Cooperation::getCooperationType, cooperationAddApiParam.getCooperationType())
                );
                if (CollectionUtil.isNotEmpty(existCooperation)) {
                    throw new BusinessException("??????????????????");
                }

                /*if (StrUtil.isNotEmpty(cooperationAddApiParam.getCompanyQualification())) {
                    String[] fileArray = StrUtil.splitToArray(cooperationAddApiParam.getCompanyQualification(), ',');
                    for (String split : fileArray) {
                        String tempPath = System.getProperty("java.io.tmpdir") + File.separator;
                        File source = new File(tempPath + File.separator + split);

                        String staticLocations = "/webser/www/bi-data-management/static/";
                        FileUtil.mkdir(staticLocations);
                        File target = new File(staticLocations + split);
                        FileUtil.copy(source, target, true);
                    }
                }*/
                cooperationCrm.setCompanyLegal("");
                cooperationCrm.setCompanyQualification("");

                cooperationCrm.setDataSource(DataSourceConstant.dataSourceCRM);
                this.save(cooperationCrm);

                //??????????????????
                cooperation = DozerUtil.toBean(cooperationCrm, Cooperation.class);
                cooperationMapper.insert(cooperation);
                cooperationCrm.setRelatedCooperationId(String.valueOf(cooperation.getId()));
                super.updateById(cooperationCrm);

                //??????????????????????????????ID
                deleteCooperationId.add(cooperation.getId());
            } else {
                cooperation = cooperationMapper.selectOne(new QueryWrapper<Cooperation>().lambda().eq(Cooperation::getId, Long.valueOf(cooperationAddApiParam.getCompanyId())));
                if (ObjectUtils.isEmpty(cooperation)) {
                    throw new BusinessException("??????????????????");
                }
            }
            //CRM
            if (StringUtils.isBlank(cooperationAddApiParam.getChannelId())) {
                // ??????????????????
                String channelName = cooperationAddApiParam.getChannelName();
                //1.???????????????????????? 2.???????????????????????????
                Channel channelDB = channelService.getOne(new QueryWrapper<Channel>().lambda().eq(Channel::getChannelName, channelName).eq(Channel::getSecretType, 1));
                if (ObjectUtil.isEmpty(channelDB)) {
                    channelDB = channelService.getOne(new QueryWrapper<Channel>().lambda().eq(Channel::getChannelName, channelName).eq(Channel::getDepartmentCode, cooperationAddApiParam.getDepartmentCode()));
                }
                if (ObjectUtil.isNotEmpty(channelDB)) {
                    throw new BusinessException("??????" + channelName + "?????????");
                }

                // ????????????
                channel.setChannelName(cooperationAddApiParam.getChannelName());
                channel.setChannelType(cooperationAddApiParam.getChannelType());
                channel.setSecretType(cooperationAddApiParam.getSecretType());
                channel.setSettlementType(cooperationAddApiParam.getSettlementType());
                channel.setCompanyId(cooperation.getId());
                channel.setCompanyName(cooperation.getCompanyName());
                channel.setDepartmentCode(cooperationAddApiParam.getDepartmentCode());
                channel.setDepartmentName(cooperationAddApiParam.getDepartmentName());
                channel.setCustomerId(cooperationAddApiParam.getCustomerId());
                channel.setCustomerName(cooperationAddApiParam.getCustomerName());
                channel.setCrmType(cooperationAddApiParam.getCrmType());
                channel.setUserid(Long.valueOf(cooperationAddApiParam.getUserId()));
                channel.setUsername(cooperationAddApiParam.getUserName());
                if (StringUtils.isNotBlank(cooperationAddApiParam.getDataSource())) {
                    channel.setDataSource(cooperationAddApiParam.getDataSource());
                }
                channelService.saveChannel(channel);
            } else {
                channel = channelService.getOne(new QueryWrapper<Channel>().lambda().eq(Channel::getChannelId, cooperationAddApiParam.getChannelId()));
                if (ObjectUtils.isEmpty(channel)) {
                    throw new BusinessException("???????????????");
                }
                channel.setCustomerId(cooperationAddApiParam.getCustomerId());
                channel.setCustomerName(cooperationAddApiParam.getCustomerName());
                if (StringUtils.isNotBlank(cooperationAddApiParam.getCrmType())) {
                    channel.setCrmType(cooperationAddApiParam.getCrmType());
                }
                channel.setCompanyId(cooperation.getId());
                channel.setCompanyName(cooperation.getCompanyName());
                channelService.updateChannel(channel);

                //?????????????????????????????????????????????CCID?????????
                channelCooperationMapper.updateCompany(String.valueOf(channel.getChannelId()), String.valueOf(cooperation.getId()), cooperation.getCompanyName());
                channelPromotionAllMapper.updateCompany(String.valueOf(channel.getChannelId()), String.valueOf(cooperation.getId()), cooperation.getCompanyName());
            }
        } catch (Exception e) {
            if (deleteCooperationId.size() > 0) {
                cooperationCrmMapper.delete(new QueryWrapper<CooperationCrm>().lambda().in(CooperationCrm::getRelatedCooperationId, deleteCooperationId));
                cooperationMapper.delete(new QueryWrapper<Cooperation>().lambda().in(Cooperation::getId, deleteCooperationId));
            }
            throw new BusinessException(e.getMessage(), e);
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("companyId", String.valueOf(cooperation.getId()));
        map.put("channelId", String.valueOf(channel.getChannelId()));

        return map;
    }

    @Override
    public void updateCompany(CooperationAddApiParam cooperationAddApiParam) {
        //????????????????????????CRM???????????????????????????
        List<String> errorMsg = new ArrayList<String>();
        CooperationCrm cooperationCrm = cooperationCrmMapper.selectOne(new LambdaQueryWrapper<CooperationCrm>().eq(CooperationCrm::getRelatedCooperationId, cooperationAddApiParam.getCompanyId()));
        if (ObjectUtils.isEmpty(cooperationCrm)) {
            errorMsg.add("??????????????????CRM??????");
        }
        if (StringUtils.isBlank(cooperationAddApiParam.getCompanyName())) {
            errorMsg.add("??????????????????");
        }
        if (ObjectUtils.isNotEmpty(cooperationAddApiParam.getCooperationType()) && ObjectUtils.isEmpty(CooperationTypeEnum.getByKey(cooperationAddApiParam.getCooperationType()))) {
            errorMsg.add("????????????????????????1??????????????????2?????????????????????");
        }
        if (StringUtils.isNotBlank(cooperationAddApiParam.getCompanyTaxkey()) && !cooperationAddApiParam.getCompanyTaxkey().matches("^[a-zA-Z0-9]{15,19}$")) {
            errorMsg.add("?????????15~19??????????????????????????????");
        }
        if (ObjectUtils.isNotEmpty(cooperationAddApiParam.getIsProtection()) && ObjectUtils.isEmpty(IsProtectionEnum.getByKey(cooperationAddApiParam.getIsProtection()))) {
            errorMsg.add("?????????????????????0???????????????1??????????????????2??????????????????");
        }
        if (ObjectUtils.isNotEmpty(cooperationAddApiParam.getIsTest()) && 0 != cooperationAddApiParam.getIsTest() && 1 != cooperationAddApiParam.getIsTest()) {
            errorMsg.add("???????????????????????????0????????????1?????????");
        }
        if (ObjectUtils.isNotEmpty(cooperationAddApiParam.getInSystem()) && 1 != cooperationAddApiParam.getInSystem() && 2 != cooperationAddApiParam.getInSystem()) {
            errorMsg.add("?????????????????????????????????1?????????2?????????");
        }
        if (ObjectUtils.isNotEmpty(cooperationAddApiParam.getNatureContract()) && 1 != cooperationAddApiParam.getNatureContract() && 2 != cooperationAddApiParam.getNatureContract()) {
            errorMsg.add("?????????????????????1??????????????????2??????????????????");
        }
        if (CollectionUtil.isNotEmpty(errorMsg)) {
            throw new BusinessException(StringUtil.join(errorMsg.toArray(), "???"));
        }

        cooperationCrmMapper.updateCompanyName(cooperationAddApiParam);
        cooperationMapper.updateCompanyName(cooperationAddApiParam);
        channelService.updateCompanyName(cooperationAddApiParam);
        channelCooperationMapper.updateCompanyName(Long.valueOf(cooperationAddApiParam.getCompanyId()), cooperationAddApiParam.getCompanyName());
        channelPromotionAllMapper.updateCompanyName(Long.valueOf(cooperationAddApiParam.getCompanyId()), cooperationAddApiParam.getCompanyName());
    }

    @Override
    public void updateChannel(CooperationAddApiParam cooperationAddApiParam) {
        //????????????
        emptyParamUpdateChannel(cooperationAddApiParam);

        Cooperation cooperation = cooperationMapper.selectOne(new LambdaQueryWrapper<Cooperation>().eq(Cooperation::getId, cooperationAddApiParam.getCompanyId()));
        if (ObjectUtils.isEmpty(cooperation)) {
            throw new BusinessException("??????ID?????????");
        }
        Channel channel = channelService.getOne(new LambdaQueryWrapper<Channel>().eq(Channel::getChannelId, cooperationAddApiParam.getChannelId()));
        if (ObjectUtils.isEmpty(channel)) {
            throw new BusinessException("??????ID?????????");
        } else if (StringUtils.isBlank(channel.getCrmType())) {
            throw new BusinessException("????????????CRM?????????????????????");
        }

        channel.setCompanyId(cooperation.getId());
        channel.setCompanyName(cooperation.getCompanyName());
        channelService.updateById(channel);

        cooperationAddApiParam.setCompanyName(cooperation.getCompanyName());
        channelCooperationMapper.updateCompany(cooperationAddApiParam.getChannelId(), cooperationAddApiParam.getCompanyId(), cooperationAddApiParam.getCompanyName());
        channelPromotionAllMapper.updateCompany(cooperationAddApiParam.getChannelId(), cooperationAddApiParam.getCompanyId(), cooperationAddApiParam.getCompanyName());

    }

    private void emptyParamUpdateChannel(CooperationAddApiParam cooperationAddApiParam) {
        List<String> errorMsg = new ArrayList<String>();
        if (ObjectUtil.isEmpty(cooperationAddApiParam.getCompanyId())) {
            errorMsg.add("??????ID??????");
        }
        if (org.apache.commons.lang3.StringUtils.isBlank(cooperationAddApiParam.getChannelId())) {
            errorMsg.add("??????ID??????");
        }
        if (errorMsg.size() > 0) {
            throw new BusinessException(String.join(",", errorMsg));
        }
    }


    public static void main(String[] args) {
        List<ChannelPromotionAll> channelPromotionAllList = new ArrayList<>();
        channelPromotionAllList.stream().forEach(i -> i.setCompanyName("1"));

        System.out.println(System.currentTimeMillis());
    }
}
