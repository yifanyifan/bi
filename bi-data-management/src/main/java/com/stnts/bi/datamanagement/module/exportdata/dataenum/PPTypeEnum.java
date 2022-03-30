package com.stnts.bi.datamanagement.module.exportdata.dataenum;

import java.util.ArrayList;
import java.util.List;

/**
 * 推广位类型
 */
public enum PPTypeEnum {
    CHANNELPP(1, "渠道推广位"), SUBCHANNELPP(2, "子渠道推广位");

    private int key;
    private String value;

    private PPTypeEnum(int key, String value) {
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

    public static PPTypeEnum getByKey(int key) {
        PPTypeEnum result = null;
        for (PPTypeEnum s : values()) {
            if (s.getKey() == key) {
                result = s;
                break;
            }
        }
        return result;
    }

    public static PPTypeEnum getByValue(String value) {
        PPTypeEnum result = null;
        for (PPTypeEnum s : values()) {
            if (s.getValue().equals(value)) {
                result = s;
                break;
            }
        }
        return result;
    }

    public static String getString() {
        List<String> msg = new ArrayList<String>();
        for (PPTypeEnum s : values()) {
            msg.add(s.getKey() + ":" + s.getValue());
        }
        return String.join(",", msg);
    }
}



