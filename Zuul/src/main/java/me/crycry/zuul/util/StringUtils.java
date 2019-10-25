package me.crycry.zuul.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class StringUtils {

    public static Map<String, String> Reader2Map(BufferedReader reader) throws IOException {

        Map<String, String> params = new HashMap<>();
        String jsonParams = "";
        String line;
        while ((line = reader.readLine()) != null) {
            jsonParams += line;
        }
        return JsonStr2Map(jsonParams);
    }

    public static Map<String, String> JsonStr2Map(String str) throws IOException {

        Map<String, String> params = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(str);
        Iterator<String> keys = node.fieldNames();
        while (keys.hasNext()) {
            String key = keys.next();
            String val = node.get(key).asText();
            params.put(key, val);
        }

        return params;
    }

}
