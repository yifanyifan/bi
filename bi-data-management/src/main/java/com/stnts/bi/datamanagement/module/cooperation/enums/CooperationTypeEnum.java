package com.stnts.bi.datamanagement.module.cooperation.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 公司类型
 */
public enum CooperationTypeEnum {
    CUSTOMER(1, "客户"), SUPPLIER(2, "供应商");

    private int key;
    private String value;

    private CooperationTypeEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static CooperationTypeEnum getByKey(int key) {
        CooperationTypeEnum result = null;
        for (CooperationTypeEnum s : values()) {
            if (s.getKey() == key) {
                result = s;
                break;
            }
        }
        return result;
    }

    public static CooperationTypeEnum getByValue(String value) {
        CooperationTypeEnum result = null;
        for (CooperationTypeEnum s : values()) {
            if (s.getValue().equals(value)) {
                result = s;
                break;
            }
        }
        return result;
    }

    public static String getString() {
        List<String> msg = new ArrayList<String>();
        for (CooperationTypeEnum s : values()) {
            msg.add(s.getKey() + ":" + s.getValue());
        }
        return String.join(",", msg);
    }
}



