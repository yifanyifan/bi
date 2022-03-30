package com.stnts.bi.datamanagement.module.exportdata.dataenum;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ChannelShareFlagType {
    固定分成("1"), 阶梯分成("2");

    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}
