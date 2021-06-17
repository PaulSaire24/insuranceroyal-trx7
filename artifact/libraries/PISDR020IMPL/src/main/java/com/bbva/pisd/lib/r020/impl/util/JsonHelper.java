package com.bbva.pisd.lib.r020.impl.util;

import com.google.gson.*;
import org.joda.time.LocalDate;

import java.lang.reflect.Type;

public class JsonHelper {

    private static final String DATE = "yyyy-MM-dd";
    private static final JsonHelper INSTANCE = new JsonHelper();

    private final Gson gson;

    private JsonHelper() {
        gson = new GsonBuilder()
                .setDateFormat(DATE)
                .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                .create();
    }

    public static JsonHelper getInstance() { return INSTANCE; }

    public <T> T fromString(String src, Class<T> clazz) { return this.gson.fromJson(src, clazz); }

    public String toJsonString(Object o) { return this.gson.toJson(o); }

}

class LocalDateSerializer implements JsonSerializer<LocalDate> {
    @Override
    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());    }
}