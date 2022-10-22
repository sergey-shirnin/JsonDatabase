package server;


import com.google.gson.Gson;

import java.util.Map;
import java.util.List;

import static server.Main.db;
import static server.Main.validCommands;

import static server.Main.OK;
import static server.Main.ERROR;


public class CommandHandler {

    final String command;

    private final String key;
    private final String value;

    String status = OK;

    CommandHandler(String requestJSON) {
        Map<String, String> requestMap = new Gson().fromJson(requestJSON, Map.class);
        this.command = requestMap.get("type");
        this.key = requestMap.get("key");
        this.value = requestMap.get("value");
    }


    private void setStatus() {
        if (List.of("get", "delete").contains(command)
                && null == db.get.apply(key)
                || !validCommands.contains(command)) {
            this.status = ERROR;
        }
    }

    public Map<String, String> processDataBase() {
        setStatus();
        if (!status.equals(OK)) {
            return Map.of("response", status, "reason", "No such key");
        }
        switch (command) {
            case "set" ->  { db.set(key, value); }
            case "delete" -> { db.delete.accept(key); }
            case "get" -> { return Map.of("response", status, "value", db.get.apply(key)); }
        }
        return Map.of("response", status);
    }
}
