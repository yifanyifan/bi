package com.stnts.bi.schedule.sdk;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.extra.template.Template;
import com.stnts.bi.schedule.annatation.StntsScheduleAnnatation;
import com.stnts.bi.schedule.config.JdbcTemplateFactoryTwo;
import com.stnts.bi.schedule.deduct.vo.CjtSszmVO;
import com.stnts.bi.schedule.task.IScheduleTask;
import com.stnts.bi.schedule.util.HttpUtils;
import com.stnts.bi.schedule.util.TemplateHelper;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 每天凌晨10分计算
 *
 * @author 刘天元
 */
/*@Service
@StntsScheduleAnnatation(name = "mysql_statistic_pid_report(", cron = "0 10 0 ? * *", description = "随乐游数据从clickhouse导入MySQL")*/
@Slf4j
public class ClickhouseToMysql { //implements IScheduleTask {

    @Value("${schedule.jdbc.ch.url}")
    private String jdbcUrl;
    @Value("${schedule.jdbc.ch.username}")
    private String jdbcUsername;
    @Value("${schedule.jdbc.ch.password}")
    private String jdbcPassword;

    @Autowired
    private HttpUtils httpUtils;

    //@Override
    public void executeInternal(JobExecutionContext jobExecutionContext) {
        long startTime = System.currentTimeMillis();
        String description = jobExecutionContext.getJobDetail().getDescription();
        log.info("\n\n=============开始执行任务[" + description + "]===========");
        insert(DateUtil.yesterday());
        long endTime = System.currentTimeMillis();
        log.info("=============执行任务[" + description + "]结束,耗时[" + (endTime - startTime) + "]ms===========\n\n");
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
            log.info("=================随乐游日数据重跑开始，日期：" + DateUtil.format(date, "yyyy-MM-dd HH:mm:ss"));
            insert(date);
        }
    }

    public void insert(Date date) {
        try {
            Template template = TemplateHelper.getSdkTemplateEngine().getTemplate("mysql_statistic_pid_report.sql");
            String sql = template.render(Dict.create().set("date", DateUtil.formatDate(date)));
            log.info("sql: " + sql);
            JdbcTemplateFactoryTwo.getJdbcTemplateCommon(null, jdbcUrl, jdbcUsername, jdbcPassword).execute(sql);

        } catch (Exception e) {
            log.info(e.getMessage(), e);

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("随乐游数据从clickhouse导入MySQL异常。\n")
                    .append("时间:").append(DateUtil.formatDate(date)).append(",\n")
                    .append("信息:").append(e.getMessage());
            httpUtils.alarm(stringBuffer.toString());
        }
    }

    /*public static String doGet(String httpUrl) {
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        String result = null;
        try {
            // 创建远程url连接对象
            URL url = new URL(httpUrl);
            // 通过远程url连接对象打开一个连接，强转成httpURLConnection类
            connection = (HttpURLConnection) url.openConnection();
            // 设置连接方式：get
            connection.setRequestMethod("GET");
            // 设置连接主机服务器的超时时间：15000毫秒
            connection.setConnectTimeout(15000);
            // 设置读取远程返回的数据时间：60000毫秒
            connection.setReadTimeout(60000);
            // 发送请求
            connection.connect();
            // 通过connection连接，获取输入流
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                // 封装输入流is，并指定字符集
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                // 存放数据
                StringBuffer sbf = new StringBuffer();
                String temp = null;
                while ((temp = br.readLine()) != null) {
                    sbf.append(temp);
                    sbf.append("\r\n");
                }
                result = sbf.toString();
            }
        } catch (Exception e) {
            log.error("http get error", e);
        } finally {
            // 关闭资源
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            connection.disconnect();// 关闭远程连接
        }
        return result;
    }*/


    /*public static class JdbcTemplateFactory {
        static JdbcTemplate jdbcTemplate;

        static String jdbcUrl = "jdbc:clickhouse://clickhouse-bidev.stnts.com:8123";

        static String jdbcUsername = "default";

        static String jdbcPassword = "w5cMSULdy9CwkuOD";


        static JdbcTemplate getJdbcTemplate() {
            if (jdbcTemplate != null) {
                return jdbcTemplate;
            }
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(jdbcUsername);
            config.setPassword(jdbcPassword);
            jdbcTemplate = new JdbcTemplate(new HikariDataSource(config));
            return jdbcTemplate;
        }
    }*/

}
