package com.precisionhawk.latas.client.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.ObjectMapper;

public class JsonUtil {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final ObjectMapper objectMapper = new ObjectMapper() {
        @Override
        public <T> T readValue(String s, Class<T> aClass) {
            return gson.fromJson(s, aClass);
        }

        @Override
        public String writeValue(Object o) {
            return gson.toJson(o);
        }
    };

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static <T> T fromJson(String string, Class<T> klass) {
        return gson.fromJson(string, klass);
    }
}
