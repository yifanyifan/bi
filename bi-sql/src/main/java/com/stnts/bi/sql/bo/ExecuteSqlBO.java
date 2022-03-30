package com.stnts.bi.sql.bo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author liutianyuan
 * @date 2019-10-17 18:00
 */

@Data
public class ExecuteSqlBO {
    private String cacheKey;
    private String sql;
    private List<Object> bindValues;
    private List<List<String>> result;
    private Boolean cacheHit;
    private Boolean cache;
    private LocalDateTime executeTime;
    private String queryType;
}
