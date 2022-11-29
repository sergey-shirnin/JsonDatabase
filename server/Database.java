package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

import java.util.HashMap;
import java.util.Map;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static server.Main.dbPath;
import static server.Main.dbPathTest;

public class Database {
    private Map<String, String> db;

    Database() {
        try {
            File dbFile = new File(dbPathTest);
            if (dbFile.createNewFile()) {
                System.out.printf("New db file created @%n%s%n!", dbPathTest);
                db = new HashMap<>();
            } else {
                System.out.printf("Using existing db file @%n%s%n", dbPathTest);
                BufferedReader reader = new BufferedReader(new FileReader(dbPathTest));
                db = new Gson().fromJson(reader.readLine(), HashMap.class);
            }
        } catch (IOException e) {
            System.out.printf("Db file read/create failed!\nMsg:%s%n", e.getMessage());
        }
    }

    public BiConsumer<String, String> set = (key, value) -> { db.put(key, value);
        write();
    };

    public Consumer<String> delete = (key) -> { db.remove(key);
        write();
    };

    public Function<String, String> get = (key) -> db.get(key);

    public Predicate<String> containsKey = (key) -> db.containsKey(key);

    private void write() {
        try (FileWriter writer = new FileWriter(dbPathTest)) {
            writer.write(new GsonBuilder().create().toJson(db));
        } catch (Exception e) {
            System.out.printf("Write to db failed!\nMsg:%s%n", e.getMessage());
        }
    }
}
