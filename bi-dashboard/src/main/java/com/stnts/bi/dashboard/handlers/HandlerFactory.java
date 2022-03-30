package com.stnts.bi.dashboard.handlers;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2020/10/22
 */
public class HandlerFactory {

    public static Map<String, Handler> handlerMap = new HashMap<>();

    static {
        handlerMap.put(Handler001.HANDLER_ID, new Handler001());
        handlerMap.put(Handler002.HANDLER_ID, new Handler002());
        handlerMap.put(Handler006.HANDLER_ID, new Handler006());
        handlerMap.put(Handler105.HANDLER_ID, new Handler105());
        handlerMap.put(Handler106.HANDLER_ID, new Handler106());
        handlerMap.put(Handler107.HANDLER_ID, new Handler107());
        handlerMap.put(Handler110.HANDLER_ID, new Handler110());
        handlerMap.put(Handler302.HANDLER_ID, new Handler302());
        handlerMap.put(Handler303.HANDLER_ID, new Handler303());
        handlerMap.put("dashboard-dd-205", new Handler205());
        handlerMap.put("dashboard-dd-304", new Handler110());
        handlerMap.put("dashboard-dd-305", new Handler110());

        handlerMap.put("dashboard-dd-204", new Handler105());
    }

    public static Handler getHandler(String handlerId){
        return handlerMap.get(handlerId);
    }
}
