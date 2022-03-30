package com.stnts.bi.sql.constant;

/**
 * @author LIUTIANYUAN
 */
public class ColumnTypeConstant {
    public final static String TEXT = "TEXT";
    public final static String INT = "INT";
    public final static String DATE = "DATE";

    public final static String[] INT_FORMAT = {
            "UInt8","UInt16","UInt32","UInt64","Int8","Int16","Int32","Int64","Float32","Float64"
    };


    public final static String[] TEXT_FORMAT = {
            "String"
    };


    public final static String[] DATE_FORMAT ={"Date", "DateTime"};

}
