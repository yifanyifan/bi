package com.stnts.signature.entity;

import com.stnts.signature.annotation.*;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author liutianyuan
 */
@SignedEntity
public class SignedParam {
    @SignedAppId
    @ApiModelProperty(value = "APP id", position = -4)
    private String appId;
    @SignedTimestamp
    @ApiModelProperty(value = "毫秒时间戳", position = -3)
    private long timestamp;
    @SignedNonce
    @ApiModelProperty(value = "随机数", position = -2)
    private int nonce;
    @Signature
    @ApiModelProperty(value = "签名", position = -1)
    private String signature;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "ParamSigned{" +
                "appId='" + appId + '\'' +
                ", timestamp=" + timestamp +
                ", nonce=" + nonce +
                ", signature='" + signature + '\'' +
                '}';
    }
}

