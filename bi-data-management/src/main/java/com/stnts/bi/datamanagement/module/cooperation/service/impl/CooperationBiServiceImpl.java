package com.stnts.bi.datamanagement.module.cooperation.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stnts.bi.common.BiSessionUtil;
import com.stnts.bi.datamanagement.constant.BooleanEnum;
import com.stnts.bi.datamanagement.constant.EnvironmentProperties;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.business.service.BusinessDictService;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelCooperationMapper;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelMapper;
import com.stnts.bi.datamanagement.module.channel.service.ChannelPromotionAllService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelService;
import com.stnts.bi.datamanagement.module.cooperation.entity.Cooperation;
import com.stnts.bi.datamanagement.module.cooperation.entity.CooperationBi;
import com.stnts.bi.datamanagement.module.cooperation.entity.CooperationBiHistory;
import com.stnts.bi.datamanagement.module.cooperation.mapper.CooperationBiMapper;
import com.stnts.bi.datamanagement.module.cooperation.mapper.CooperationMapper;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationBiHistoryService;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationBiService;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationService;
import com.stnts.bi.datamanagement.module.cooperation.vo.UserVO;
import com.stnts.bi.datamanagement.module.feign.SysClient;
import com.stnts.bi.datamanagement.util.CompareMapUtil;
import com.stnts.bi.datamanagement.util.DozerUtil;
import com.stnts.bi.datamanagement.util.JacksonUtil;
import com.stnts.bi.datamanagement.util.SignUtil;
import com.stnts.bi.entity.sys.DepartmentEntity;
import com.stnts.bi.entity.sys.OrgEntity;
import com.stnts.bi.entity.sys.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * ???????????? ?????????BI??????+??????????????? ???????????????
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-03
 */
@Service
public class CooperationBiServiceImpl extends ServiceImpl<CooperationBiMapper, CooperationBi> implements CooperationBiService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private BusinessDictService businessDictService;

    @Autowired
    private SysClient sysClient;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private ChannelMapper channelMapper;

    @Autowired
    private ChannelCooperationMapper channelCooperationMapper;

    @Autowired
    private ChannelPromotionAllService channelPromotionAllService;

    @Autowired
    private CooperationBiMapper cooperationBiMapper;

    @Autowired
    private CooperationMapper cooperationMapper;

    private final Map<String, String> englishToChineseFieldNameMap = MapUtil.<String, String>builder().put("cooperationType", "???????????????").put("cpName", "???????????????")
            .put("cpCode", "?????????").put("companyName", "????????????").put("companyType", "????????????").put("companyTaxkey", "???????????????")
            .put("companyLegal", "??????????????????")
            .put("companyTel", "????????????").put("companyContact", "???????????????").put("contactPhone", "?????????????????????")
            .put("contactMail", "????????????").put("contactFax", "??????").put("companyWebsite", "????????????").put("companyAddress", "????????????")
            .put("parentIndustry", "????????????????????????").put("childIndustry", "????????????????????????").put("handlerUserName", "?????????")
            .put("isTest", "????????????").put("companyDesc", "????????????").put("companyProducts", "??????????????????")
            .put("isProtection", "????????????")
            .map();

    private final Map<String, Map<Integer, String>> fieldConstantValueMap = MapUtil.<String, Map<Integer, String>>builder()
            .put("cooperationType", MapUtil.<Integer, String>builder().put(1, "????????????").put(2, "???????????????").build())
            .put("companyType", MapUtil.<Integer, String>builder().put(1, "??????").put(2, "??????").build())
            .put("isTest", MapUtil.<Integer, String>builder().put(0, "??????").put(1, "??????").build())
            .put("isProtection", MapUtil.<Integer, String>builder().put(0, "?????????").put(1, "????????????").put(2, "????????????").build())
            .build();

    private final CooperationService cooperationService;

    private final CooperationBiHistoryService cooperationBiHistoryService;

    private final EnvironmentProperties environmentProperties;

    private final SignUtil signUtil;

    public CooperationBiServiceImpl(CooperationService cooperationService, CooperationBiHistoryService cooperationBiHistoryService, EnvironmentProperties environmentProperties, SignUtil signUtil) {
        this.cooperationService = cooperationService;
        this.cooperationBiHistoryService = cooperationBiHistoryService;
        this.environmentProperties = environmentProperties;
        this.signUtil = signUtil;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveCooperation(CooperationBi cooperationBi) {
        super.save(cooperationBi);
        Cooperation cooperation = DozerUtil.toBean(cooperationBi, Cooperation.class);
        cooperationService.save(cooperation);
        CooperationBi updateCooperationBi = new CooperationBi();
        updateCooperationBi.setId(cooperationBi.getId());
        updateCooperationBi.setRelatedCooperationId(cooperation.getId());
        super.updateById(updateCooperationBi);
        cooperationBi.setRelatedCooperationId(cooperation.getId());

        if (BooleanEnum.True.getKey().equals(cooperationBi.getLastStatus())) {
            cooperationService.switchStatus(cooperation.getId(), cooperationBi.getLastStatus(), cooperationBi.getLastRemark(), NumberUtil.parseInt(cooperationBi.getCreateUserId()), cooperationBi.getCreateUser());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCooperation(CooperationBi cooperationBi) {
        Long cooperationBiId = cooperationBi.getId();
        CooperationBi existCooperationBi = super.getById(cooperationBiId);
        if (ObjectUtil.isNull(existCooperationBi)) {
            throw new BusinessException(StrUtil.format("id???{}????????????????????????", cooperationBiId));
        }
        super.updateById(cooperationBi);
        // TODO ???????????????????????????????????????????????????
        Cooperation cooperation = DozerUtil.toBean(cooperationBi, Cooperation.class);
        cooperation.setId(existCooperationBi.getRelatedCooperationId());
        cooperationService.updateById(cooperation);

        saveCooperationBiHistory(cooperationBi, existCooperationBi, cooperationBi.getUpdateUser(), cooperationBi.getUpdateUserId());

        //??????dm_channel???dm_channel_cooperation???dm_pid_dim???????????????
        channelMapper.updateCompanyName(cooperation.getId(), cooperation.getCompanyName());
        channelCooperationMapper.updateCompanyName(cooperation.getId(), cooperation.getCompanyName());

        //?????????
        channelPromotionAllService.updateCompanyThread(cooperation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long id) {
        CooperationBi existCooperationBi = super.getById(id);
        if (existCooperationBi == null) {
            throw new BusinessException(StrUtil.format("id???{}????????????????????????", id));
        }
        super.removeById(id);
        Long relatedCooperationId = existCooperationBi.getRelatedCooperationId();
        List<CooperationBi> relatedCooperationBiList = super.list(new QueryWrapper<CooperationBi>().lambda()
                .eq(CooperationBi::getRelatedCooperationId, relatedCooperationId)
                .ne(CooperationBi::getId, id));
        if (CollectionUtil.isEmpty(relatedCooperationBiList)) {
            cooperationService.removeById(relatedCooperationId);
        }
    }

    @Override
    public List departmentList(HttpServletRequest request) {
        // ?????? By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();

        List<Map<String, Object>> mapList = new ArrayList<>();
        if (user != null) {
            List<OrgEntity> orgEntities = user.getOrgs();
            List<Integer> orgId = orgEntities.stream().map(OrgEntity::getOrgId).collect(Collectors.toList());

            List<DepartmentEntity> departmentEntityList = new ArrayList<>();
            if (orgId != null && orgId.size() > 0) {
                //??????????????????????????????????????????????????????????????????????????????????????????????????????????????? by yifan 20210723???
                departmentEntityList = sysClient.listDepartmentByOrgId(orgId);
                List<String> deList = departmentEntityList.stream().filter(i -> StringUtils.isNotBlank(i.getCode())).map(DepartmentEntity::getCode).collect(Collectors.toList());
                if (!deList.contains(user.getCode())) {
                    DepartmentEntity departmentEntity = new DepartmentEntity();
                    departmentEntity.setCode(user.getCode());
                    departmentEntity.setName(user.getDepartmentName());
                    departmentEntityList.add(departmentEntity);
                }
            } else {
                //?????????????????????????????????????????????
                DepartmentEntity departmentEntity = new DepartmentEntity();
                departmentEntity.setCode(user.getCode());
                departmentEntity.setName(user.getDepartmentName());
                departmentEntityList.add(departmentEntity);
            }
            for (DepartmentEntity files : departmentEntityList) {
                Map<String, Object> map = new HashMap<String, Object>();
                map = JSONObject.parseObject(JSONObject.toJSONString(files), Map.class);
                mapList.add(map);
            }
        }
        return mapList;
    }

    @Override
    public List departmentListAll(HttpServletRequest request) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        List<DepartmentEntity> departmentEntityList = sysClient.listDepartmentByOrgId(new ArrayList<Integer>());
        for (DepartmentEntity files : departmentEntityList) {
            Map<String, Object> map = new HashMap<String, Object>();
            map = JSONObject.parseObject(JSONObject.toJSONString(files), Map.class);
            mapList.add(map);
        }
        return mapList;
    }

    @Override
    public List<UserVO> queryUserTree() {
        Long timestamp = System.currentTimeMillis();
        TreeMap<String, Object> params = MapUtil.newTreeMap(Comparator.comparing(String::toString));
        params.put("appId", environmentProperties.getUamsAppId());
        params.put("timestamp", timestamp);
        String sign = signUtil.getSign(params, environmentProperties.getUamsSecret());
        params.put("format", "json");
        params.put("sign", sign);
        String getResult = HttpUtil.get(StrUtil.format("http://{}/ump/api/listUserInfo", environmentProperties.getUamsAddress()), params);
        Map map = JacksonUtil.fromJSON(getResult, Map.class);
        List users = (List) map.get("data");
        List<UserVO> userVOList = DozerUtil.toBeanList(users, UserVO.class);
        List<UserVO> result = userVOList.stream()
                .filter(v -> v.getStatus() == 1)
                .peek(v -> v.setCnname(StrUtil.format("{}({})", v.getCnname(), v.getCardNumber())))
                .collect(Collectors.toList());
        return result;
    }

    private void saveCooperationBiHistory(CooperationBi cooperationBi, CooperationBi existCooperationBi, String userName, String userId) {
        Map<String, Object> sourceMap = BeanUtil.beanToMap(existCooperationBi);
        Map<String, Object> targetMap = BeanUtil.beanToMap(cooperationBi);
        replaceConstant(sourceMap);
        replaceConstant(targetMap);
        rename(sourceMap);
        rename(targetMap);
        CompareMapUtil.compareMap(sourceMap, targetMap);
        if (MapUtil.isNotEmpty(sourceMap) || MapUtil.isNotEmpty(targetMap)) {
            CooperationBiHistory cooperationBiHistory = new CooperationBiHistory();
            cooperationBiHistory.setCooperationBiId(cooperationBi.getId());
            cooperationBiHistory.setCreateUser(userName);
            cooperationBiHistory.setCreateUserId(userId);
            cooperationBiHistory.setSourceContent(JacksonUtil.toJSON(sourceMap));
            cooperationBiHistory.setTargetContent(JacksonUtil.toJSON(targetMap));
            cooperationBiHistoryService.save(cooperationBiHistory);
        }
    }

    private void replaceConstant(Map<String, Object> map) {
        map.forEach((key, value) -> {
            if (fieldConstantValueMap.containsKey(key)) {
                Map<Integer, String> integerStringMap = fieldConstantValueMap.get(key);
                map.put(key, integerStringMap.get(value));
            }
        });
    }

    private void rename(Map<String, Object> map) {
        if (map == null) {
            return;
        }
        map.entrySet().removeIf(v -> !englishToChineseFieldNameMap.containsKey(v.getKey()));
        List<String> keys = new ArrayList<>(map.keySet());
        for (String key : keys) {
            map.put(englishToChineseFieldNameMap.get(key), map.remove(key));
        }
    }
}
