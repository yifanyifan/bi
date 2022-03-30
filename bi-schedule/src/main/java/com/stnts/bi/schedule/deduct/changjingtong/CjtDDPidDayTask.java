package com.stnts.bi.schedule.deduct.changjingtong;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.template.Template;
import com.alibaba.fastjson.JSON;
import com.stnts.bi.schedule.annatation.StntsScheduleAnnatation;
import com.stnts.bi.schedule.config.JdbcTemplateFactoryTwo;
import com.stnts.bi.schedule.deduct.vo.CjtSszmVO;
import com.stnts.bi.schedule.deduct.vo.PidDayVO;
import com.stnts.bi.schedule.task.IScheduleTask;
import com.stnts.bi.schedule.util.TemplateHelper;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 每天凌晨2点30分运行
 *
 * @author 易樊
 */
@Service
@StntsScheduleAnnatation(name = "CjtDDPidDayTaskName", cron = "0 30 2 ? * *", description = "将前一天带带PID日数据推到场景通")
public class CjtDDPidDayTask implements IScheduleTask {
    private static final Logger log = LoggerFactory.getLogger(CjtDDPidDayTask.class);

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

    @Override
    public void executeInternal(JobExecutionContext jobExecutionContext) {
        String description = jobExecutionContext.getJobDetail().getDescription();
        try {
            Long startTime = System.currentTimeMillis();
            log.info("\n\n=============开始执行任务[" + description + "]===========");
            CjtSszmVO cjtSszmVO = new CjtSszmVO();
            cjtSszmVO.setPartitionDate(DateUtil.yesterday());
            start(cjtSszmVO);
            Long endTime = System.currentTimeMillis();
            log.info("=============执行任务[" + description + "]结束,耗时[" + (endTime - startTime) + "]ms===========\n\n");
        } catch (Exception e) {
            log.info("=============执行任务[" + description + "]异常" + e.getMessage(), e);
        }
    }

    public void startAgain(CjtSszmVO cjtSszmVO) {
        Date start = cjtSszmVO.getPartitionDateStart();
        Date end = cjtSszmVO.getPartitionDateEnd();

        List<Date> dateList = com.stnts.bi.schedule.util.DateUtil.getBetweenDates(start, end);

        for (Date date : dateList) {
            log.info("=================DD日数据重跑开始，时间：" + DateUtil.format(date, "yyyy-MM-dd HH:mm:ss"));
            cjtSszmVO.setPartitionDate(date);

            start(cjtSszmVO);
        }
    }

    public void start(CjtSszmVO cjtSszmVO) {
        String dateStr = DateUtil.format(cjtSszmVO.getPartitionDate(), "yyyyMMdd");

        // 1. 获取数据
        Template template = TemplateHelper.getDeductTemplateEngine().getTemplate("daidai_dayly_data.sql");
        Dict dict = Dict.create().set("dateStr", dateStr);
        String sql = template.render(dict);

        // 将为0的数据也全部推场景通 2021-12-28
        // + "and p.channel_id is not null and p.sub_channel_id is not null and p.sub_channel_id != '' ";
        // ================ 兼容重跑 Start ================
        if (CollectionUtil.isNotEmpty(cjtSszmVO.getPidList())) {
            String pidAgainStr = JSON.toJSONString(cjtSszmVO.getPidList());
            String agentIdStr = pidAgainStr.replace("[", "").replace("]", "");
            sql += " and p.pid in (" + agentIdStr + ") ";
        } else if (CollectionUtil.isNotEmpty(cjtSszmVO.getCcidList())) {
            String ccidAgainStr = JSON.toJSONString(cjtSszmVO.getCcidList());
            String agentIdStr = ccidAgainStr.replace("[", "").replace("]", "");
            sql += " and ((p.ccid_settlement is not null and p.ccid_settlement != '' and p.ccid_settlement in (" + agentIdStr + ")) or ((p.ccid_settlement is null or p.ccid_settlement = '') and p.ccid in (" + agentIdStr + ")))";
        }
        if (CollectionUtil.isNotEmpty(cjtSszmVO.getChannelIdList())) {
            String channelIdAgainStr = JSON.toJSONString(cjtSszmVO.getChannelIdList());
            String channelIdIdStr = channelIdAgainStr.replace("[", "").replace("]", "");
            sql += " and p.channel_id in (" + channelIdIdStr + ")";
        } else if (CollectionUtil.isNotEmpty(cjtSszmVO.getSubChannelIdList())) {
            String subChannelIdAgainStr = JSON.toJSONString(cjtSszmVO.getSubChannelIdList());
            String subChannelIdStr = subChannelIdAgainStr.replace("[", "").replace("]", "");
            sql += " and p.sub_channel_id in (" + subChannelIdStr + ")";
        }
        log.info("\n\n=============CjtDDPidDayTask，QuerySQL：" + sql);
        List<PidDayVO> pidDayVOList = JdbcTemplateFactoryTwo.getJdbcTemplateCommon(hiveDriver, hiveUrl, hiveUserName, hivePassWord).query(sql, new BeanPropertyRowMapper<PidDayVO>(PidDayVO.class));
        log.info("\n\n=============CjtDDPidDayTask，QuerySQLResult：" + pidDayVOList.size());

        toCJT(pidDayVOList);
    }

    public void toCJT(List<PidDayVO> pidDayVOList) {
        log.info("pidDayVOList集合大小：" + pidDayVOList.size());
        if (CollectionUtil.isNotEmpty(pidDayVOList)) {
            JdbcTemplate jdbcTemplate = JdbcTemplateFactoryTwo.getJdbcTemplateCommon("com.mysql.jdbc.Driver", jdbcUrl2, jdbcUsername2, jdbcPassword2);

            String insertSQL = "INSERT INTO statistic_pid_report (`date`, pid, ccid, product_name, product_code, channel_id, channel_name, sub_channel_id, sub_channel_name, charge_rule, pc, uv, reg_count, pay_order_count, pay_fee, created_at) values ";

            StringBuilder insertSQLStart = new StringBuilder(insertSQL);
            for (int i = 1; i <= pidDayVOList.size(); i++) {
                PidDayVO pidDayVO = pidDayVOList.get(i - 1);

                List<String> row = new ArrayList<String>();
                row.add(ObjectUtil.isNotEmpty(pidDayVO.getDateNow()) ? pidDayVO.getDateNow() : "NULL");
                row.add(StringUtils.isNotBlank(pidDayVO.getPid()) ? "'" + pidDayVO.getPid() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(pidDayVO.getCcid()) ? "'" + pidDayVO.getCcid() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(pidDayVO.getProductName()) ? "'" + pidDayVO.getProductName() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(pidDayVO.getProductCode()) ? "'" + pidDayVO.getProductCode() + "'" : "NULL");
                row.add(ObjectUtil.isNotEmpty(pidDayVO.getChannelId()) ? "'" + pidDayVO.getChannelId() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(pidDayVO.getChannelName()) ? "'" + pidDayVO.getChannelName() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(pidDayVO.getSubChannelId()) ? "'" + pidDayVO.getSubChannelId() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(pidDayVO.getSubChannelName()) ? "'" + pidDayVO.getSubChannelName() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(pidDayVO.getChargeRule()) ? "'" + pidDayVO.getChargeRule() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(pidDayVO.getPc()) ? "'" + pidDayVO.getPc() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(pidDayVO.getUv()) ? "'" + pidDayVO.getUv() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(pidDayVO.getRegCount()) ? "'" + pidDayVO.getRegCount() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(pidDayVO.getPayOrderCount()) ? "'" + pidDayVO.getPayOrderCount() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(pidDayVO.getPayFee()) ? "'" + pidDayVO.getPayFee() + "'" : "NULL");
                String rowStr = StringUtils.join(row, ",");
                insertSQLStart.append("(").append(rowStr).append(", now()),");
                if (i % 20 == 0 || i == pidDayVOList.size()) {
                    insertSQLStart = new StringBuilder(insertSQLStart.substring(0, insertSQLStart.length() - 1));

                    insertSQLStart.append(" on duplicate key update " +
                            "ccid = values(ccid), " +
                            "product_name = values(product_name), " +
                            "channel_id = values(channel_id), " +
                            "channel_name = values(channel_name), " +
                            "sub_channel_name = values(sub_channel_name), " +
                            "charge_rule = values(charge_rule), " +
                            "pc = values(pc), " +
                            "uv = values(uv), " +
                            "reg_count = values(reg_count)," +
                            "pay_order_count = values(pay_order_count)," +
                            "pay_fee = values(pay_fee)");

                    log.info("insertSQL: " + insertSQLStart);
                    jdbcTemplate.execute(insertSQLStart.toString());
                    insertSQLStart = new StringBuilder(insertSQL);
                }
            }
        }
    }

}
