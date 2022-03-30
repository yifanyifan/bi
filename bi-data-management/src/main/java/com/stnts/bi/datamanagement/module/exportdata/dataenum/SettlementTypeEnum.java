package com.stnts.bi.datamanagement.module.exportdata.dataenum;

import java.util.ArrayList;
import java.util.List;

/**
 * 是否内结
 */
public enum SettlementTypeEnum {
    YES(1, "是"), NO(2, "否");

    private int key;
    private String value;

    private SettlementTypeEnum(int key, String value) {
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

    public static SettlementTypeEnum getByKey(int key) {
        SettlementTypeEnum result = null;
        for (SettlementTypeEnum s : values()) {
            if (s.getKey() == key) {
                result = s;
                break;
            }
        }
        return result;
    }

    public static SettlementTypeEnum getByValue(String value) {
        SettlementTypeEnum result = null;
        for (SettlementTypeEnum s : values()) {
            if (s.getValue().equals(value)) {
                result = s;
                break;
            }
        }
        return result;
    }

    public static String getString() {
        List<String> msg = new ArrayList<String>();
        for (SettlementTypeEnum s : values()) {
            msg.add(s.getKey() + ":" + s.getValue());
        }
        return String.join(",", msg);
    }
}



