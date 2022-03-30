package com.stnts.bi.datamanagement.module.exportdata.dataenum;

import java.util.ArrayList;
import java.util.List;

/**
 * 保密类型
 */
public enum SecretTypeEnum {
    SHARED(1, "共享"), PRIVATED(2, "私有");

    private int key;
    private String value;

    private SecretTypeEnum(int key, String value) {
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

    public static SecretTypeEnum getByKey(int key) {
        SecretTypeEnum result = null;
        for (SecretTypeEnum s : values()) {
            if (s.getKey() == key) {
                result = s;
                break;
            }
        }
        return result;
    }

    public static SecretTypeEnum getByValue(String value) {
        SecretTypeEnum result = null;
        for (SecretTypeEnum s : values()) {
            if (s.getValue().equals(value)) {
                result = s;
                break;
            }
        }
        return result;
    }

    public static String getString() {
        List<String> msg = new ArrayList<String>();
        for (SecretTypeEnum s : values()) {
            msg.add(s.getKey() + ":" + s.getValue());
        }
        return String.join(",", msg);
    }
}



