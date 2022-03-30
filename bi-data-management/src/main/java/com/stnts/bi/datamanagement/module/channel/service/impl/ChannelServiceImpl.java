package com.stnts.bi.datamanagement.module.channel.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stnts.bi.common.BiSessionUtil;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.business.service.BusinessDictService;
import com.stnts.bi.datamanagement.module.channel.entity.Channel;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelChild;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelCooperation;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelPromotionPosition;
import com.stnts.bi.datamanagement.module.channel.mapper.*;
import com.stnts.bi.datamanagement.module.channel.param.ChannelPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelBaseIdService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelProductService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelPromotionAllService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelService;
import com.stnts.bi.datamanagement.module.cooperation.entity.Cooperation;
import com.stnts.bi.datamanagement.module.cooperation.param.CooperationAddApiParam;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationBiService;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationService;
import com.stnts.bi.datamanagement.module.cooperation.vo.UserVO;
import com.stnts.bi.datamanagement.module.exportdata.dataenum.ChannelTypeEnum;
import com.stnts.bi.datamanagement.module.exportdata.dataenum.SecretTypeEnum;
import com.stnts.bi.datamanagement.module.exportdata.dataenum.SettlementTypeEnum;
import com.stnts.bi.datamanagement.module.feign.SysClient;
import com.stnts.bi.entity.common.PageEntity;
import com.stnts.bi.entity.sys.UserDmEntity;
import com.stnts.bi.entity.sys.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/3/1
 */
@Slf4j
@Service
public class ChannelServiceImpl extends ServiceImpl<ChannelMapper, Channel> implements ChannelService {
    private static final Logger logger = LoggerFactory.getLogger(ChannelServiceImpl.class);

    @Value("${data-management.setting.crm-channel-api-host}")
    private String crmChannelApiHost;
    @Value("${data-management.setting.crm-general-api-host}")
    private String crmGeneralApiHost;

    @Autowired
    private ChannelMapper channelMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ChannelPromotionMapper channelPromotionMapper;
    @Autowired
    private ChannelProductService channelProductService;
    @Autowired
    private ChannelCooperationMapper channelCooperationMapper;
    @Autowired
    private ChannelBaseIdService channelBaseIdService;
    @Autowired
    private BusinessDictService businessDictService;
    @Autowired
    private SysClient sysClient;
    @Autowired
    private ChannelChildMapper channelChildMapper;
    @Autowired
    private ChannelPromotionPositionMapper channelPromotionPositionMapper;
    @Autowired
    private CooperationService cooperationService;
    @Autowired
    private ChannelPromotionAllService channelPromotionAllService;
    @Autowired
    private CooperationBiService cooperationBiService;

    @Override
    public Channel saveChannel(Channel channel) {
        try {
            //校验
            check(channel);
            //参数处理
            paramCheck(channel);

            Long channelId = channelBaseIdService.getNewChannelID();
            channel.setChannelId(channelId);
            Boolean b = super.save(channel);
            channelBaseIdService.updateNewChannelID(channelId);
            return channel;
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException) {
                throw new BusinessException("该渠道名称已存在");
            } else if (e instanceof DataIntegrityViolationException) {
                throw new BusinessException("渠道名称2-50个字符");
            } else {
                throw e;
            }
        }
    }

    public void check(Channel channel) {
        List<Channel> channelList = new ArrayList<Channel>();
        //【渠道名称+公司类型+公司名称】共享渠道全局唯一，私有渠道部门下唯一
        if (channel.getSecretType() == 1) {
            channelList = channelMapper.selectList(new LambdaQueryWrapper<Channel>()
                    .eq(Channel::getChannelName, channel.getChannelName())
                    .eq(Channel::getCompanyId, channel.getCompanyId())
                    .and(i -> i.eq(Channel::getSecretType, 1).or().eq(Channel::getDepartmentCode, channel.getDepartmentCode()))
            );
        } else {
            channelList = channelMapper.selectList(new LambdaQueryWrapper<Channel>()
                    .eq(Channel::getChannelName, channel.getChannelName())
                    .eq(Channel::getCompanyId, channel.getCompanyId())
                    .eq(Channel::getDepartmentCode, channel.getDepartmentCode())
            );
        }
        if (CollectionUtil.isNotEmpty(channelList)) {
            String departmentNameStr = channelList.stream().map(i -> i.getDepartmentName()).collect(Collectors.joining(","));
            throw new BusinessException("在【" + departmentNameStr + "】下，渠道名称+公司名称+合作类型已存在，请直接使用。");
        }
    }

    public void checkUpdate(Channel channel) {
        List<Channel> channelList = new ArrayList<Channel>();
        //渠道名称共享渠道全局唯一，私有渠道部门下唯一
        if (channel.getSecretType() == 1) {
            channelList = channelMapper.selectList(new LambdaQueryWrapper<Channel>()
                    .eq(Channel::getChannelName, channel.getChannelName())
                    .eq(Channel::getCompanyId, channel.getCompanyId())
                    .and(i -> i.eq(Channel::getSecretType, 1).or().eq(Channel::getDepartmentCode, channel.getDepartmentCode()))
                    .ne(Channel::getChannelId, channel.getChannelId())
            );
        } else {
            channelList = channelMapper.selectList(new LambdaQueryWrapper<Channel>()
                    .eq(Channel::getChannelName, channel.getChannelName())
                    .eq(Channel::getCompanyId, channel.getCompanyId())
                    .eq(Channel::getDepartmentCode, channel.getDepartmentCode())
                    .ne(Channel::getChannelId, channel.getChannelId())
            );
        }
        if (CollectionUtil.isNotEmpty(channelList)) {
            String departmentNameStr = channelList.stream().map(i -> i.getDepartmentName()).collect(Collectors.joining(","));
            throw new BusinessException("在【" + departmentNameStr + "】下，渠道名称+公司名称+合作类型已存在，请直接使用。");
        }

        //如果当前内结渠道下绑定有内结CCID，则不能修改渠道【是否内结】状态
        if (StringUtils.isBlank(channel.getSettlementType())) {
            channel.setSettlementType("2");
        }
        Channel channelDB = channelMapper.selectOne(new LambdaQueryWrapper<Channel>().eq(Channel::getChannelId, channel.getChannelId()));
        Integer ccidCount = channelCooperationMapper.selectCount(new LambdaQueryWrapper<ChannelCooperation>().eq(ChannelCooperation::getChannelId, channel.getChannelId()));
        if ("1".equals(channelDB.getSettlementType()) && ccidCount > 0 && !channelDB.getSettlementType().equals(channel.getSettlementType())) {
            throw new BusinessException("渠道为内结状态，且有关联内结CCID，所以无法修改是否内结状态");
        }
    }

    public void paramCheck(Channel channel) {
        if (channel.getCompanyName().contains("（供应商）")) {
            channel.setCompanyName(channel.getCompanyName().replace("（供应商）", ""));
        } else if (channel.getCompanyName().contains("（客户）")) {
            channel.setCompanyName(channel.getCompanyName().replace("（客户）", ""));
        }

        if (StringUtils.isBlank(channel.getSettlementType())) {
            channel.setSettlementType("2");
        }
    }

    //CRM
    public void toCrm(Channel channel) {
        //如果修改了公司这个时候需要根据渠道关联的来源通知CRM
        Channel channelDB = channelMapper.selectOne(new QueryWrapper<Channel>().lambda().eq(Channel::getChannelId, channel.getChannelId()));
        if (!channelDB.getCompanyId().equals(channel.getCompanyId())) {
            String crmType = channelDB.getCrmType();
            String appId = "";
            String url = "";
            String secret = "";

            if (StringUtils.isNotBlank(crmType)) {
                if ("1".equals(crmType)) {
                    //渠道版CRM
                    appId = "channel_crm";
                    secret = "qd#53792";
                    url = crmChannelApiHost.concat("/api/outerapi/changeCompany.do");
                } else if ("2".equals(crmType)) {
                    //通用版CRM
                    appId = "game_crm";
                    secret = "yx#89456";
                    url = crmGeneralApiHost.concat("/admin-mng/outerapi/changeCompany.do");
                }

                Map<String, Object> map = MapUtil.<String, Object>builder()
                        .put("appId", appId)
                        .put("timestamp", System.currentTimeMillis())
                        .put("clientId", channelDB.getCustomerId())
                        .put("companyId", channel.getCompanyId())
                        .put("companyName", channel.getCompanyName())
                        .build();
                Map<String, Object> objectMap = map.entrySet().stream().filter(e -> ObjectUtil.isNotNull(e.getValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                Map<String, Object> params = MapUtil.newTreeMap(Comparator.comparing(String::toString));

                objectMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(entry -> params.put(entry.getKey(), entry.getValue()));

                String query = URLUtil.buildQuery(params, Charset.defaultCharset());

                log.info("通知CRM ==========> 加密参数：" + query.concat(secret));
                String sign = SecureUtil.md5(query.concat(secret));
                log.info("通知CRM ==========> sign：" + sign);
                params.put("sign", sign);

                try {
                    log.info("通知CRM ==========> 路径：" + url + "，参数：" + JSON.toJSONString(params));
                    String result = HttpUtil.post(url, params);
                    log.info("通知CRM ==========> 返回值" + result);
                    if (StrUtil.isNotEmpty(result)) {
                        JSONObject json = new JSONObject(result);
                        int status = json.getInt("code");
                        if (status != 200) {
                            logger.info("修改CRM公司名称，通知CRM失败：" + json.getStr("info"));
                        }
                    }
                } catch (Exception e) {
                    logger.info("修改CRM公司名称，通知CRM异常" + e.getMessage(), e);
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateChannel(Channel channel) {
        try {
            //校验
            checkUpdate(channel);
            //参数处理
            paramCheck(channel);
            //如果改公司名，则通知CRM
            toCrm(channel);

            super.updateById(channel);
            ChannelCooperation cc = new ChannelCooperation();
            cc.setAgentId(channel.getCompanyId());
            cc.setAgentName(channel.getCompanyName());
            cc.setChannelName(channel.getChannelName());
            cc.setChannelId(channel.getChannelId());
            /*cc.setDepartmentCode(channel.getDepartmentCode());
            cc.setDepartmentName(channel.getDepartmentName());*/
            channelCooperationMapper.updateChannel(cc);

            //修改PID表中
            //存宽表
            channelPromotionAllService.updateChannelThread(channel);

            return true;
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException) {
                throw new BusinessException("该渠道名称已存在");
            } else if (e instanceof DataIntegrityViolationException) {
                throw new BusinessException("渠道名称2-50个字符");
            } else {
                throw e;
            }
        }
    }

    @Override
    public boolean deleteChannel(Long id) throws Exception {
        //如果渠道关联了pid  则不能删除
        long countByChannelId = channelPromotionMapper.countByChannelId(id);
        if (countByChannelId > 0) {
            throw new BusinessException("渠道关联PID,无法删除");
        }
        Integer count = channelCooperationMapper.selectCount(new QueryWrapper<ChannelCooperation>().lambda().eq(ChannelCooperation::getChannelId, id));
        if (count > 0) {
            throw new BusinessException("渠道关联CCID,无法删除");
        }

        super.removeById(id);
        //删除对应子渠道
        channelChildMapper.delete(new LambdaQueryWrapper<ChannelChild>().eq(ChannelChild::getChannelId, id));
        //删除对应推广位
        channelPromotionPositionMapper.delete(new LambdaQueryWrapper<ChannelPromotionPosition>().eq(ChannelPromotionPosition::getChannelId, id));

        return true;
    }

    @Override
    public PageEntity<Channel> getChannelPageList(ChannelPageParam cParam, HttpServletRequest request) throws Exception {
        // 权限 By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        cParam.setDepartmentCodeAllList(departmentCodeAllList);
        Map<Integer, List<UserDmEntity>> mapAll = businessDictService.getCCIDAndPidByPermissions(user);
        cParam.setMapAll(mapAll);

        Page<Channel> page = new Page<>(cParam.getPageIndex(), cParam.getPageSize());
        List<OrderItem> orderItemList = CollectionUtil.isNotEmpty(cParam.getOrders()) ? cParam.getOrders() : new ArrayList<OrderItem>();
        orderColumn(orderItemList);
        page.setOrders(orderItemList);

        List<Channel> pageList = channelMapper.getPageList(page, cParam, user);

        List<Long> companyIdList = pageList.stream().map(Channel::getCompanyId).distinct().collect(Collectors.toList());
        Map<Long, Integer> cooperationMap = new HashMap<Long, Integer>();
        if (companyIdList.size() > 0) {
            List<Cooperation> cooperationList = cooperationService.list(new QueryWrapper<Cooperation>().lambda().in(Cooperation::getId, companyIdList));
            cooperationMap = cooperationList.stream().collect(Collectors.toMap(Cooperation::getId, s -> s.getCooperationType()));
        }

        List<Long> channelIdList = pageList.stream().map(Channel::getChannelId).distinct().collect(Collectors.toList());
        cParam.setChannelIdList(channelIdList);

        for (Channel param : pageList) {
            if (param.getCompanyId() != null) {
                Long companyIdLong = param.getCompanyId();
                if (cooperationMap.containsKey(companyIdLong)) {
                    String typeStr = cooperationMap.get(companyIdLong) == 1 ? "客户" : "供应商";
                    param.setCompanyName(param.getCompanyName() + "（" + typeStr + "）");
                }
            }
        }
        return new PageEntity<>(page, pageList);
    }

    public void orderColumn(List<OrderItem> orderItemList) {
        if (CollectionUtil.isNotEmpty(orderItemList)) {
            List<String> errorOrder = new ArrayList<String>();
            for (OrderItem orderItem : orderItemList) {
                if ("channelId".equals(orderItem.getColumn())) {
                    orderItem.setColumn("channel_id");
                    continue;
                } else if ("channelName".equals(orderItem.getColumn())) {
                    orderItem.setColumn("channel_name");
                    continue;
                } else if ("companyName".equals(orderItem.getColumn())) {
                    orderItem.setColumn("company_name");
                    continue;
                } else if ("departmentName".equals(orderItem.getColumn())) {
                    orderItem.setColumn("department_name");
                    continue;
                } else if ("secretType".equals(orderItem.getColumn())) {
                    orderItem.setColumn("secret_type");
                    continue;
                } else if ("subChannelNum".equals(orderItem.getColumn())) {
                    orderItem.setColumn("subChannelNum");
                    continue;
                } else if ("promotionPositionNum".equals(orderItem.getColumn())) {
                    orderItem.setColumn("promotionPositionNum");
                    continue;
                } else if ("ccIdNum".equals(orderItem.getColumn())) {
                    orderItem.setColumn("ccIdNum");
                    continue;
                } else if ("usernameName".equals(orderItem.getColumn())) {
                    orderItem.setColumn("username");
                    continue;
                } else if ("updateTime".equals(orderItem.getColumn())) {
                    orderItem.setColumn("update_time");
                    continue;
                }
                errorOrder.add(orderItem.getColumn());
            }

            if (CollectionUtil.isNotEmpty(errorOrder)) {
                throw new BusinessException("非法排序参数：" + org.apache.commons.lang.StringUtils.join(errorOrder, ","));
            }
        } else {
            OrderItem orderItem = new OrderItem();
            orderItem.setColumn("ca.channel_id");
            orderItem.setAsc(false);
            orderItemList.add(orderItem);
        }
    }

    @Override
    public List<Channel> getChannelList(ChannelPageParam channelPageParam, HttpServletRequest request) throws Exception {
        // 权限 By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        channelPageParam.setDepartmentCodeAllList(departmentCodeAllList);

        LambdaQueryWrapper<Channel> wrapper = getLambdaQueryWrapper(channelPageParam);
        List<Channel> channelList = channelMapper.selectList(wrapper);

        channelList = channelList.stream().map(i -> i.setSecretTypeStr(SecretTypeEnum.getByKey(i.getSecretType()).getValue())).collect(Collectors.toList());

        if (StrUtil.isNotEmpty(channelPageParam.getCcid())) {
            Channel channel = channelMapper.selectOne(new QueryWrapper<Channel>().lambda().inSql(Channel::getChannelId, "select channel_id from dm_channel_cooperation where ccid = '" + channelPageParam.getCcid() + "'"));
            if (null != channel && channelList.stream().map(Channel::getChannelId).noneMatch(id -> id.longValue() == channel.getChannelId())) {
                channelList.add(channel);
            }
        }
        return channelList;
    }

    @Override
    public List<Channel> getChannelListCRM(ChannelPageParam channelPageParam) throws Exception {
        LambdaQueryWrapper<Channel> wrapper = new LambdaQueryWrapper<Channel>()
                .isNull(Channel::getCustomerId)
                .eq(channelPageParam.getChannelId() != null, Channel::getChannelId, channelPageParam.getChannelId())
                .like(StringUtils.isNotBlank(channelPageParam.getChannelName()), Channel::getChannelName, channelPageParam.getChannelName())
                .eq(channelPageParam.getCompanyId() != null, Channel::getCompanyId, channelPageParam.getCompanyId());

        List<Channel> channelList = channelMapper.selectList(wrapper);
        return channelList;
    }

    @Override
    public List<Channel> getChannelList(ChannelPageParam channelPageParam) throws Exception {
        LambdaQueryWrapper<Channel> wrapper = getLambdaQueryWrapper(channelPageParam);
        List<Channel> channelList = channelMapper.selectList(wrapper);
        if (StrUtil.isNotEmpty(channelPageParam.getCcid())) {
            Channel channel = channelMapper.selectOne(new QueryWrapper<Channel>().lambda().inSql(Channel::getChannelId, "select channel_id from dm_channel_cooperation where ccid = '" + channelPageParam.getCcid() + "'"));
            if (null != channel && channelList.stream().map(Channel::getChannelId).noneMatch(id -> id.longValue() == channel.getChannelId())) {
                channelList.add(channel);
            }
        }
        return channelList;
    }

    @Override
    public Map<String, Object> searchList(String departmentCode, String companyId, Long channelId, Integer secretType, HttpServletRequest request) {
        // 权限 By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        Map<Integer, List<UserDmEntity>> mapAll = businessDictService.getCCIDAndPidByPermissions(user);

        ChannelPageParam channelPageParam = new ChannelPageParam();
        channelPageParam.setDepartmentCode(departmentCode);
        channelPageParam.setCompanyId(StringUtils.isBlank(companyId) ? null : Long.valueOf(companyId));
        channelPageParam.setChannelId(channelId);
        channelPageParam.setSecretType(secretType);
        channelPageParam.setDepartmentCodeAllList(departmentCodeAllList);
        channelPageParam.setMapAll(mapAll);

        List<Channel> channelList = channelMapper.secrchList(channelPageParam);

        Map<Long, Integer> cooperationMap = new HashMap<Long, Integer>();
        if (channelList != null && channelList.size() > 0) {
            List<Long> companyIdList = channelList.stream().map(Channel::getCompanyId).distinct().collect(Collectors.toList());
            List<Cooperation> cooperationList = cooperationService.list(new QueryWrapper<Cooperation>().lambda().in(Cooperation::getId, companyIdList));
            cooperationMap = cooperationList.stream().collect(Collectors.toMap(Cooperation::getId, s -> s.getCooperationType()));
        }

        //部门
        Set<Map<String, String>> departmentList = new HashSet<Map<String, String>>();
        //公司
        Set<Map<String, String>> companyList = new HashSet<Map<String, String>>();
        //渠道
        Set<Map<String, String>> channelMapList = new HashSet<Map<String, String>>();
        //保密类型
        Set<Map<String, String>> secretList = new HashSet<Map<String, String>>();
        for (Channel channel : channelList) {
            if (StringUtils.isNotBlank(channel.getDepartmentCode())) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("code", channel.getDepartmentCode());
                map.put("name", channel.getDepartmentName());
                departmentList.add(map);
            }
            if (channel.getCompanyId() != null) {
                Map<String, String> map = new HashMap<String, String>();
                String companyIdStr = channel.getCompanyId().toString();
                map.put("companyId", companyIdStr);
                if (cooperationMap.containsKey(channel.getCompanyId())) {
                    String cooperationType = cooperationMap.get(channel.getCompanyId()) == 1 ? "客户" : "供应商";
                    map.put("companyName", channel.getCompanyName() + "（" + cooperationType + "）");
                } else {
                    map.put("companyName", channel.getCompanyName());
                }
                companyList.add(map);
            }
            if (channel.getChannelId() != null) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("channelId", channel.getChannelId().toString());
                map.put("channelName", channel.getChannelName());
                channelMapList.add(map);
            }
            if (channel.getSecretType() != null) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("secretType", channel.getSecretType().toString());
                map.put("secretStr", channel.getSecretType() == 1 ? "共享" : "私有");//1(共享)、2(私有)
                secretList.add(map);
            }
        }

        departmentList = departmentList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(i -> i.get("code")))), HashSet::new));
        companyList = companyList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(i -> i.get("companyId")))), HashSet::new));
        channelMapList = channelMapList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(i -> i.get("channelId")))), HashSet::new));
        secretList = secretList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(i -> i.get("secretType")))), HashSet::new));

        Map<String, Object> mapAlls = new HashMap<String, Object>();
        mapAlls.put("department", departmentList);
        mapAlls.put("company", companyList);
        mapAlls.put("channel", channelMapList);
        mapAlls.put("secret", secretList);

        return mapAlls;
    }

    @Override
    public List<Channel> getChannelListGeneral(Channel channel) {
        LambdaQueryWrapper<Channel> wrapper = new LambdaQueryWrapper<Channel>()
                .eq(channel.getChannelId() != null, Channel::getChannelId, channel.getChannelId())
                .like(StringUtils.isNotBlank(channel.getChannelName()), Channel::getChannelName, channel.getChannelName())
                .eq(StringUtils.isNotBlank(channel.getDepartmentCode()), Channel::getDepartmentCode, channel.getDepartmentCode())
                .like(StringUtils.isNotBlank(channel.getDepartmentName()), Channel::getDepartmentName, channel.getDepartmentName())
                .eq(channel.getCompanyId() != null, Channel::getCompanyId, channel.getCompanyId())
                .like(StringUtils.isNotBlank(channel.getCompanyName()), Channel::getCompanyName, channel.getCompanyName())
                .ge(ObjectUtil.isNotEmpty(channel.getUpdateTimeStart()), Channel::getUpdateTime, channel.getUpdateTimeStart())
                .le(ObjectUtil.isNotEmpty(channel.getUpdateTimeEnd()), Channel::getUpdateTime, channel.getUpdateTimeEnd());
        List<Channel> channelList = channelMapper.selectList(wrapper);

        List<UserVO> userVOList = cooperationBiService.queryUserTree();
        Map<String, String> userVOMap = userVOList.stream().collect(Collectors.toMap(item -> String.valueOf(item.getId()), item -> item.getCardNumber()));

        channelList.stream().forEach(i -> i.setUserid(userVOMap.containsKey(String.valueOf(i.getUserid())) ? Long.valueOf(userVOMap.get(String.valueOf(i.getUserid()))) : 0l));

        return channelList;
    }

    @Override
    public Channel saveChannelGeneral(Channel channel) {
        //参数处理
        emptyParam(channel);
        //默认值处理
        defaultParam(channel);

        Channel channelRes = this.saveChannel(channel);

        channelRes = channelMapper.selectOne(new LambdaQueryWrapper<Channel>().eq(Channel::getChannelId, channelRes.getChannelId()));
        return channelRes;
    }

    /**
     * 当前 产品对应部门下 内结渠道 对应的 CCID
     *
     * @param productCodeList
     * @return
     */
    @Override
    public List<ChannelCooperation> getSettleCCIDByProd(List<String> productCodeList) {
        return channelMapper.getSettleCCIDByProd(productCodeList);
    }

    @Override
    public void updateCompanyName(CooperationAddApiParam cooperationAddApiParam) {
        channelMapper.updateCompanyName(Long.valueOf(cooperationAddApiParam.getCompanyId()), cooperationAddApiParam.getCompanyName());
    }

    public void defaultParam(Channel channel) {
        //公司名称
        if (StringUtils.isBlank(channel.getCompanyName())) {
            Cooperation cooperation = cooperationService.getById(channel.getCompanyId());
            if (ObjectUtil.isNotEmpty(cooperation)) {
                channel.setCompanyName(cooperation.getCompanyName());
            } else {
                throw new BusinessException("公司不存在");
            }
        }

        //部门CODE
        List<String> resultList = channelProductService.departmentCodeAndNameVaild(channel.getDepartmentCode(), channel.getDepartmentName());
        channel.setDepartmentCode(resultList.get(0));
        channel.setDepartmentName(resultList.get(1));

        channel.setUserid(0l);
        channel.setUsername("General");
    }

    public void emptyParam(Channel channel) {
        List<String> errorMsg = new ArrayList<String>();
        if (ObjectUtil.isNull(channel.getCompanyId())) {
            errorMsg.add("公司ID为空");
        }
        if (StringUtils.isBlank(channel.getChannelName())) {
            errorMsg.add("渠道名称为空");
        }
        if (StringUtils.isBlank(channel.getDepartmentCode()) && StringUtils.isBlank(channel.getDepartmentName())) {
            errorMsg.add("部门CODE和部门名称不可均为空");
        }
        if (StringUtils.isBlank(channel.getChannelType())) {
            channel.setChannelType("2");
        } else {
            ChannelTypeEnum channelTypeEnum = ChannelTypeEnum.getByKey(Integer.valueOf(channel.getChannelType()));
            if (ObjectUtil.isEmpty(channelTypeEnum)) {
                errorMsg.add("是否自营参数不正确");
            }
        }

        if (ObjectUtil.isEmpty(channel.getSecretType())) {
            channel.setSecretType(1);
        } else {
            SecretTypeEnum secretTypeEnum = SecretTypeEnum.getByKey(channel.getSecretType());
            if (ObjectUtil.isEmpty(secretTypeEnum)) {
                errorMsg.add("保密类型参数不正确");
            }
        }

        if (StringUtils.isBlank(channel.getSettlementType())) {
            channel.setSettlementType("2");
        } else {
            SettlementTypeEnum settlementTypeEnum = SettlementTypeEnum.getByKey(Integer.valueOf(channel.getSettlementType()));
            if (ObjectUtil.isEmpty(settlementTypeEnum)) {
                errorMsg.add("是否内结参数不正确");
            }
        }
        if (errorMsg.size() > 0) {
            throw new BusinessException(String.join(",", errorMsg));
        }
    }

    private LambdaQueryWrapper<Channel> getLambdaQueryWrapper(ChannelPageParam channelPageParam) {
        LambdaQueryWrapper<Channel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(channelPageParam.getChannelId() != null, Channel::getChannelId, channelPageParam.getChannelId());

        wrapper.and(StringUtils.isNotBlank(channelPageParam.getKeyword()), w -> w.like(Channel::getChannelName, channelPageParam.getKeyword())
                .or().like(Channel::getCompanyName, channelPageParam.getKeyword())
                .or().like(Channel::getDepartmentName, channelPageParam.getKeyword())
                .or(SecretTypeEnum.getByValue(channelPageParam.getKeyword()) != null, k -> k.eq(Channel::getSecretType, SecretTypeEnum.getByValue(channelPageParam.getKeyword()).getKey()))
        );

        List<String> departmentCodeList = channelPageParam.getDepartmentCodeAllList();
        wrapper.and(a -> a.eq(ObjectUtil.isNotNull(channelPageParam.getCompanyId()), Channel::getCompanyId, channelPageParam.getCompanyId())
                .and(b -> b.and(CollectionUtil.isNotEmpty(departmentCodeList), w -> w.in(Channel::getDepartmentCode, departmentCodeList).eq(Channel::getSecretType, 2))
                        .or(w -> w.eq(Channel::getSecretType, SecretTypeEnum.getByValue(channelPageParam.getKeyword()) != null ? SecretTypeEnum.getByValue(channelPageParam.getKeyword()).getKey() : 1)))
        );

        /*wrapper.and(departmentCodeList != null && departmentCodeList.size() > 0,
                w -> w.eq(ObjectUtil.isNotNull(channelPageParam.getCompanyId()), Channel::getCompanyId, channelPageParam.getCompanyId())
                        .in(departmentCodeList.size() > 0, Channel::getDepartmentCode, departmentCodeList)
                        .eq(departmentCodeList.size() > 0, Channel::getSecretType, 2))
                .or(w -> w.eq(ObjectUtil.isNotNull(channelPageParam.getCompanyId()), Channel::getCompanyId, channelPageParam.getCompanyId())
                        .eq(Channel::getSecretType, 1)
                );*/
        return wrapper;
    }
}
