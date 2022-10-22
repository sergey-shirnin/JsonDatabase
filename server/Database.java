package server;

import java.util.Arrays;
import java.util.function.*;


public class Database {
    static String[] db;

    Database(int size){
        db = new String[size];
        Arrays.fill(db, "");
    }

    public void set(int cell, String value) { db[cell - 1] = value; }

    public Function<Integer, String> get = (cell) -> db[cell - 1];

    public Consumer<Integer> delete = (cell) -> db[cell - 1] = "";

    public int getSize() {
        return db.length;
    }
}
