package com.stnts.bi.schedule.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;

@Data
public class JdbcTemplateFactoryTwo {
    /**
     * BI ClickHouse数据源
     */
    @Value("${schedule.jdbc.ch.url}")
    private static String jdbcUrl;
    @Value("${schedule.jdbc.ch.username}")
    private static String jdbcUsername;
    @Value("${schedule.jdbc.ch.password}")
    private static String jdbcPassword;

    static JdbcTemplate jdbcTemplate;

    public static JdbcTemplate getJdbcTemplate() {
        if (jdbcTemplate != null) {
            return jdbcTemplate;
        }
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(jdbcUsername);
        config.setPassword(jdbcPassword);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(new HikariDataSource(config));
        return jdbcTemplate;
    }

    static Map<String, JdbcTemplate> jdbcTemplateMap = new HashMap<String, JdbcTemplate>();

    /**
     * 自定义数据源
     */
    public static JdbcTemplate getJdbcTemplateCommon(String driver, String url, String userName, String password) {
        String key = url + userName + password;
        if (jdbcTemplateMap.containsKey(key)) {
            return jdbcTemplateMap.get(key);
        }

        HikariConfig config = new HikariConfig();
        if (StringUtils.isNotBlank(driver)) {
            config.setDriverClassName(driver);
        }
        config.setJdbcUrl(url);
        config.setUsername(userName);
        config.setPassword(password);
        JdbcTemplate jdbcTemplateCJT = new JdbcTemplate(new HikariDataSource(config));
        jdbcTemplateCJT.setQueryTimeout(6000);

        jdbcTemplateMap.put(key, jdbcTemplateCJT);

        return jdbcTemplateCJT;
    }
}
