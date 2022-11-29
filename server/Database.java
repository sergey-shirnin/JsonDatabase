package server;

import com.google.gson.*;

import java.io.*;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static server.Main.path;


public class Database {
    private JsonObject db;
    private JsonElement dbCurrentLvl;
    private String lastComplexKey = null;

    Database() {
        try {
            File dbFile = new File(path);
            if (dbFile.createNewFile()) {
                System.out.printf("New db file created @%n%s%n!", path);
                db = new JsonObject();
            } else {
                System.out.printf("Using existing db file @%n%s%n", path);
                BufferedReader reader = new BufferedReader(new FileReader(path));
                db = new Gson().fromJson(reader.readLine(), JsonObject.class);
            }
        } catch (IOException e) {
            System.out.printf("Db file read/create failed!\nMsg:%s%n", e.getMessage());
        }
    }

    public BiConsumer<JsonElement, JsonElement> set = (key, value) -> {
        JsonObject dbCurrentLvl = db;
        if (key.isJsonPrimitive()) {
            db.add(key.getAsString(), value);
        } else {
            JsonArray keys = key.getAsJsonArray();
            String addKey = keys.remove(keys.size() - 1).getAsString();
            for (var currentKey : keys) {
                if (!dbCurrentLvl.has(currentKey.getAsString()) ||
                        dbCurrentLvl.get(currentKey.getAsString()).isJsonPrimitive()) {
                    dbCurrentLvl.add(currentKey.getAsString(), new JsonObject());
                }
                dbCurrentLvl = (JsonObject) dbCurrentLvl.get(currentKey.getAsString());
            }
            dbCurrentLvl.add(addKey, value);
        }
        write();
    };

    public Consumer<JsonElement> delete = (key) -> {
        var Object = key.isJsonPrimitive()
                ? db.remove(key.getAsString())
                : dbCurrentLvl.getAsJsonObject().remove(lastComplexKey);
        write();
    };

    public Function<JsonElement, JsonElement> get = (key) -> {
        if (key.isJsonPrimitive() || key.getAsJsonArray().size() == 1) {
            return db.get(key.getAsString());
        }
        return dbCurrentLvl;
    };

    public Predicate<JsonElement> hasKey = (key) -> {
        Boolean isThere = false;
        dbCurrentLvl = db;
        if (key.isJsonPrimitive() || key.getAsJsonArray().size() == 1) {
            isThere = db.has(key.getAsString());
        } else {
            JsonArray keys = key.getAsJsonArray();
            lastComplexKey = keys.get(keys.size() - 1).getAsString();
            for (int i = 0; i < keys.size() - 1; i++) {
                dbCurrentLvl = ((JsonObject) dbCurrentLvl).get(keys.get(i).getAsString());
                if (!dbCurrentLvl.isJsonObject()) {
                    isThere = false;
                    break;
                }
                isThere = ((JsonObject) dbCurrentLvl).has(lastComplexKey);
            }
            if (Session.request.getType().equals("get")) {
                dbCurrentLvl = ((JsonObject) dbCurrentLvl).get(lastComplexKey);
            }
        }
        return isThere;
    };

    private void write() {
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(new GsonBuilder().create().toJson(db));
        } catch (Exception e) {
            System.out.printf("Write to db failed!\nMsg:%s%n", e.getMessage());
        }
    }
}
