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
 * @since 2019-03-27
 */
@Data
@Accessors(chain = true)
public class OlapDsDatabase implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String name;

    private String namespace;

    private String dataSource;

    private Integer owner_product_id;


}
