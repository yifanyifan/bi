package com.stnts.bi.schedule.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * @author 刘天元
 */
@Slf4j
@Service
public class SqlUtil {

    private final JdbcTemplate jdbcTemplate;

    public SqlUtil(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void executeSql(String sql) {
        log.info(sql);
        jdbcTemplate.execute(sql);
    }

    public Integer queryForOne(String sql) {
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }
}
