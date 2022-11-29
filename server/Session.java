package server;

import client.Args;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.io.IOException;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import static server.Main.readLock;
import static server.Main.writeLock;
import static server.Main.db;


public class Session implements Callable<Boolean> {

    private final Socket socket;

    Session(Socket socket) {
        this.socket = socket;
    }

    final static Map<String, String> ERROR = Map.of(
            "response", "ERROR",
            "reason", "No such key");
    final static Map<String, String> OK = Map.of(
            "response", "OK");

    final static String EXIT = "exit";

    static boolean isClosedByClient = false;

    @Override
    public Boolean call() {
        Map<String, String> response = ERROR;

        try (DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream()))
        {
            Args request = new Gson().fromJson(input.readUTF(), Args.class);

            switch (request.getType()) {
                // GET
                case "get" -> {
                    readLock.lock();
                    if (db.containsKey.test(request.getKey())) {
                        response = new HashMap<>(OK);
                        response.put("value", db.get.apply(request.getKey()));
                    }
                    readLock.unlock();
                }

                // SET
                case "set" -> {
                    writeLock.lock();
                    db.set.accept(request.getKey(), request.getValue());
                    response = OK;
                    writeLock.unlock();
                }

                // DELETE
                case "delete" -> {
                    writeLock.lock();
                    if (db.containsKey.test(request.getKey())) {
                        db.delete.accept(request.getKey());
                        response = OK;
                    }
                    writeLock.unlock();
                }

                // TERMINATE SESSION
                case EXIT -> {
                    response = OK;
                    isClosedByClient = true;
                }
            }

            // SEND TO CLIENT
            output.writeUTF(new Gson().toJson(response));

        } catch (IOException e) {
            System.out.printf("Client session failed at initiation!\nMsg:%s%n", e.getMessage());
        }

        return isClosedByClient;
    }
}
