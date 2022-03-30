package com.stnts.bi.gameop.handlers;

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
        handlerMap.put(SearchMediumHandler.HANDLER_ID, new SearchMediumHandler());

        //
        handlerMap.put(GameTimeHandler.HANDLER_ID, new GameTimeHandler());
        handlerMap.put(ChargeTimeHandler.HANDLER_ID, new ChargeTimeHandler());
        handlerMap.put(MoneyDisHandler.HANDLER_ID, new MoneyDisHandler());

        handlerMap.put(MoneyChannelDisHandler.HANDLER_ID, new MoneyChannelDisHandler());
        handlerMap.put(ChargeTimeChannelHandler.HANDLER_ID, new ChargeTimeChannelHandler());

        //渠道  饼图  线图
        handlerMap.put(PieTopChartHandler.HANDLER_ID, new PieTopChartHandler());
        handlerMap.put(LineTopChartHandler.HANDLER_ID, new LineTopChartHandler());

        handlerMap.put(CompareHandler.HANDLER_ID_MOM, new CompareHandler());
        handlerMap.put(CompareHandler.HANDLER_ID_YOY, new CompareHandler());
        handlerMap.put(CompareHandler.HANDLER_ID_ALL, new CompareHandler());
    }

    public static Handler getHandler(String handlerId){
        return handlerMap.get(handlerId);
    }
}
