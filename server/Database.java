package server;


import java.util.HashMap;
import java.util.Map;
import java.util.function.*;


public class Database {
    static Map<String, String> db;

    Database(){
        db = new HashMap<>();
    }

    public void set(String key, String value) { db.put(key, value); }
    public Consumer<String> delete = (key) -> db.remove(key);
    public Function<String, String> get = (key) -> db.get(key);
}
