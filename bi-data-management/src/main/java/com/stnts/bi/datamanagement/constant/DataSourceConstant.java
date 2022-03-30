package com.stnts.bi.datamanagement.constant;

/**
 * 数据来源标识。来源方式：1. BI平台新增；2. 金蝶同步；3. 友拓同步；4. 友拓新增；5. 业务方订单系统新增；6. CRM渠道版；7. CRM通用版；
 * @author tianyuan
 */
public class DataSourceConstant {

    public final static Integer dataSourceBI = 1;

    public final static Integer dataSourceEAS = 2;

    public final static Integer dataSourceYoutopSync = 3;

    public final static Integer dataSourceYoutopCreate = 4;

    public final static Integer dataSourceOrderSystem = 5;

    public final static Integer dataSourceCRM = 6;

}
