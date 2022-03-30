package com.stnts.bi.sdk.exception;

/**
 * @author liutianyuan
 */
public class ValidationException extends RuntimeException {

    private Integer code;
    private String info;

    public ValidationException(String info) {
        super(info);
        this.info = info;
    }

    public ValidationException(String info, Throwable cause) {
        super(info, cause);
        this.info = info;
    }

    public ValidationException(Integer code, String info) {
        super(info);
        this.code = code;
        this.info = info;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
