package com.stnts.bi.datamanagement.module.cooperation.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 公司类型
 */
public enum IsProtectionEnum {
    NOPROTECT(0, "不保护"), DEPPROTECT(1, "部门保护"), PRIPROTECT(2, "私有保护");

    private int key;
    private String value;

    private IsProtectionEnum(int key, String value) {
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

    public static IsProtectionEnum getByKey(int key) {
        IsProtectionEnum result = null;
        for (IsProtectionEnum s : values()) {
            if (s.getKey() == key) {
                result = s;
                break;
            }
        }
        return result;
    }

    public static IsProtectionEnum getByValue(String value) {
        IsProtectionEnum result = null;
        for (IsProtectionEnum s : values()) {
            if (s.getValue().equals(value)) {
                result = s;
                break;
            }
        }
        return result;
    }

    public static String getString() {
        List<String> msg = new ArrayList<String>();
        for (IsProtectionEnum s : values()) {
            msg.add(s.getKey() + ":" + s.getValue());
        }
        return String.join(",", msg);
    }
}



