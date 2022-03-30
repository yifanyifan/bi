package com.stnts.bi.datamanagement.module.channel.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 标签层级
 */
public enum LabelLevelEnum {
    ONE("1", "一级"), TWO("2", "二级");

    private String key;
    private String value;

    LabelLevelEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static LabelLevelEnum getByKey(String key) {
        LabelLevelEnum result = null;
        for (LabelLevelEnum s : values()) {
            if (s.getKey().equals(key)) {
                result = s;
                break;
            }
        }
        return result;
    }

    public static LabelLevelEnum getByValue(String value) {
        LabelLevelEnum result = null;
        for (LabelLevelEnum s : values()) {
            if (s.getValue().equals(value)) {
                result = s;
                break;
            }
        }
        return result;
    }

    public static List getMap() {
        List<Map> msg = new ArrayList<Map>();
        for (LabelLevelEnum s : values()) {
            Map sub = new HashMap();
            sub.put("key", s.key);
            sub.put("value", s.value);
            msg.add(sub);
        }
        return msg;
    }

    public static String getString() {
        List<String> msg = new ArrayList<String>();
        for (LabelLevelEnum s : values()) {
            msg.add(s.getKey() + ":" + s.getValue());
        }
        return String.join(",", msg);
    }
}



