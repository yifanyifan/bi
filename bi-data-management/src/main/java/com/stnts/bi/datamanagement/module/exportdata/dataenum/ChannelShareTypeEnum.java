package com.stnts.bi.datamanagement.module.exportdata.dataenum;

import java.util.ArrayList;
import java.util.List;

/**
 * 结算指标
 */
public enum ChannelShareTypeEnum {
    INCOME(1, "收入"), PROFITS(2, "利润"), REGISTERED(3, "注册"), ACTIVATION(4, "激活");

    private int key;
    private String value;

    private ChannelShareTypeEnum(int key, String value) {
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

    public static ChannelShareTypeEnum getByKey(int key) {
        ChannelShareTypeEnum result = null;
        for (ChannelShareTypeEnum s : values()) {
            if (s.getKey() == key) {
                result = s;
                break;
            }
        }
        return result;
    }

    public static ChannelShareTypeEnum getByValue(String value) {
        ChannelShareTypeEnum result = null;
        for (ChannelShareTypeEnum s : values()) {
            if (s.getValue().equals(value)) {
                result = s;
                break;
            }
        }
        return result;
    }

    public static String getString() {
        List<String> msg = new ArrayList<String>();
        for (ChannelShareTypeEnum s : values()) {
            msg.add(s.getKey() + ":" + s.getValue());
        }
        return String.join(",", msg);
    }
}



