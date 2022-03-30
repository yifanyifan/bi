package com.stnts.bi.datamanagement.module.exportdata.dataenum;

import java.util.ArrayList;
import java.util.List;

/**
 * 是否自营
 */
public enum ChannelTypeEnum {
    SELFSUPPORT(1, "自营"), NOSELFSUPPORT(2, "非自营");

    private int key;
    private String value;

    private ChannelTypeEnum(int key, String value) {
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

    public static ChannelTypeEnum getByKey(int key) {
        ChannelTypeEnum result = null;
        for (ChannelTypeEnum s : values()) {
            if (s.getKey() == key) {
                result = s;
                break;
            }
        }
        return result;
    }

    public static ChannelTypeEnum getByValue(String value) {
        ChannelTypeEnum result = null;
        for (ChannelTypeEnum s : values()) {
            if (s.getValue().equals(value)) {
                result = s;
                break;
            }
        }
        return result;
    }

    public static String getString() {
        List<String> msg = new ArrayList<String>();
        for (ChannelTypeEnum s : values()) {
            msg.add(s.getKey() + ":" + s.getValue());
        }
        return String.join(",", msg);
    }
}



