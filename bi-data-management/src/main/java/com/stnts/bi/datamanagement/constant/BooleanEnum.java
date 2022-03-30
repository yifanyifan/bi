package com.stnts.bi.datamanagement.constant;

public enum BooleanEnum {
    True(1, "真"), False(0, "假");

    private final Integer key;
    private final String value;
    BooleanEnum(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public Integer getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
