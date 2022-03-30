package com.stnts.bi.sql.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author liutianyuan
 * @since 2019-03-29
 */
@Data
@Accessors(chain = true)
public class OlapChart implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String desc;

    private String type;

    private Double limit;


}
