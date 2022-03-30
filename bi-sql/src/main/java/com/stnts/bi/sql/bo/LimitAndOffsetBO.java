package com.stnts.bi.sql.bo;

import lombok.Data;

/**
 * @author liutianyuan
 * @date 2019-10-21 14:16
 */

@Data
public class LimitAndOffsetBO {

    public LimitAndOffsetBO(Integer limit, Integer offset) {
        this.limit = limit;
        this.offset = offset;
    }

    Integer limit;
    Integer offset;

}
