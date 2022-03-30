package com.stnts.bi.sql.bo;

import lombok.Data;

/**
 * @author liutianyuan
 */
@Data
public class QueryTableColumnResultBO {
    private String columnName;
    private String columnType;
    private String olapType;
    private String columnExp;
    private String columnNickname;
}
