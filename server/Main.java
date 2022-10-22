package server;

import com.google.gson.Gson;

import java.util.List;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.net.ServerSocket;
import java.net.Socket;


public class Main {
    private final static int PORT = 1_666;

    final static String OK = "OK";
    final static String ERROR = "ERROR";
    final static String EXIT = "EXIT";

    final static List<String> validCommands = List.of("set", "delete", "get", EXIT);

    final static Database db = new Database();

    static String requestJSON;
    static CommandHandler handler;
    static String responseJSON;

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            do {
                try (Socket socket = server.accept();
                     DataInputStream input = new DataInputStream(socket.getInputStream());
                     DataOutputStream output = new DataOutputStream(socket.getOutputStream()))
                {
                    System.out.println("Server started!");

                    requestJSON = input.readUTF();
                    handler = new CommandHandler(requestJSON);

                    responseJSON = new Gson().toJson(handler.processDataBase());
                    output.writeUTF(responseJSON);
                }
            } while (!handler.command.equalsIgnoreCase(EXIT));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
