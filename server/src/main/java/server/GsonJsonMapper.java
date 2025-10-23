package server;

import com.google.gson.Gson;
import io.javalin.json.JsonMapper;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class GsonJsonMapper implements JsonMapper {

    private final Gson gson = new Gson();

    @Override
    public String toJsonString(Object obj, Type type) {
        return gson.toJson(obj);
    }

    @Override
    public <T> T fromJsonString(String json, Type targetType) {
        return gson.fromJson(json, targetType);
    }

    @Override
    public <T> T fromJsonStream(InputStream json, Type targetType) {
        try (Scanner s = new Scanner(json, StandardCharsets.UTF_8).useDelimiter("\\A")) {
            return gson.fromJson(s.hasNext() ? s.next() : "", targetType);
        }
    }
}
