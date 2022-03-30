package com.stnts.bi.sql.util;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author liutianyuan
 * @date 2019-05-05 15:14
 */

public class JacksonSerializer {

    public static class StringOrObjectJsonSerialize extends JsonSerializer<String> {
        @Override
        public void serialize(String str, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if(JSONUtil.isJson(str)) {
                jsonGenerator.writeRawValue(str);
            } else {
                jsonGenerator.writeString(str);
            }
        }
    }
}
