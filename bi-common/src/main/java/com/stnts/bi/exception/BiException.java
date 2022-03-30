package com.stnts.bi.exception;

import jdk.nashorn.internal.objects.annotations.Constructor;
import lombok.*;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/1/20
 */
@Getter
@Setter
@AllArgsConstructor
public class BiException extends RuntimeException{

    private Integer code;
    private String msg;

    public BiException(String message, String msg) {
        super(message);
        this.msg = msg;
    }

    public BiException(String msg) {
        this.msg = msg;
    }

    public BiException() {
    }

    public BiException(String message, Throwable cause, String msg) {
        super(message, cause);
        this.msg = msg;
    }

    public BiException(Throwable cause) {
        super(cause);
    }

    public BiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String msg) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.msg = msg;
    }
}
