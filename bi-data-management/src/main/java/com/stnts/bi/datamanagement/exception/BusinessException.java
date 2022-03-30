package com.stnts.bi.datamanagement.exception;

/**
 * @author liutianyuan
 */
public class BusinessException extends RuntimeException {

    private Integer code;
    private String info;

    public BusinessException(String info) {
        super(info);
        this.info = info;
    }

    public BusinessException(String info, Throwable cause) {
        super(info, cause);
        this.info = info;
    }

    public BusinessException(Integer code, String info) {
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
