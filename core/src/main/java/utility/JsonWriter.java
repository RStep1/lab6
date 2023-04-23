package utility;

import java.util.Hashtable;

import com.google.gson.Gson;
import data.Vehicle;

/**
 * Converts collection from hashtable to json.
 */
public class JsonWriter {
    private final Hashtable<Long, Vehicle> dataBase;
    public JsonWriter(Hashtable<Long, Vehicle> dataBase) {
        this.dataBase = dataBase;
    }

    public String writeDataBase() {
        Gson gson = new Gson();
        return gson.toJson(dataBase);
    }
}
