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
import com.stnts.bi.schedule.util.HttpUtils;
import com.stnts.bi.schedule.util.TemplateHelper;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
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
@StntsScheduleAnnatation(name = "CjtSlyTaskName", cron = "0 10 0 ? * *", description = "随乐游推日数据")
public class CjtSlyTask implements IScheduleTask {
    private static final Logger log = LoggerFactory.getLogger(CjtSlyTask.class);

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

        Date date = DateUtil.yesterday();
        try {
            Long startTime1 = System.currentTimeMillis();
            log.info("\n\n=============开始执行任务[" + description + "]===========");
            CjtSszmVO cjtSszmVO1 = new CjtSszmVO();
            cjtSszmVO1.setPartitionDate(date);
            start(cjtSszmVO1);
            Long endTime1 = System.currentTimeMillis();
            log.info("=============执行任务[" + description + "]结束,耗时[" + (endTime1 - startTime1) + "]ms===========\n\n");
        } catch (Exception e) {
            log.info("=============执行任务[" + description + "]异常" + e.getMessage(), e);

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("随乐游推日数据异常。\n")
                    .append("时间:").append(DateUtil.formatDate(date)).append(",\n")
                    .append("信息:").append(e.getMessage());
            httpUtils.alarm(stringBuffer.toString());
        }
    }

    /**
     * 重跑
     *
     * @param cjtSszmVO
     */
    public void startAgain(CjtSszmVO cjtSszmVO) {
        Date start = cjtSszmVO.getPartitionDateStart();
        Date end = cjtSszmVO.getPartitionDateEnd();

        List<Date> dateList = com.stnts.bi.schedule.util.DateUtil.getBetweenDates(start, end);

        for (Date date : dateList) {
            log.info("=================随乐游推日数据重跑开始，日期：" + DateUtil.format(date, "yyyy-MM-dd HH:mm:ss"));
            cjtSszmVO.setPartitionDate(date);
            start(cjtSszmVO);
        }
    }

    /**
     * 开始
     *
     * @param cjtSszmVO
     */
    public void start(CjtSszmVO cjtSszmVO) {
        String dateStr = DateUtil.format(cjtSszmVO.getPartitionDate(), "yyyy-MM-dd");

        Template template = TemplateHelper.getDeductTemplateEngine().getTemplate("suileyou_dayly_data.sql");
        Dict dict = Dict.create().set("dateStr", dateStr);
        String querySQL = template.render(dict);
        // ================ 兼容重跑 Start ================
        if (CollectionUtil.isNotEmpty(cjtSszmVO.getPidList())) {
            String pidAgainStr = JSON.toJSONString(cjtSszmVO.getPidList());
            String agentIdStr = pidAgainStr.replace("[", "").replace("]", "").replace("\"", "'");
            querySQL += " and _t2.pid in (" + agentIdStr + ") ";
        } else if (CollectionUtil.isNotEmpty(cjtSszmVO.getCcidList())) {
            String ccidAgainStr = JSON.toJSONString(cjtSszmVO.getCcidList());
            String agentIdStr = ccidAgainStr.replace("[", "").replace("]", "").replace("\"", "'");
            querySQL += " and _t2.ccid in (" + agentIdStr + ")";
        }
        if (CollectionUtil.isNotEmpty(cjtSszmVO.getChannelIdList())) {
            String channelIdAgainStr = JSON.toJSONString(cjtSszmVO.getChannelIdList());
            String channelIdIdStr = channelIdAgainStr.replace("[", "").replace("]", "").replace("\"", "'");
            querySQL += " and _t2.channel_id in (" + channelIdIdStr + ")";
        } else if (CollectionUtil.isNotEmpty(cjtSszmVO.getSubChannelIdList())) {
            String subChannelIdAgainStr = JSON.toJSONString(cjtSszmVO.getSubChannelIdList());
            String subChannelIdStr = subChannelIdAgainStr.replace("[", "").replace("]", "").replace("\"", "'");
            querySQL += " and _t2.sub_channel_id in (" + subChannelIdStr + ")";
        }
        log.info("随乐游推日数据 querySQL: " + querySQL);
        List<PidDayVO> cjtList = JdbcTemplateFactoryTwo.getJdbcTemplateCommon(null, jdbcUrl, jdbcUsername, jdbcPassword).query(querySQL, new BeanPropertyRowMapper<PidDayVO>(PidDayVO.class));
        log.info("随乐游推日数据 query集合大小：" + cjtList.size());
        toCJT(cjtList);
        log.info("随乐游推日数据到CJT结束");
    }

    /**
     * 推送数据
     */
    public void toCJT(List<PidDayVO> pidDayVOList) {
        if (CollectionUtil.isNotEmpty(pidDayVOList)) {
            JdbcTemplate jdbcTemplate = JdbcTemplateFactoryTwo.getJdbcTemplateCommon("com.mysql.jdbc.Driver", jdbcUrl2, jdbcUsername2, jdbcPassword2);

            String insertSQL = "INSERT INTO statistic_pid_report (`date`, pid, ccid, product_name, product_code, channel_id, channel_name, sub_channel_id, sub_channel_name, charge_rule, uv, reg_count, pay_fee, created_at) values ";

            StringBuilder insertSQLStart = new StringBuilder(insertSQL);
            for (int i = 1; i <= pidDayVOList.size(); i++) {
                PidDayVO pidDayVO = pidDayVOList.get(i - 1);

                List<String> row = new ArrayList<String>();
                row.add(ObjectUtil.isNotEmpty(pidDayVO.getDateNow()) ? "'" + pidDayVO.getDateNow() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(pidDayVO.getPid()) ? "'" + pidDayVO.getPid() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(pidDayVO.getCcid()) ? "'" + pidDayVO.getCcid() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(pidDayVO.getProductName()) ? "'" + pidDayVO.getProductName() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(pidDayVO.getProductCode()) ? "'" + pidDayVO.getProductCode() + "'" : "NULL");
                row.add(ObjectUtil.isNotEmpty(pidDayVO.getChannelId()) ? "'" + pidDayVO.getChannelId() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(pidDayVO.getChannelName()) ? "'" + pidDayVO.getChannelName() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(pidDayVO.getSubChannelId()) ? "'" + pidDayVO.getSubChannelId() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(pidDayVO.getSubChannelName()) ? "'" + pidDayVO.getSubChannelName() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(pidDayVO.getChargeRule()) ? "'" + pidDayVO.getChargeRule() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(pidDayVO.getUv()) ? "'" + pidDayVO.getUv() + "'" : "NULL");
                row.add(StringUtils.isNotBlank(pidDayVO.getRegCount()) ? "'" + pidDayVO.getRegCount() + "'" : "NULL");
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
                            "uv = values(uv), " +
                            "reg_count = values(reg_count)," +
                            "pay_fee = values(pay_fee)");

                    log.info("insertSQL: " + insertSQLStart);
                    jdbcTemplate.execute(insertSQLStart.toString());
                    insertSQLStart = new StringBuilder(insertSQL);
                }
            }
        }
    }
}
