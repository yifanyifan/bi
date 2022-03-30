package com.stnts.bi.datamanagement.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * @author tianyuan
 */
public class CompareMapUtil {

    public static void compareMap(Map<String, Object> sourceMap, Map<String, Object> targetMap) {
        if(sourceMap == null || targetMap == null) {
            return;
        }

        targetMap.entrySet().removeIf(next -> next.getValue() == null);

        Iterator<Map.Entry<String, Object>> sourceMapIterator = sourceMap.entrySet().iterator();
        while (sourceMapIterator.hasNext()) {
            Map.Entry<String, Object> entry = sourceMapIterator.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if(targetMap.containsKey(key)) {
                Object compareValue = targetMap.get(key);
                if(Objects.equals(value, compareValue)) {
                    sourceMapIterator.remove();
                    targetMap.remove(key);
                }
            } else {
                sourceMapIterator.remove();
            }
        }

        for (Map.Entry<String, Object> entry : sourceMap.entrySet()) {
            if(entry.getValue() == null) {
                entry.setValue("");
            }
        }
    }

}
