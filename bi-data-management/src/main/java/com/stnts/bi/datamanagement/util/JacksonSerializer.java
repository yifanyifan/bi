package com.stnts.bi.datamanagement.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author liutianyuan
 * @date 2019-05-05 15:14
 */

public class JacksonSerializer {

    public final static Integer integerSecret = -1000;
    public final static LocalDateTime dateSecret = LocalDateTimeUtil.parse("0001-01-01 00:00:00");

    public static class SecretIntegerCooperationSerializer extends JsonSerializer<Integer> {
        @Override
        public void serialize(Integer value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if(JacksonSerializer.integerSecret.equals(value)) {
                jsonGenerator.writeString("***");
            } else {
                jsonGenerator.writeNumber(value);
            }
        }
    }

    public static class SecretDateCooperationSerializer extends JsonSerializer<LocalDateTime> {
        @Override
        public void serialize(LocalDateTime value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if(JacksonSerializer.dateSecret.equals(value)) {
                jsonGenerator.writeString("***");
            } else {
                jsonGenerator.writeString(LocalDateTimeUtil.format(value));
            }
        }
    }
}
