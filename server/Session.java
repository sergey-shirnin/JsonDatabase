package server;

import client.Args;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.io.IOException;

import java.net.Socket;
import java.util.Map;
import java.util.concurrent.Callable;

import static server.Main.*;


public class Session implements Callable<String> {

    private final Socket socket;

    Session(Socket socket) {
        this.socket = socket;
    }

    final static Gson gson = new Gson();

    final static Map<String, String> OK = Map.of(
            "response", "OK");
    final static JsonObject ERROR = gson.toJsonTree(Map.of(
            "response", "ERROR",
            "reason", "No such key")).getAsJsonObject();

    String serverShutDownMsg = "keep running";

    static Args request = null;

    public String call() {
        JsonObject response = ERROR;

        try (DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream()))
        {
            String inputString = input.readUTF();

            request = new Gson().fromJson(inputString, Args.class);

            switch (request.getType()) {
                // GET
                case "get" -> {
                    readLock.lock();
                    if (db.hasKey.test(request.getKey())) {
                        response = gson.toJsonTree(OK).getAsJsonObject();
                        response.add("value", db.get.apply(request.getKey()));
                    }
                    readLock.unlock();
                }

                // DELETE
                case "delete" -> {
                    writeLock.lock();
                    if (db.hasKey.test(request.getKey())) {
                        db.delete.accept(request.getKey());
                        response = gson.toJsonTree(OK).getAsJsonObject();
                    }
                    writeLock.unlock();
                }

                // SET
                case "set" -> {
                    writeLock.lock();
                    db.set.accept(request.getKey(), request.getValue());
                    response = gson.toJsonTree(OK).getAsJsonObject();
                    writeLock.unlock();
                }

                // TERMINATE SESSION
                case EXIT -> {
                    response = gson.toJsonTree(OK).getAsJsonObject();
                    serverShutDownMsg = EXIT;
                }
            }

            // SEND TO CLIENT
            output.writeUTF(gson.toJson(response));

        } catch (IOException e) {
            System.out.printf("Client session failed at initiation!\nMsg:%s%n", e.getMessage());
        }

        return serverShutDownMsg;
    }
}
