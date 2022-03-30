package com.stnts.bi.sql.bo;

import lombok.Data;

import java.util.List;

/**
 * @author liutianyuan
 * @date 2019-08-08 15:47
 */

@Data
public class BuildSqlResultBO {

    private String sql;
    List<Object> bindValues;
}
