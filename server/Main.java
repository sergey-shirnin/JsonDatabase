package server;

import java.util.List;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.net.ServerSocket;
import java.net.Socket;


public class Main {
    private final static int PORT = 1_666;

    final static String EXIT = "exit";
    final static List<String> validCommands = List.of("set", "get", "delete", EXIT);

    final static String OK = "OK";
    final static String ERROR = "ERROR";

    final static Database db = new Database(1000);
    static CommandHandler handler;

    static String request;
    static String reqResult;

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server started!");
            do {
                try (Socket socket = server.accept();
                     DataInputStream input = new DataInputStream(socket.getInputStream());
                     DataOutputStream output = new DataOutputStream(socket.getOutputStream())
                ) {
                    request = input.readUTF();
                    handler = new CommandHandler(request);
                    reqResult = handler.processDataBase();
                    output.writeUTF(reqResult);
                }
            } while (!request.equals(EXIT));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
