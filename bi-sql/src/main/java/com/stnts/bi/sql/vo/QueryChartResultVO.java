package com.stnts.bi.sql.vo;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author liutianyuan
 */
@Data
public class QueryChartResultVO {
    private Double limit;

    private Integer total;
    private String sql;
    List<Object> bindValues;
    private Boolean cacheHit;
    @JsonFormat(pattern= DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime datasTime;

    private List<Object> datas;

    @JsonSerialize(using = CustomSerializeForListList.class)
    private List<List<String>> rowFormatDataList;

    @Data
    public static class DimensionData {
        private Integer id;
        private String category;
        private String group;
        private String name;
        private String displayName;
        @JsonSerialize(using = CustomSerializeForList.class)
        private List<String> data;
        @JsonSerialize(using = CustomSerializeForList.class)
        private List<String> compareData;
        private Integer order;
        private String format;
        private List<String> distinctData;
        private List<String> compareDistinctData;
    }

    @Data
    public static class MeasureData {
        private Integer id;
        @JsonSerialize(using = CustomSerializeForList.class)
        private List<String> data;
        @JsonSerialize(using = CustomSerializeForList.class)
        private List<String> compareData;
        private Map<String, List<String>> compareMap;
        private String totalData;
        private String category;
        /**
         * percent of total。计算当前值和总和的比值。
         */
        private Boolean proportion;
        /**
         * percent of max。计算当前值和最大值的比值。
         */
        private Boolean percentOfMax;
        private String displayName;
        private String name;
        private String type;
        private Integer order;
        /**
         * 同比、环比、二次计算，多个用逗号隔开
         */
        private String contrast;
        private String summary;
        private List<Object> groupData;
        private List<Object> compareGroupData;
        private List<String> dimensionData;
        private String digitDisplay;
        private Integer decimal;
    }

    public static class CustomSerializeForList extends JsonSerializer<List<String>> {
        @Override
        public void serialize(List<String> stringList, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartArray();
            serializeList(stringList, jsonGenerator);
            jsonGenerator.writeEndArray();

        }
    }

    public static void serializeList(List<String> stringList, JsonGenerator jsonGenerator) throws IOException {
        for (String str : stringList) {
            if(StrUtil.isEmpty(str)) {
                jsonGenerator.writeNull();
            }else {
                try {
                    BigDecimal bigDecimal = new BigDecimal(str);
                    String bigDecimalToPlainString = bigDecimal.toPlainString();
                    if(StrUtil.startWith(str, "0") && !StrUtil.startWith(bigDecimalToPlainString, "0")) {
                        jsonGenerator.writeString(str);
                    } else if(NumberUtil.isGreater(bigDecimal, new BigDecimal(Integer.MAX_VALUE))) {
                        jsonGenerator.writeString(str);
                    } else {
                        jsonGenerator.writeNumber(bigDecimalToPlainString);
                    }
                } catch (Exception newBigDecimalException) {
                    jsonGenerator.writeString(str);
                }
            }
        }
    }

    public static class CustomSerializeForListList extends JsonSerializer<List<List<String>>> {
        @Override
        public void serialize(List<List<String>> stringListList, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartArray();
            for (List<String> stringList : stringListList) {
                jsonGenerator.writeStartArray();
                serializeList(stringList, jsonGenerator);
                jsonGenerator.writeEndArray();
            }
            jsonGenerator.writeEndArray();
        }
    }

}
