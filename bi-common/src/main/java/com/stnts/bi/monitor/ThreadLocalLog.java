package com.stnts.bi.monitor;

/**
 * @author liutianyuan
 * @date 2021-06-16 15:19
 */

public class ThreadLocalLog {
    private static final ThreadLocal<LogBO> LOG_THREAD_LOCAL = new ThreadLocal<>();

    public static LogBO get() {
        return LOG_THREAD_LOCAL.get();
    }

    public static void set(LogBO logBO) {
        LOG_THREAD_LOCAL.set(logBO);
    }

    public static void remove() {
        LOG_THREAD_LOCAL.remove();
    }
}
