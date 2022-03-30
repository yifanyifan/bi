package com.stnts.bi.sql.bo;

import lombok.Data;
import org.jooq.*;

import java.util.LinkedList;
import java.util.List;

/**
 * @author liutianyuan
 * @date 2019-08-08 15:47
 */

@Data
public class BuildSqlBO {

    public BuildSqlBO() {
        this.selectFieldList = new LinkedList<>();
        this.groupFieldList = new LinkedList<>();
        this.havingConditionList = new LinkedList<>();
        this.sortFieldList = new LinkedList<>();
    }

    private List<SelectField<?>> selectFieldList;
    private List<Field> groupFieldList;
    private List<Condition> havingConditionList;
    private List<SortField<?>> sortFieldList;
    private String fromSql;
    private Boolean withRollup;
    private String withRollupName;
}
