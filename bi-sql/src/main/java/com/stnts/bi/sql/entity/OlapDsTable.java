package com.stnts.bi.sql.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author liutianyuan
 * @since 2019-03-27
 */
@Data
@Accessors(chain = true)
public class OlapDsTable implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String name;

    private Boolean isView;

    private String viewSql;

    private Boolean appendFinal;

    private String desc;

    private String nickname;




}
