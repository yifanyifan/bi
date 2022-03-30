package com.stnts.bi.schedule.deduct.changjingtong;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import com.stnts.bi.schedule.annatation.StntsScheduleAnnatation;
import com.stnts.bi.schedule.config.JdbcTemplateFactoryTwo;
import com.stnts.bi.schedule.deduct.vo.*;
import com.stnts.bi.schedule.task.IScheduleTask;
import com.stnts.bi.schedule.util.MongoDBUtil;
import com.stnts.bi.schedule.util.TemplateHelper;
import com.stnts.bi.utils.BigDecimalUtils;
import com.stnts.bi.vo.CjtCCIDVO;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

/**
 * 每天凌晨10分计算
 *
 * @author 易樊
 */
@Service
@StntsScheduleAnnatation(name = "CjtDeductTaskName", cron = "0 30 3 ? * *", description = "场景通扣量计算")
public class CjtDeductTask implements IScheduleTask {
    private static final Logger log = LoggerFactory.getLogger(CjtDeductTask.class);

    @Value("${schedule.jdbc.ch.url}")
    private String jdbcUrl;
    @Value("${schedule.jdbc.ch.username}")
    private String jdbcUsername;
    @Value("${schedule.jdbc.ch.password}")
    private String jdbcPassword;

    @Value("${schedule.jdbc.cjt.url}")
    private String jdbcUrl2;
    @Value("${schedule.jdbc.cjt.username}")
    private String jdbcUsername2;
    @Value("${schedule.jdbc.cjt.password}")
    private String jdbcPassword2;

    @Value("${schedule.jdbc.hive.driver}")
    private String hiveDriver;
    @Value("${schedule.jdbc.hive.url}")
    private String hiveUrl;
    @Value("${schedule.jdbc.hive.username}")
    private String hiveUserName;
    @Value("${schedule.jdbc.hive.password}")
    private String hivePassWord;

    @Value("${schedule.jdbc.mongo.url}")
    private String mongoUrl;
    @Value("${schedule.jdbc.mongo.url02}")
    private String mongoUrl02;
    @Value("${schedule.jdbc.mongo.url03}")
    private String mongoUrl03;
    @Value("${schedule.jdbc.mongo.port}")
    private Integer mongoPort;
    @Value("${schedule.jdbc.mongo.username}")
    private String mongoUserName;
    @Value("${schedule.jdbc.mongo.authsource}")
    private String mongoSourceName;
    @Value("${schedule.jdbc.mongo.password}")
    private String mongoPassWord;
    @Value("${schedule.jdbc.mongo.dbname}")
    private String mongoDbName;

    @Value("${schedule.jdbc.mysql.url}")
    private String mysqlUrl;
    @Value("${schedule.jdbc.mysql.username}")
    private String mysqlUserName;
    @Value("${schedule.jdbc.mysql.password}")
    private String mysqlPassWord;

    @Autowired
    private MongoDBUtil mongoDBUtil;

    private ScheduledExecutorService pool = Executors.newScheduledThreadPool(15);

    @Override
    public void executeInternal(JobExecutionContext jobExecutionContext) {
        String description = jobExecutionContext.getJobDetail().getDescription();
        try {
            Long startTime = System.currentTimeMillis();
            log.info("\n\n=============开始执行任务[" + description + "]===========");
            CjtDeductAgainVO cjtDeductAgainVO = new CjtDeductAgainVO();
            cjtDeductAgainVO.setPartitionDate(DateUtil.yesterday());
            start(cjtDeductAgainVO);
            Long endTime = System.currentTimeMillis();
            log.info("=============执行任务[" + description + "]结束,耗时[" + (endTime - startTime) + "]ms===========\n\n");
        } catch (Exception e) {
            log.info("=============执行任务[" + description + "]异常" + e.getMessage(), e);
        }
    }

    public void startAgain(CjtDeductAgainVO cjtDeductAgainVO) throws Exception {
        Date start = cjtDeductAgainVO.getPartitionDateStart();
        Date end = cjtDeductAgainVO.getPartitionDateEnd();

        List<Date> dateList = com.stnts.bi.schedule.util.DateUtil.getBetweenDates(start, end);

        for (Date date : dateList) {
            log.info("=================开始重跑Insert：" + DateUtil.format(date, "yyyy-MM-dd HH:mm:ss"));
            cjtDeductAgainVO.setPartitionDate(date);
            start(cjtDeductAgainVO);
        }
    }

    public void start(CjtDeductAgainVO cjtDeductAgainVO) throws Exception {
        //1. 订单扣量
        log.info("\n\n=============开始执行任务[产品扣量]===========cjtDeductAgainVO：" + JSON.toJSONString(cjtDeductAgainVO));
        Long startTime01 = System.currentTimeMillis();
        List<OrderDeductVO> orderDeductVOList = orderDeductCommon01New(cjtDeductAgainVO);
        orderDeductCommon02New(orderDeductVOList);
        orderDeductCommon03New(orderDeductVOList, cjtDeductAgainVO);
        Long endTime01 = System.currentTimeMillis();
        log.info("=============执行任务[产品扣量]结束,耗时[" + (endTime01 - startTime01) + "]ms===========\n\n");

        //PID扣量+日收益计算
        deduct(cjtDeductAgainVO);
        log.info("=============> over");
    }

    /**
     * PID扣量+日收益计算
     *
     * @param cjtDeductAgainVO
     * @throws Exception
     */
    public void deduct(CjtDeductAgainVO cjtDeductAgainVO) throws Exception {
        //2. PID扣量
        Long startTime02 = System.currentTimeMillis();
        log.info("\n\n=============开始执行任务[PID扣量（接口）]===========cjtDeductAgainVO:" + JSON.toJSONString(cjtDeductAgainVO));
        List<PidDeductVO> pidDeductVOList = pidDeductCommon01New(cjtDeductAgainVO);
        pidDeductCommon02New(pidDeductVOList);
        Long endTime02 = System.currentTimeMillis();
        log.info("=============执行任务[PID扣量（接口）]结束,耗时[" + (endTime02 - startTime02) + "]ms===========\n\n");

        //3. 日预估收益dt_monitor_api_log
        Long startTime3 = System.currentTimeMillis();
        log.info("\n\n=============开始执行任务[日收益计算]===========pidDeductVOList.size():" + pidDeductVOList.size());
        dayDeductCommon01New(pidDeductVOList);
        dayDeductCommon02New(pidDeductVOList);
        Long endTime3 = System.currentTimeMillis();
        log.info("=============执行任务[日收益计算]结束,耗时[" + (endTime3 - startTime3) + "]ms===========\n\n");
    }

    /**
     * =========================================只更新 PID扣量 和 日收益=========================================
     */
    /**
     * 重跑（更新：PID扣量，日收益）
     *
     * @param cjtDeductAgainVO
     * @throws Exception
     */
    public void startAgainDeduct(CjtDeductAgainVO cjtDeductAgainVO) throws Exception {
        Date start = cjtDeductAgainVO.getPartitionDateStart();
        Date end = cjtDeductAgainVO.getPartitionDateEnd();

        List<Date> dateList = com.stnts.bi.schedule.util.DateUtil.getBetweenDates(start, end);

        for (Date date : dateList) {
            log.info("=================开始重跑Update：" + DateUtil.format(date, "yyyy-MM-dd HH:mm:ss"));
            cjtDeductAgainVO.setPartitionDate(date);

            start02(cjtDeductAgainVO);
        }
    }

    /**
     * 开始重跑Update，按天
     *
     * @param cjtDeductAgainVO
     * @throws Exception
     */
    public void start02(CjtDeductAgainVO cjtDeductAgainVO) throws Exception {
        //1. 订单扣量
        log.info("\n\n=============开始从场景通获取历史产品扣量数据[产品扣量]===========cjtDeductAgainVO：" + JSON.toJSONString(cjtDeductAgainVO));
        Long startTime01 = System.currentTimeMillis();

        Date date = cjtDeductAgainVO.getPartitionDate();
        String dateStr = DateUtil.format(date, "yyyy-MM-dd");

        // 2. 通过查场景通数据库，扣量后，未扣量订单统计（订单扣量后订单量、订单扣量后订单金额）
        String sql = "select ddo.reg_pid as pid, count(1) as countSum, cast(sum(ddo.pay_fee) as decimal(18,4)) as feeSum from deduction_daidai_order ddo " +
                "left join statistic_pid_report sp on upper(ddo.reg_pid) = upper(sp.pid) " +
                "where ddo.is_deduction != '1' and ddo.pay_fee is not null " +
                "and (ddo.ctime >= '" + dateStr + " 00:00:00' and ddo.ctime <= '" + dateStr + " 23:59:59') " +
                "and (sp.date >= '" + dateStr + " 00:00:00' and sp.date <= '" + dateStr + " 23:59:59') " +
                "and ddo.reg_pid is not null and ddo.reg_pid != '' ";
        // 兼容重跑
        if (CollectionUtil.isNotEmpty(cjtDeductAgainVO.getPidList())) {
            String pidAgainStr = JSON.toJSONString(cjtDeductAgainVO.getPidList());
            String agentIdStr = pidAgainStr.replace("[", "").replace("]", "");
            sql += " and ddo.reg_pid in (" + agentIdStr + ") ";
        } else if (CollectionUtil.isNotEmpty(cjtDeductAgainVO.getCcidList())) {
            String ccidAgainStr = JSON.toJSONString(cjtDeductAgainVO.getCcidList());
            String agentIdStr = ccidAgainStr.replace("[", "").replace("]", "");
            sql += " and sp.ccid in (" + agentIdStr + ")";
        }
        if (CollectionUtil.isNotEmpty(cjtDeductAgainVO.getChannelIdList())) {
            String channelIdAgainStr = JSON.toJSONString(cjtDeductAgainVO.getChannelIdList());
            String channelIdIdStr = channelIdAgainStr.replace("[", "").replace("]", "");
            sql += " and sp.channel_id in (" + channelIdIdStr + ")";
        } else if (CollectionUtil.isNotEmpty(cjtDeductAgainVO.getSubChannelIdList())) {
            String subChannelIdAgainStr = JSON.toJSONString(cjtDeductAgainVO.getSubChannelIdList());
            String subChannelIdStr = subChannelIdAgainStr.replace("[", "").replace("]", "");
            sql += " and sp.sub_channel_id in (" + subChannelIdStr + ")";
        }
        if (CollectionUtil.isNotEmpty(cjtDeductAgainVO.getProductCodeList())) {
            String productCodeListAgainStr = JSON.toJSONString(cjtDeductAgainVO.getProductCodeList());
            String productCodeListStr = productCodeListAgainStr.replace("[", "").replace("]", "");
            sql += " and sp.product_code in (" + productCodeListStr + ")";
        }

        sql += " group by ddo.reg_pid ";

        JdbcTemplate jdbcTemplate = JdbcTemplateFactoryTwo.getJdbcTemplateCommon("", jdbcUrl2, jdbcUsername2, jdbcPassword2);
        log.info("\n\n=============通过查场景通数据库，扣量后，未扣量订单统计 SQL：" + sql);
        List<CjtOrderDeductAgainVO> cjtOrderDeductAgainVOList = jdbcTemplate.query(sql, new CjtOrderDeductAgainVO());

        Map orderDeductMap = new HashMap();
        Map<String, String> originalCount = cjtOrderDeductAgainVOList.stream().collect(Collectors.toMap(item -> item.getPid(), item -> item.getCountSum()));
        Map<String, String> originalPrice = cjtOrderDeductAgainVOList.stream().collect(Collectors.toMap(item -> item.getPid(), item -> item.getFeeSum()));
        orderDeductMap.put("orderCount", originalCount);
        orderDeductMap.put("orderPrice", originalPrice);
        log.info("=====>orderDeductMap:" + orderDeductMap.size());
        cjtDeductAgainVO.setOrderDeductMap(orderDeductMap);

        Long endTime01 = System.currentTimeMillis();
        log.info("=============执行重跑任务[产品扣量]结束,耗时[" + (endTime01 - startTime01) + "]ms===========\n\n");

        //PID扣量+日收益计算
        deduct(cjtDeductAgainVO);
        log.info("=============> over");
    }

    /**
     * =========================================日预估扣量：这里重跑不需要考虑重跑参数CCID、PID，因为在上一层方法，已经过滤=========================================
     */
    /**
     * 开始日收益计算
     *
     * @param pidDeductVOList
     */
    public void dayDeductCommon01New(List<PidDeductVO> pidDeductVOList) {
        if (CollectionUtil.isNotEmpty(pidDeductVOList)) {
            // 1. 获取订单对应CCID
            List<String> pidList = pidDeductVOList.stream().map(i -> i.getPid().toUpperCase()).collect(Collectors.toList());
            String str = JSON.toJSONString(pidList).replace("[", "").replace("]", "");

            String sql = "select DISTINCT c.pid, c.ccid, c.channel_id, c.channel_name, c.charge_rule, c.channel_rate, c.channel_share, c.channel_share_step, c.price, c.channel_share_type from dm_channel_promotion_all c " +
                    "where upper(c.pid) in (" + str + ") and flag = '1'";
            log.info("\n\n=============日收益计算 SQL：" + sql);
            List<CjtCCIDVO> cjtPIDVOList = JdbcTemplateFactoryTwo.getJdbcTemplateCommon(null, mysqlUrl, mysqlUserName, mysqlPassWord).query(sql, new CjtCCIDVO());
            log.info("\n\n=============日收益计算 查询数量：" + cjtPIDVOList.size());
            Map<String, CjtCCIDVO> cjtPIDVOMap = cjtPIDVOList.stream().collect(Collectors.toMap(CjtCCIDVO::getPid, s -> s));

            // 2. 进行日收益计算
            doDayJS(pidDeductVOList, cjtPIDVOMap);
        }
    }

    /**
     * 开始日收益计算 落库，插入
     *
     * @param pidDeductVOList
     */
    public void dayDeductCommon02New(List<PidDeductVO> pidDeductVOList) {
        // 3. 插入
        dayDeductMongoNew(pidDeductVOList);
        dayDeductMysql(pidDeductVOList);
    }

    /**
     * 进行日收益计算
     *
     * @param pidDeductVOList
     * @param cjtCCIDVOMap
     */
    public void doDayJS(List<PidDeductVO> pidDeductVOList, Map<String, CjtCCIDVO> cjtCCIDVOMap) {
        for (PidDeductVO pidDeductVO : pidDeductVOList) {
            //原始日收益(元)
            String yssy = "0";
            //产品扣量后日收益
            String yssyOrder = "0";
            //PID扣量后日收益
            String yssyPid = "0";
            if (cjtCCIDVOMap.containsKey(pidDeductVO.getPid())) {
                CjtCCIDVO cjtCCIDVO = cjtCCIDVOMap.get(pidDeductVO.getPid());

                log.info("pidDeductVO:" + JSON.toJSONString(pidDeductVO));
                log.info("cjtCCIDVO:" + JSON.toJSONString(cjtCCIDVO));

                if ("CPS".equals(cjtCCIDVO.getChargeRule())) {
                    // 原始日收益
                    String fee = pidDeductVO.getPayFee();
                    log.info("fee:" + fee);
                    String base = getBaseByDay(cjtCCIDVO, fee);
                    log.info("base:" + base);
                    yssy = BigDecimalUtils.divByRoundHalfUp(BigDecimalUtils.mul(pidDeductVO.getPayFee(), base).toString(), "100", 2, BigDecimal.ROUND_DOWN);

                    // 直接使用产字品扣量后数乘以CCID（另：如果没有进行产品扣量，则用原始订单金额） -- 董华溢 2021127
                    String feeOrder = pidDeductVO.getOrderDeductFee();
                    String baseOrder = getBaseByDay(cjtCCIDVO, feeOrder);
                    yssyOrder = BigDecimalUtils.divByRoundHalfUp(BigDecimalUtils.mul(feeOrder, baseOrder).toString(), "100", 2, BigDecimal.ROUND_DOWN);

                    // 直接使用PID扣量后数字乘以CCID（另：如果没有进行PID扣量，则用产品扣量金额，如果没有进行产品扣量，则用原始订单金额） -- 董华溢 2021127
                    String feePid = pidDeductVO.getPidDeductFee();
                    String basePid = getBaseByDay(cjtCCIDVO, feePid);
                    yssyPid = BigDecimalUtils.divByRoundHalfUp(BigDecimalUtils.mul(feePid, basePid).toString(), "100", 2, BigDecimal.ROUND_DOWN);
                } else if ("CPA".equals(cjtCCIDVO.getChargeRule())) {
                    // 若CCID选CPA，如果有PID扣量，前两个一样，第3个日收益值不一样；如果没有PID扣量，3个值都一样 by donghuayi 20211117
                    // 只有PID扣量影响注册数
                    String base = getBaseByDay(cjtCCIDVO, pidDeductVO.getRegCount());
                    yssy = BigDecimalUtils.mul(pidDeductVO.getRegCount(), base, 2).toString();
                    yssyOrder = BigDecimalUtils.mul(pidDeductVO.getRegCount(), base, 2).toString();

                    //String feeOrder = StringUtils.isNotBlank(pidDeductVO.getChannelShareType()) ? pidDeductVO.getPidDeductRegCount() : pidDeductVO.getRegCount();
                    String feeOrder = pidDeductVO.getPidDeductRegCount();
                    String basePid = getBaseByDay(cjtCCIDVO, feeOrder);
                    yssyPid = BigDecimalUtils.mul(feeOrder, basePid, 2).toString();
                }
            }
            pidDeductVO.setDayEarn(yssy);
            pidDeductVO.setDayOrderEarn(yssyOrder);
            pidDeductVO.setDayPidEarn(yssyPid);
        }
    }

    /**
     * 进行日收益计算， 获取CCID百分比
     *
     * @param cjtCCIDVO
     * @param regBase
     * @return
     */
    public String getBaseByDay(CjtCCIDVO cjtCCIDVO, String regBase) {
        String base = "";

        if (ObjectUtil.isNotEmpty(cjtCCIDVO.getChannelShare())) {
            base = cjtCCIDVO.getChannelShare().toString();
        } else if (ObjectUtil.isNotEmpty(cjtCCIDVO.getChannelShareStep())) {
            // [{"num":["1","2"],"_reactId":0,"share":"3.00"},{"_reactId":"1ob6a7ed3cs","num":["4","5"],"share":"6.00"}]
            JSONArray jsonArray = JSONUtil.parseArray(cjtCCIDVO.getChannelShareStep());
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONArray jsonArraySub = JSONUtil.parseArray(jsonObject.get("num"));
                log.info("jsonArraySub:" + JSON.toJSONString(jsonArraySub));

                if (jsonArraySub.size() != 1) {
                    String start = jsonArraySub.getStr(0);
                    String end = jsonArraySub.getStr(1);

                    //TODO 若没有命中到，则默认使用最后一组数据
                    if ((i == jsonArray.size() - 1) || (BigDecimalUtils.compare(end, regBase) && BigDecimalUtils.compare(regBase, start))) {
                        base = jsonObject.getStr("share");
                        break;
                    }
                } else {
                    String start = jsonArraySub.getStr(0);
                    if ((i == jsonArray.size() - 1) || (BigDecimalUtils.compare(regBase, start))) {
                        base = jsonObject.getStr("share");
                        break;
                    }
                }

            }
        }

        return base;
    }

    /**
     * 进行日收益计算，落库，Mongo Insert
     *
     * @param pidDeductVOList
     */
    public void dayDeductMongoNew(List<PidDeductVO> pidDeductVOList) {
        if (CollectionUtil.isNotEmpty(pidDeductVOList)) {
            Long l1 = System.currentTimeMillis();

            pool.schedule(new Runnable() {
                @Override
                public void run() {
                    MongoDatabase mongoDatabase = mongoDBUtil.getConnect(mongoUrl, mongoUrl02, mongoUrl03, mongoPort, mongoUserName, mongoSourceName, mongoPassWord, mongoDbName);
                    MongoCollection mongoCollection = mongoDatabase.getCollection("deduct.day");
                    for (int i = 1; i <= pidDeductVOList.size(); i++) {
                        PidDeductVO pidDeductVO = pidDeductVOList.get(i - 1);

                        List<Bson> bsonList = new ArrayList<Bson>();
                        bsonList.add(eq("date", pidDeductVO.getDate()));
                        bsonList.add(eq("pid", pidDeductVO.getPid()));
                        bsonList.add(eq("productCode", pidDeductVO.getProductCode()));
                        bsonList.add(eq("subChannelId", pidDeductVO.getSubChannelId()));
                        Bson filter = and(bsonList);
                        Bson update = new Document("$set", new Document()
                                .append("date", pidDeductVO.getDate())
                                .append("pid", pidDeductVO.getPid())
                                .append("productCode", pidDeductVO.getProductCode())
                                .append("channelId", pidDeductVO.getChannelId())
                                .append("subChannelId", pidDeductVO.getSubChannelId())
                                .append("pc", pidDeductVO.getPc())
                                .append("uv", pidDeductVO.getUv())
                                .append("regCount", pidDeductVO.getRegCount())
                                .append("payCount", pidDeductVO.getPayCount())
                                .append("payFee", pidDeductVO.getPayFee())
                                .append("orderDeductCount", pidDeductVO.getOrderDeductCount())
                                .append("orderDeductFee", pidDeductVO.getOrderDeductFee())
                                .append("pidDeductCount", pidDeductVO.getPidDeductRegCount())
                                .append("pidDeductFee", pidDeductVO.getPidDeductFee())
                                .append("dayEarn", pidDeductVO.getDayEarn())
                                .append("dayOrderEarn", pidDeductVO.getDayOrderEarn())
                                .append("dayPidEarn", pidDeductVO.getDayPidEarn())
                                .append("createTime", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"))
                                .append("updateTime", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"))
                        );
                        UpdateResult result = mongoCollection.updateMany(filter, update, new UpdateOptions().upsert(true));
                    }

                    Long l2 = System.currentTimeMillis();
                    log.info("日收益计算（deduct.day）：有" + pidDeductVOList.size() + "个使用异步插入，耗时" + ((l2 - l1) / 1000) + "秒执行完成");
                }
            }, 500, TimeUnit.MILLISECONDS);
        } else {
            log.info("日收益计算（deduct.day）：没有需要插入的数据");
        }
    }

    /**
     * 进行日收益计算，落库，MySQL
     *
     * @param pidDeductVOList
     */
    public void dayDeductMysql(List<PidDeductVO> pidDeductVOList) {
        if (CollectionUtil.isNotEmpty(pidDeductVOList)) {
            JdbcTemplate jdbcTemplate = JdbcTemplateFactoryTwo.getJdbcTemplateCommon("com.mysql.jdbc.Driver", jdbcUrl2, jdbcUsername2, jdbcPassword2);
            String updateSQL = "update statistic_pid_report set deduction_pay_order_count = ?, deduction_pay_fee = ?, pid_deduction_reg_count = ?, pid_deduction_pay_fee = ?, " +
                    "income = ?, deduction_income = ?, pid_deduction_income = ?, updated_at = ? where date = ? and upper(pid) = upper(?) and product_code = ? and sub_channel_id = ?";

            List paramAll = new ArrayList<>();

            for (int i = 1; i <= pidDeductVOList.size(); i++) {
                PidDeductVO pidDeductVO = pidDeductVOList.get(i - 1);
                Object[] objects = new Object[]{pidDeductVO.getOrderDeductCount(), pidDeductVO.getOrderDeductFee(), pidDeductVO.getPidDeductRegCount(), pidDeductVO.getPidDeductFee(),
                        pidDeductVO.getDayEarn(), pidDeductVO.getDayOrderEarn(), pidDeductVO.getDayPidEarn(), new Date(), pidDeductVO.getDate(), pidDeductVO.getPid(), pidDeductVO.getProductCode(), pidDeductVO.getSubChannelId()
                };
                paramAll.add(objects);

                if (i % 20 == 0 || i == pidDeductVOList.size()) {
                    log.info("日收益计算（场景通MySQL）: 有20个更新" + JSON.toJSONString(paramAll));
                    jdbcTemplate.batchUpdate(updateSQL, paramAll);
                    paramAll = new ArrayList<>();
                }
            }
        } else {
            log.info("日收益计算（场景通MySQL）：没有需要更新的数据");
        }
    }

    /**
     * =========================================PID扣量=========================================
     */
    /**
     * 执行PID扣量
     *
     * @param cjtDeductAgainVO
     * @return
     * @throws Exception
     */
    public List<PidDeductVO> pidDeductCommon01New(CjtDeductAgainVO cjtDeductAgainVO) throws Exception {
        //参与订单扣量的PID集合
        String dateStr = DateUtil.format(cjtDeductAgainVO.getPartitionDate(), "yyyyMMdd");

        // 1. 查询 PID扣量数据
        String sql = "select DISTINCT h.dateNow, h.pid, h.ccid, h.product_code, h.product_name, h.channel_id, h.channel_name, h.sub_channel_id, h.sub_channel_name, h.charge_rule, h.pc, h.uv, h.reg_count, h.pay_fee, h.pay_order_count, h.channel_share_type, h.channel_fc_type, h.channel_share, h.channel_share_step, h.bdate, h.edate " +
                "from ( " +
                "select DISTINCT '" + dateStr + "' as dateNow, spr.pid, spr.ccid, spr.product_code, spr.product_name, spr.channel_id, spr.channel_name, spr.sub_channel_id, spr.sub_channel_name, spr.charge_rule, spr.pc, spr.uv, spr.reg_count,  " +
                "spr.pay_fee, spr.pay_order_count, pdrb.channel_share_type, pdrb.channel_fc_type, pdrb.channel_share, pdrb.channel_share_step, pdrb.bdate, pdrb.edate " +
                "from statistic_pid_report spr  " +
                "left join pid_deduction_range_bi pdrb on (upper(spr.pid) = upper(pdrb.pid) and STR_TO_DATE('" + dateStr + "','%Y%m%d') between pdrb.bdate and pdrb.edate) " +
                "where date_format(spr.`date`, '%Y%m%d') = '" + dateStr + "' ";
        // ================ 兼容重跑 Start ================
        if (CollectionUtil.isNotEmpty(cjtDeductAgainVO.getPidList())) {
            String pidAgainStr = JSON.toJSONString(cjtDeductAgainVO.getPidList());
            String agentIdStr = pidAgainStr.replace("[", "").replace("]", "");
            sql += " and spr.pid in (" + agentIdStr + ") ";
        } else if (CollectionUtil.isNotEmpty(cjtDeductAgainVO.getCcidList())) {
            String ccidAgainStr = JSON.toJSONString(cjtDeductAgainVO.getCcidList());
            String agentIdStr = ccidAgainStr.replace("[", "").replace("]", "");
            sql += " and spr.ccid in (" + agentIdStr + ")";
        }
        if (CollectionUtil.isNotEmpty(cjtDeductAgainVO.getChannelIdList())) {
            String channelIdAgainStr = JSON.toJSONString(cjtDeductAgainVO.getChannelIdList());
            String channelIdIdStr = channelIdAgainStr.replace("[", "").replace("]", "");
            sql += " and spr.channel_id in (" + channelIdIdStr + ")";
        } else if (CollectionUtil.isNotEmpty(cjtDeductAgainVO.getSubChannelIdList())) {
            String subChannelIdAgainStr = JSON.toJSONString(cjtDeductAgainVO.getSubChannelIdList());
            String subChannelIdStr = subChannelIdAgainStr.replace("[", "").replace("]", "");
            sql += " and spr.sub_channel_id in (" + subChannelIdStr + ")";
        }
        if (CollectionUtil.isNotEmpty(cjtDeductAgainVO.getProductCodeList())) {
            String productCodeListAgainStr = JSON.toJSONString(cjtDeductAgainVO.getProductCodeList());
            String productCodeListStr = productCodeListAgainStr.replace("[", "").replace("]", "");
            sql += " and spr.product_code in (" + productCodeListStr + ")";
        }
        // ================ 兼容重跑 End ================
        sql += "order by pdrb.updated_at desc " +
                ") h group by h.pid";

        JdbcTemplate jdbcTemplate = JdbcTemplateFactoryTwo.getJdbcTemplateCommon("", jdbcUrl2, jdbcUsername2, jdbcPassword2);
        log.info("\n\n=============PID扣量 SQL：" + sql);
        List<PidDeductVO> pidDeductVOList = jdbcTemplate.query(sql, new PidDeductVO());
        log.info("\n\n=============PID扣量 查询数量：" + pidDeductVOList.size());

        // 2. 获取场景通有哪些产品设置了扣量规则
        String gzSQL = "select distinct dofb.product_code from deduction_order_filter_bi dofb";
        List<String> productCodeGZList = jdbcTemplate.queryForList(gzSQL, String.class);
        log.info("\n\n=============PID扣量 规则表涉及产品：" + String.join(",", productCodeGZList));

        // 3. 补充产品扣量数量
        Map<String, String> orderCountMap = new HashMap<String, String>();
        Map<String, String> orderPriceMap = new HashMap<String, String>();
        Map<String, Map<String, String>> orderDeductMap = cjtDeductAgainVO.getOrderDeductMap();
        if (CollectionUtil.isNotEmpty(orderDeductMap)) {
            orderCountMap = orderDeductMap.get("orderCount");
            orderPriceMap = orderDeductMap.get("orderPrice");
        }
        // 所有PID产品扣量后的结果
        for (PidDeductVO pidDeductVO : pidDeductVOList) {
            // 1. 有产品扣量：若场景通设置了扣量规则，则使用扣量后的金额（有数则为数，没数则为PID下订单被扣完，则为0）；2. 没有产品扣量：如SSZM，如果没有产品扣量，则直接使用原始金额
            String zeroCount = productCodeGZList.contains(pidDeductVO.getProductCode()) ? "0" : pidDeductVO.getPayCount();
            String zeroFee = productCodeGZList.contains(pidDeductVO.getProductCode()) ? "0" : pidDeductVO.getPayFee();
            pidDeductVO.setOrderDeductCount(orderCountMap.containsKey(pidDeductVO.getPid()) ? orderCountMap.get(pidDeductVO.getPid()) : zeroCount);
            pidDeductVO.setOrderDeductFee(orderPriceMap.containsKey(pidDeductVO.getPid()) ? orderPriceMap.get(pidDeductVO.getPid()) : zeroFee);
        }

        // 3. 进行PID扣量
        doPIDDeduct(pidDeductVOList);

        return pidDeductVOList;
    }

    /**
     * 进行PID扣量
     *
     * @param pidDeductVOList
     */
    private void doPIDDeduct(List<PidDeductVO> pidDeductVOList) {
        for (PidDeductVO pidDeductVO : pidDeductVOList) {
            // PID扣量后的注册数
            String pidDeductRegCount = pidDeductVO.getRegCount();
            // PID扣量后订单金额
            String pidDeductFee = pidDeductVO.getOrderDeductFee();

            if ("1".equals(pidDeductVO.getChannelShareType())) { // CPS 流水
                // PID扣量，直接使用产品扣量金额（不存在产品扣量金额不存在的情况） -- 杨威20211122
                String base = getBaseByPID2(pidDeductVO, pidDeductVO.getOrderDeductFee());
                pidDeductFee = BigDecimalUtils.divByRoundHalfUp(BigDecimalUtils.mul(pidDeductVO.getOrderDeductFee(), BigDecimalUtils.sub("100", base).toString()).toString(), "100", 2, BigDecimal.ROUND_DOWN);
            } else if ("3".equals(pidDeductVO.getChannelShareType())) { // CPA 注册
                // 基于原始注册人数来计算PID扣量  -- 杨威20211119
                String base = getBaseByPID2(pidDeductVO, pidDeductVO.getRegCount());

                // 向下取整数 - by yangwei 20211118
                pidDeductRegCount = BigDecimalUtils.divByRoundHalfUp(BigDecimalUtils.mul(pidDeductVO.getRegCount(), BigDecimalUtils.sub("100", base).toString()).toString(), "100", 0, BigDecimal.ROUND_DOWN);
            }

            //注意： 如果没有设置PID扣量情况下，如果没有订单扣量，则取原始金额
            pidDeductVO.setPidDeductFee(pidDeductFee);
            pidDeductVO.setPidDeductRegCount(pidDeductRegCount);
        }
    }

    /**
     * 执行PID扣量， 获取基准费率扣量比例
     *
     * @param pidDeductVO
     * @param regBase
     * @return
     */
    public String getBaseByPID2(PidDeductVO pidDeductVO, String regBase) {
        String base = "";

        if ("1".equals(pidDeductVO.getChannelFcType())) {
            base = pidDeductVO.getChannelShare().toString();
        } else if ("2".equals(pidDeductVO.getChannelFcType())) {
            //[{"b":"0","n":36,"rate":80},{"b":36,"n":50,"rate":100},{"b":50,"n":"+∞","rate":20}]
            JSONArray jsonArray = JSONUtil.parseArray(pidDeductVO.getChannelShareStep());
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String start = jsonObject.getStr("b");
                String end = jsonObject.getStr("n");
                if ((i == jsonArray.size() - 1) || (BigDecimalUtils.compare(end, regBase) && BigDecimalUtils.compare(regBase, start))) {
                    base = jsonObject.getStr("rate");
                    break;
                }
            }
        }

        return base;
    }

    /**
     * 执行PID扣量后，落库，Mongo，新增
     *
     * @param pidDeductVOList
     */
    public void pidDeductCommon02New(List<PidDeductVO> pidDeductVOList) {
        if (CollectionUtil.isNotEmpty(pidDeductVOList)) {
            Long l1 = System.currentTimeMillis();
            pool.schedule(new Runnable() {
                @Override
                public void run() {
                    MongoDatabase mongoDatabase = mongoDBUtil.getConnect(mongoUrl, mongoUrl02, mongoUrl03, mongoPort, mongoUserName, mongoSourceName, mongoPassWord, mongoDbName);
                    MongoCollection mongoCollection = mongoDatabase.getCollection("deduct.pid");

                    for (int i = 1; i <= pidDeductVOList.size(); i++) {
                        PidDeductVO pidDeductVO = pidDeductVOList.get(i - 1);

                        List<Bson> bsonList = new ArrayList<Bson>();
                        bsonList.add(eq("date", pidDeductVO.getDate()));
                        bsonList.add(eq("pid", pidDeductVO.getPid()));
                        bsonList.add(eq("productCode", pidDeductVO.getProductCode()));
                        bsonList.add(eq("subChannelId", pidDeductVO.getSubChannelId()));
                        Bson filter = and(bsonList);
                        Bson update = new Document("$set", new Document()
                                .append("date", pidDeductVO.getDate())
                                .append("pid", pidDeductVO.getPid())
                                .append("productCode", pidDeductVO.getProductCode())
                                .append("channelId", pidDeductVO.getChannelId())
                                .append("subChannelId", pidDeductVO.getSubChannelId())
                                .append("pc", pidDeductVO.getPc())
                                .append("uv", pidDeductVO.getUv())
                                .append("regCount", pidDeductVO.getRegCount())
                                .append("payCount", pidDeductVO.getPayCount())
                                .append("payFee", pidDeductVO.getPayFee())
                                .append("orderDeductCount", pidDeductVO.getOrderDeductCount())
                                .append("orderDeductFee", pidDeductVO.getOrderDeductFee())
                                .append("pidDeductCount", pidDeductVO.getPidDeductRegCount())
                                .append("pidDeductFee", pidDeductVO.getPidDeductFee())
                                .append("createTime", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"))
                                .append("updateTime", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"))
                        );
                        UpdateResult result = mongoCollection.updateMany(filter, update, new UpdateOptions().upsert(true));
                    }

                    Long l2 = System.currentTimeMillis();
                    log.info("PID扣量（deduct.pid）：有" + pidDeductVOList.size() + "个使用异步插入，耗时" + ((l2 - l1) / 1000) + "秒执行完成");
                }
            }, 500, TimeUnit.MILLISECONDS);
        } else {
            log.info("PID扣量（deduct.pid）：没有需要插入的数据");
        }
    }

    /**
     * =========================================订单扣量==============================================
     */

    /**
     * 开始订单扣量
     *
     * @param cjtDeductAgainVO
     * @return
     */
    public List<OrderDeductVO> orderDeductCommon01New(CjtDeductAgainVO cjtDeductAgainVO) {
        String dateStr = DateUtil.format(cjtDeductAgainVO.getPartitionDate(), "yyyyMMdd");

        // 1. 从 hive 中获取信息
        Template template = TemplateHelper.getDeductTemplateEngine().getTemplate("daidai_order_deduct.sql");
        Dict dict = Dict.create().set("dateStr", dateStr);
        String sql = template.render(dict);

        // 兼容重跑
        if (CollectionUtil.isNotEmpty(cjtDeductAgainVO.getCcidList())) {
            String ccidAgainStr = JSON.toJSONString(cjtDeductAgainVO.getCcidList());
            String agentIdStr = ccidAgainStr.replace("[", "").replace("]", "");
            sql += " and c.ccid in (" + agentIdStr + ")";
        } else if (CollectionUtil.isNotEmpty(cjtDeductAgainVO.getPidList())) {
            String pidAgainStr = JSON.toJSONString(cjtDeductAgainVO.getPidList());
            String agentIdStr = pidAgainStr.replace("[", "").replace("]", "");
            sql += " and c.pid in (" + agentIdStr + ")";
        }
        if (CollectionUtil.isNotEmpty(cjtDeductAgainVO.getChannelIdList())) {
            String channelIdAgainStr = JSON.toJSONString(cjtDeductAgainVO.getChannelIdList());
            String channelIdIdStr = channelIdAgainStr.replace("[", "").replace("]", "");
            sql += " and c.channel_id in (" + channelIdIdStr + ")";
        } else if (CollectionUtil.isNotEmpty(cjtDeductAgainVO.getSubChannelIdList())) {
            String subChannelIdAgainStr = JSON.toJSONString(cjtDeductAgainVO.getSubChannelIdList());
            String subChannelIdStr = subChannelIdAgainStr.replace("[", "").replace("]", "");
            sql += " and c.sub_channel_id in (" + subChannelIdStr + ")";
        }

        log.info("\n\n=============产品扣量SQL：" + sql);
        List<OrderDeductVO> orderDeductVOList = JdbcTemplateFactoryTwo.getJdbcTemplateCommon(hiveDriver, hiveUrl, hiveUserName, hivePassWord).query(sql, new OrderDeductVO());
        log.info("\n\n=============产品扣量查询数量：" + orderDeductVOList.size());

        // 2. 执行扣量
        doOrderDeduct(orderDeductVOList, dateStr);

        return orderDeductVOList;
    }

    /**
     * 执行订单扣量
     *
     * @param orderDeductVOList
     * @param dateStr
     */
    public void doOrderDeduct(List<OrderDeductVO> orderDeductVOList, String dateStr) {
        // 2. 获取扣量信息
        JdbcTemplate jdbcTemplate = JdbcTemplateFactoryTwo.getJdbcTemplateCommon("com.mysql.jdbc.Driver", jdbcUrl2, jdbcUsername2, jdbcPassword2);
        // 2.1 黑名单 + “媒体商ID集合 + CCID集合”（通过产品CODE）
        String getMsgUid = "select spr.product_code, spr.uids, spr.agent_ids, spr.ccid from deduction_order_filter_bi spr where spr.product_code = '320000'";
        List<DeductionUidBi> deductionUidBiList = jdbcTemplate.query(getMsgUid, new DeductionUidBi());
        Map<String, DeductionUidBi> deductionUidBiMap = deductionUidBiList.stream().collect(Collectors.toMap(DeductionUidBi::getProductCode, s -> s));
        log.info("\n\n=============产品扣量，黑名单规则：" + JSON.toJSONString(deductionUidBiList));

        // 2.2 规则（通过产品CODE + 有效期开始时间 + 有效期结束时间）
        String getMsgRange = "select product_code, bdate, edate, detail, updated_at from deduction_range_bi spr  where product_code = '320000' and STR_TO_DATE(" + dateStr + ",'%Y%m%d') between bdate and edate order by id desc";
        List<DeductionRangeBi> deductionRangeBiList = jdbcTemplate.query(getMsgRange, new DeductionRangeBi());
        log.info("\n\n=============产品扣量，扣量规则：" + JSON.toJSONString(deductionRangeBiList));

        // 3. 执行扣量
        // 3.1 黑名单
        if (CollectionUtil.isNotEmpty(deductionUidBiList)) {
            List<String> deductionUidBiListAll = new ArrayList<String>();
            for (DeductionUidBi deductionUidBi : deductionUidBiList) {
                if (StringUtils.isNotBlank(deductionUidBi.getUids())) {
                    deductionUidBiListAll.addAll(JSONUtil.parseArray(deductionUidBi.getUids()).toList(String.class));
                }
            }
            orderDeductVOList.stream().forEach(i -> {
                if (deductionUidBiListAll.contains(i.getUId())) {
                    i.setDeductStatus("1");
                } else {
                    i.setDeductStatus("2");
                }
            });
        }
        Long nul = orderDeductVOList.stream().map(i -> "1".equals(i.getDeductStatus())).count();
        // 3.2 范围
        if (CollectionUtil.isNotEmpty(deductionRangeBiList)) {
            DeductionRangeBi deductionRangeBi = deductionRangeBiList.get(0); // 同一天，同一个产品下，正常情况仅只会取1条规则（最多两条：当出现有效期交叉时，会有两条，这时取update最新一条规则） by yangwei 2021-11-16

            if (deductionUidBiMap.containsKey(deductionRangeBi.getProductCode())) {
                DeductionUidBi deductionUidBi = deductionUidBiMap.get(deductionRangeBi.getProductCode());

                String agentIdStr = deductionUidBi.getAgentIds().replace("[", ",").replace("]", ",").replace("\"", "");
                String ccidStr = deductionUidBi.getCcid().replace("[", ",").replace("]", ",").replace("\"", "");
                JSONArray detailArray = JSONUtil.parseArray(deductionRangeBi.getDetail());

                orderDeductVOList.stream().forEach(i -> {
                    if (Pattern.matches(".*," + i.getChannelId() + ",.*", agentIdStr) || Pattern.matches(".*," + i.getRegCCID() + ",.*", ccidStr)) {
                        for (int j = 0; j < detailArray.size(); j++) {
                            JSONObject jsonObject = JSONUtil.parseObj(detailArray.get(j));
                            String b = jsonObject.getStr("b");
                            String n = jsonObject.getStr("n");
                            if ((j == detailArray.size() - 1) || (BigDecimalUtils.compare(i.getPayFee(), b) && BigDecimalUtils.compare(n, i.getPayFee()))) {
                                // 随机扣量
                                String rate = jsonObject.getStr("rate");
                                if (RandomUtils.nextInt(1, 101) <= Integer.valueOf(rate)) {
                                    i.setDeductStatus("1");
                                } else {
                                    i.setDeductStatus("2");
                                }
                                break;
                            }
                        }
                    } else {
                        i.setDeductStatus("2");
                    }
                });
            }
        }
        Long nul2 = orderDeductVOList.stream().map(i -> "1".equals(i.getDeductStatus())).count();
        log.info("\n\n=============产品扣量，黑名单共计扣量：" + nul + "个订单，规则共计扣量：" + (nul2 - nul) + "个订单，共计：" + nul2 + "个订单");
    }

    /**
     * 订单扣量后，落库
     *
     * @param orderDeductVOList
     */
    public void orderDeductCommon02New(List<OrderDeductVO> orderDeductVOList) {
        // 3. 扣量结果输出到mongo
        orderDeductMongoNew(orderDeductVOList);
        orderDeductMysqlNew(orderDeductVOList);
    }

    /**
     * 订单扣量后，落库，Mongo
     *
     * @param orderDeductVOList
     */
    public void orderDeductMongoNew(List<OrderDeductVO> orderDeductVOList) {
        if (CollectionUtil.isNotEmpty(orderDeductVOList)) {
            Long l1 = System.currentTimeMillis();
            pool.schedule(new Runnable() {
                @Override
                public void run() {
                    MongoDatabase mongoDatabase = mongoDBUtil.getConnect(mongoUrl, mongoUrl02, mongoUrl03, mongoPort, mongoUserName, mongoSourceName, mongoPassWord, mongoDbName);
                    MongoCollection mongoCollection = mongoDatabase.getCollection("deduct.order");
                    for (int i = 1; i <= orderDeductVOList.size(); i++) {
                        OrderDeductVO orderDeductVO = orderDeductVOList.get(i - 1);

                        List<Bson> bsonList = new ArrayList<Bson>();
                        bsonList.add(eq("orderId", orderDeductVO.getOrderId()));
                        Bson filter = and(bsonList);
                        Bson update = new Document("$set", new Document()
                                .append("orderId", orderDeductVO.getOrderId())
                                .append("uId", orderDeductVO.getUId())
                                .append("payFee", orderDeductVO.getPayFee())
                                .append("payType", orderDeductVO.getPayType())
                                .append("cTime", orderDeductVO.getCTime())
                                .append("payPid", orderDeductVO.getPayPid())
                                .append("regPid", orderDeductVO.getRegPid())
                                .append("productCode", orderDeductVO.getProductCode())
                                .append("productName", orderDeductVO.getProductName())
                                .append("channelId", orderDeductVO.getChannelId())
                                .append("channelName", orderDeductVO.getChannelName())
                                .append("subChannelId", orderDeductVO.getSubChannelId())
                                .append("subChannelName", orderDeductVO.getSubChannelName())
                                .append("regCCID", orderDeductVO.getRegCCID())
                                .append("deductStatus", orderDeductVO.getDeductStatus())
                                .append("createTime", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"))
                                .append("updateTime", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"))
                        );
                        UpdateResult result = mongoCollection.updateMany(filter, update, new UpdateOptions().upsert(true));
                    }

                    Long l2 = System.currentTimeMillis();
                    log.info("带带订单扣量（deduct.order）：有" + orderDeductVOList.size() + "个使用异步插入，耗时" + ((l2 - l1) / 1000) + "秒执行完成");
                }
            }, 500, TimeUnit.MILLISECONDS);
        } else {
            log.info("带带订单扣量（deduct.order）：无需要插入的数据");
        }
    }

    /**
     * 订单扣量后，落库，MySQL
     *
     * @param orderDeductVOList
     */
    public void orderDeductMysqlNew(List<OrderDeductVO> orderDeductVOList) {
        if (CollectionUtil.isNotEmpty(orderDeductVOList)) {
            JdbcTemplate jdbcTemplate = JdbcTemplateFactoryTwo.getJdbcTemplateCommon("com.mysql.jdbc.Driver", jdbcUrl2, jdbcUsername2, jdbcPassword2);
            String updateSQL = "INSERT INTO deduction_daidai_order (order_id, ctime, pay_fee, pay_type, uid, pay_pid, reg_pid, reg_ccid, product_code, product_name, channel_id, sub_channel_id, is_deduction, " +
                    "type_deduction, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now()) " +
                    "on duplicate key update " +
                    "ctime = values(ctime), " +
                    "pay_fee = values(pay_fee), " +
                    "pay_type = values(pay_type), " +
                    "uid = values(uid), " +
                    "pay_pid = values(pay_pid), " +
                    "reg_pid = values(reg_pid), " +
                    "reg_ccid = values(reg_ccid), " +
                    "product_code = values(product_code), " +
                    "product_name = values(product_name), " +
                    "channel_id = values(channel_id), " +
                    "sub_channel_id = values(sub_channel_id), " +
                    "is_deduction = values(is_deduction), " +
                    "type_deduction = values(type_deduction) ";
            List paramAll = new ArrayList<>();

            for (int i = 1; i <= orderDeductVOList.size(); i++) {
                OrderDeductVO orderDeductVO = orderDeductVOList.get(i - 1);
                Object[] objects = new Object[]{orderDeductVO.getOrderId(), orderDeductVO.getCTime(), orderDeductVO.getPayFee(), orderDeductVO.getPayType(), orderDeductVO.getUId(), orderDeductVO.getPayPid(),
                        orderDeductVO.getRegPid(), orderDeductVO.getRegCCID(), orderDeductVO.getProductCode(), orderDeductVO.getProductName(), orderDeductVO.getChannelId(), orderDeductVO.getSubChannelId(),
                        orderDeductVO.getDeductStatus(), "1"};
                paramAll.add(objects);

                if (i % 20 == 0 || i == orderDeductVOList.size()) {
                    log.info("订单扣量（场景通MySQL）: 有20个插入" + JSON.toJSONString(paramAll));
                    jdbcTemplate.batchUpdate(updateSQL, paramAll);
                    paramAll = new ArrayList<>();
                }
            }
        } else {
            log.info("订单扣量（场景通MySQL）：没有需要更新的数据");
        }
    }

    /**
     * 订单扣量后，订单量、订单金额 统计
     *
     * @param orderDeductVOList
     * @param cjtDeductAgainVO
     */
    public void orderDeductCommon03New(List<OrderDeductVO> orderDeductVOList, CjtDeductAgainVO cjtDeductAgainVO) {
        // 4. 扣量后，未扣量订单统计（订单扣量后订单量、订单扣量后订单金额）
        Map<String, Map<String, String>> orderDeductMap = new HashMap<String, Map<String, String>>();

        Map<String, List<OrderDeductVO>> orderDeductVOMap = orderDeductVOList.stream().filter(i -> !"1".equals(i.getDeductStatus())).collect(Collectors.groupingBy(OrderDeductVO::getRegPid));
        Map<String, String> originalCount = new HashMap<String, String>();
        Map<String, String> originalPrice = new HashMap<String, String>();
        for (Map.Entry<String, List<OrderDeductVO>> entry : orderDeductVOMap.entrySet()) {
            String key = entry.getKey();
            List<OrderDeductVO> value = entry.getValue();

            String countAll = String.valueOf(value.size());
            String priceAll = "0";
            for (OrderDeductVO orderDeductVO : value) {
                priceAll = BigDecimalUtils.add(priceAll, orderDeductVO.getPayFee()).toString();
            }
            originalCount.put(key, countAll);
            originalPrice.put(key, priceAll);
        }
        orderDeductMap.put("orderCount", originalCount);
        orderDeductMap.put("orderPrice", originalPrice);

        cjtDeductAgainVO.setOrderDeductMap(orderDeductMap);
    }

}
