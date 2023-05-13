package utility;

import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import data.Vehicle;

/**
 * Converts collection from ConcurrentHashMap to json.
 */
public class JsonWriter {
    private final ConcurrentHashMap<Long, Vehicle> dataBase;
    public JsonWriter(ConcurrentHashMap<Long, Vehicle> dataBase) {
        this.dataBase = dataBase;
    }

    public String writeDataBase() {
        Gson gson = new Gson();
        return gson.toJson(dataBase);
    }
}
