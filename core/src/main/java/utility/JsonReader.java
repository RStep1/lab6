package utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import data.Vehicle;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Converts database from json to ConcurrentHashMap collection.
 */
public class JsonReader {
    private final String json;
    public JsonReader(String json) {
        this.json = json;
    }

    public ConcurrentHashMap<Long, Vehicle> readDataBase() {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<ConcurrentHashMap<Long, Vehicle>>(){}.getType());
    }
}
