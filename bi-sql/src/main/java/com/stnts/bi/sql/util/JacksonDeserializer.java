package com.stnts.bi.sql.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.io.IOException;

/**
 * @author liutianyuan
 * @date 2019-05-05 15:14
 */

public class JacksonDeserializer {

    public static class KeepAsJsonDeserializer extends JsonDeserializer<String> {
        @Override
        public String deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
            TreeNode tree = jsonParser.getCodec().readTree(jsonParser);
            if(tree instanceof ArrayNode) {
                ArrayNode arrayNode = (ArrayNode)tree;
                return arrayNode.toString();
            }
            if(tree instanceof TextNode) {
                TextNode textNode = (TextNode)tree;
                return textNode.asText();
            }
            return tree.toString();
        }
    }
}
