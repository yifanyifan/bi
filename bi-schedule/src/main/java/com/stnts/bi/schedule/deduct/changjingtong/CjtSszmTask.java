package com.stnts.bi.schedule.deduct.changjingtong;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.stnts.bi.schedule.annatation.StntsScheduleAnnatation;
import com.stnts.bi.schedule.config.JdbcTemplateFactoryTwo;
import com.stnts.bi.schedule.deduct.vo.CjtSszmVO;
import com.stnts.bi.schedule.task.IScheduleTask;
import com.stnts.bi.schedule.util.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 每5分钟执行1次
 *
 * @author 易樊
 */
@Service
@StntsScheduleAnnatation(name = "CjtSszmTaskName", cron = "0 0/5 * * * ?", description = "为场景通推蜀山掌门/星空时代业务指标信息")
public class CjtSszmTask implements IScheduleTask {
    private static final Logger log = LoggerFactory.getLogger(CjtSszmTask.class);

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
    @Autowired
    private HttpUtils httpUtils;

    @Override
    public void executeInternal(JobExecutionContext jobExecutionContext) {
        String description = jobExecutionContext.getJobDetail().getDescription();

        Date date = new Date();
        try {
            Long startTime1 = System.currentTimeMillis();
            log.info("\n\n=============开始执行任务[" + description + "/SSZM]===========");
            CjtSszmVO cjtSszmVO1 = new CjtSszmVO();
            cjtSszmVO1.setPartitionDate(date);
            cjtSszmVO1.setModel("SSZM");
            start(cjtSszmVO1);
            Long endTime1 = System.currentTimeMillis();
            log.info("=============执行任务[" + description + "/SSZM]结束,耗时[" + (endTime1 - startTime1) + "]ms===========\n\n");
        } catch (Exception e) {
            log.info("=============执行任务[" + description + "/SSZM]异常" + e.getMessage(), e);

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("为场景通推蜀山掌门业务指标信息异常。\n")
                    .append("时间:").append(DateUtil.formatDate(date)).append(",\n")
                    .append("信息:").append(e.getMessage());
            httpUtils.alarm(stringBuffer.toString());
        }


        try {
            Long startTime2 = System.currentTimeMillis();
            log.info("\n\n=============开始执行任务[" + description + "/XKSD]===========");
            CjtSszmVO cjtSszmVO2 = new CjtSszmVO();
            cjtSszmVO2.setPartitionDate(date);
            cjtSszmVO2.setModel("XKSD");
            start(cjtSszmVO2);
            Long endTime2 = System.currentTimeMillis();
            log.info("=============执行任务[" + description + "/XKSD]结束,耗时[" + (endTime2 - startTime2) + "]ms===========\n\n");
        } catch (Exception e) {
            log.info("=============执行任务[" + description + "/XKSD]异常" + e.getMessage(), e);

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("为场景通推星空时代业务指标信息异常。\n")
                    .append("时间:").append(DateUtil.formatDate(date)).append(",\n")
                    .append("信息:").append(e.getMessage());
            httpUtils.alarm(stringBuffer.toString());
        }
    }

    /**
     * 开始
     *
     * @param cjtSszmVO
     */
    public void start(CjtSszmVO cjtSszmVO) {
        log.info("\n\n=============开始执行任务[" + cjtSszmVO.getModel() + "]===========");
        String dateStr = DateUtil.format(cjtSszmVO.getPartitionDate(), "yyyy-MM-dd");

        String querySQL = "select dog.partition_date as partition_date, dp.pid as pid, " +
                "case when (dp.ccid_settlement is not null and dp.ccid_settlement != '') then dp.ccid_settlement else dp.ccid end as ccid, " +
                "dp.product_code as product_code, dp.product_name as product_name, " +
                "case when (dp.channel_id_settlement is not null and dp.channel_id_settlement != 0) then dp.channel_id_settlement else dp.channel_id end as channel_id, " +
                "case when (dp.channel_name_settlement is not null and dp.channel_name_settlement != '') then dp.channel_name_settlement else dp.channel_name end as channel_name,  " +
                "dp.sub_channel_id as sub_channel_id, dp.sub_channel_name as sub_channel_name,dp.charge_rule as charge_rule,  " +
                "uniqExactArrayIf(dog.reg_arr,notEmpty(dog.reg_arr)) as reg_cnts, sum(dog.pay_total_money) as pay_money " +
                "from bi_gameop.dws_publish_global_game_agg_view dog  " +
                "left join bi_gameop.dim_pid dp on upper(dog.pid) = upper(dp.pid)  " +
                "where dog.partition_date ='" + dateStr + "' and dog.dim_type ='user' and dog.business='YFY' AND dog.game_code='" + cjtSszmVO.getModel() + "' and dog.channel_name != '内部测试' and dog.pid != 'NULL' ";
        // ================ 兼容重跑 Start ================
        if (CollectionUtil.isNotEmpty(cjtSszmVO.getPidList())) {
            String pidAgainStr = JSON.toJSONString(cjtSszmVO.getPidList());
            String agentIdStr = pidAgainStr.replace("[", "").replace("]", "");
            querySQL += " and dp.pid in (" + agentIdStr + ") ";
        } else if (CollectionUtil.isNotEmpty(cjtSszmVO.getCcidList())) {
            String ccidAgainStr = JSON.toJSONString(cjtSszmVO.getCcidList());
            String agentIdStr = ccidAgainStr.replace("[", "").replace("]", "");
            querySQL += " and ((dp.ccid_settlement is not null and dp.ccid_settlement != '' and dp.ccid_settlement in (" + agentIdStr + ")) or ((dp.ccid_settlement is null or dp.ccid_settlement = '') and dp.ccid in (" + agentIdStr + ")))";
        }
        if (CollectionUtil.isNotEmpty(cjtSszmVO.getChannelIdList())) {
            String channelIdAgainStr = JSON.toJSONString(cjtSszmVO.getChannelIdList());
            String channelIdIdStr = channelIdAgainStr.replace("[", "").replace("]", "");
            querySQL += " and dp.channel_id in (" + channelIdIdStr + ")";
        } else if (CollectionUtil.isNotEmpty(cjtSszmVO.getSubChannelIdList())) {
            String subChannelIdAgainStr = JSON.toJSONString(cjtSszmVO.getSubChannelIdList());
            String subChannelIdStr = subChannelIdAgainStr.replace("[", "").replace("]", "");
            querySQL += " and dp.sub_channel_id in (" + subChannelIdStr + ")";
        }

        querySQL += " group by dog.partition_date, dp.pid, dp.ccid, dp.ccid_settlement, dp.channel_id_settlement, dp.channel_name_settlement, dp.product_code, dp.product_name, dp.channel_id, dp.channel_name, dp.sub_channel_id, dp.sub_channel_name, dp.charge_rule HAVING  dp.pid != '' ";
        log.info("SSZM日数据 querySQL: " + querySQL);
        List<CjtSszmVO> cjtSszmVOList = JdbcTemplateFactoryTwo.getJdbcTemplateCommon(null, jdbcUrl, jdbcUsername, jdbcPassword).query(querySQL, new CjtSszmVO());

        toCJT(cjtSszmVOList, cjtSszmVO.getModel());
        log.info("\n\n=============结束执行任务[" + cjtSszmVO.getModel() + "]===========");
    }

    /**
     * 重跑
     *
     * @param cjtSszmVO
     */
    public void startAgain(CjtSszmVO cjtSszmVO) {
        Date start = cjtSszmVO.getPartitionDateStart();
        Date end = cjtSszmVO.getPartitionDateEnd();
        String model = cjtSszmVO.getModel();

        List<Date> dateList = com.stnts.bi.schedule.util.DateUtil.getBetweenDates(start, end);

        for (Date date : dateList) {
            log.info("=================" + model + "日数据重跑开始，日期：" + DateUtil.format(date, "yyyy-MM-dd HH:mm:ss"));
            cjtSszmVO.setPartitionDate(date);
            start(cjtSszmVO);
        }
    }

    /**
     * 推送数据
     *
     * @param cjtSszmVOList
     */
    public void toCJT(List<CjtSszmVO> cjtSszmVOList, String model) {
        log.info("cjtSszmVOList集合大小：" + cjtSszmVOList.size());
        if (CollectionUtil.isNotEmpty(cjtSszmVOList)) {
            JdbcTemplate jdbcTemplate = JdbcTemplateFactoryTwo.getJdbcTemplateCommon("com.mysql.jdbc.Driver", jdbcUrl2, jdbcUsername2, jdbcPassword2);

            String insertSQL = "INSERT INTO statistic_pid_report (`date`, pid, ccid, product_code, product_name, channel_id, channel_name, sub_channel_id, sub_channel_name, charge_rule, reg_count, pay_fee, created_at) VALUES ";

            StringBuilder insertSQLStart = new StringBuilder(insertSQL);
            for (int i = 1; i <= cjtSszmVOList.size(); i++) {
                CjtSszmVO cjtSszmVO = cjtSszmVOList.get(i - 1);

                if (StringUtils.isEmpty(cjtSszmVO.getProductCode()) || ObjectUtil.isEmpty(cjtSszmVO.getChannelId()) || cjtSszmVO.getChannelId() == 0) {
                    continue;
                }

                List<String> row = new ArrayList<String>();
                row.add(ObjectUtil.isNotEmpty(cjtSszmVO.getPartitionDate()) ? "'" + DateUtil.format(cjtSszmVO.getPartitionDate(), "yyyy-MM-dd") + "'" : "NULL");
                row.add(StringUtils.isNotBlank(cjtSszmVO.getPid()) ? "'" + cjtSszmVO.getPid() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(cjtSszmVO.getCcid()) ? "'" + cjtSszmVO.getCcid() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(cjtSszmVO.getProductCode()) ? "'" + cjtSszmVO.getProductCode() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(cjtSszmVO.getProductName()) ? "'" + cjtSszmVO.getProductName() + "'" : "NULL");
                row.add(ObjectUtil.isNotEmpty(cjtSszmVO.getChannelId()) ? "'" + cjtSszmVO.getChannelId() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(cjtSszmVO.getChannelName()) ? "'" + cjtSszmVO.getChannelName() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(cjtSszmVO.getSubChannelId()) ? "'" + cjtSszmVO.getSubChannelId() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(cjtSszmVO.getSubChannelName()) ? "'" + cjtSszmVO.getSubChannelName() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(cjtSszmVO.getChargeRule()) ? "'" + cjtSszmVO.getChargeRule() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(cjtSszmVO.getRegCnts()) ? "'" + cjtSszmVO.getRegCnts() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(cjtSszmVO.getPayMoney()) ? "'" + cjtSszmVO.getPayMoney() + "'" : "NULL");
                String rowStr = StringUtils.join(row, ",");
                insertSQLStart.append("(").append(rowStr).append(", now()),");
                if (i % 20 == 0 || i == cjtSszmVOList.size()) {
                    insertSQLStart = new StringBuilder(insertSQLStart.substring(0, insertSQLStart.length() - 1));

                    insertSQLStart.append(" on duplicate key update " +
                            "ccid = values(ccid), " +
                            "product_name = values(product_name), " +
                            "channel_id = values(channel_id), " +
                            "channel_name = values(channel_name), " +
                            "sub_channel_name = values(sub_channel_name), " +
                            "charge_rule = values(charge_rule), " +
                            "reg_count = values(reg_count), " +
                            "pay_fee = values(pay_fee)");

                    log.info(model + " 日数据（场景通MySQL）: 有20个新增" + insertSQLStart);
                    jdbcTemplate.execute(insertSQLStart.toString());
                    insertSQLStart = new StringBuilder(insertSQL);
                }
            }
        }
    }
}
