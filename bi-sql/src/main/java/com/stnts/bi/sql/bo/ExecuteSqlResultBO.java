package com.stnts.bi.sql.bo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author liutianyuan
 * @date 2019-10-17 18:00
 */

@Data
public class ExecuteSqlResultBO {
    private List<List<String>> result;
    private LocalDateTime executeTime;
}
