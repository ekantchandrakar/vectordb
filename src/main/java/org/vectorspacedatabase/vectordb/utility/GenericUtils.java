package org.vectorspacedatabase.vectordb.utility;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GenericUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String buildRequestBody(String content, String model, String encodingFormat) throws IOException {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("input", content);
        requestBody.put("model", model);
        requestBody.put("encoding_format", encodingFormat);

        return objectMapper.writeValueAsString(requestBody);
    }
}
