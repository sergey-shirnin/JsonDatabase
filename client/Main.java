package client;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;

import java.io.*;

import java.net.Socket;

public class Main {

    private final static String SERVER_IP = "127.0.0.1";
    private final static int SERVER_PORT = 1_666;

    final static String jsonPath = System.getProperty("user.dir") + "/JSON Database/task/src/client/data/";
    final static String jsonPathTest = System.getProperty("user.dir") + "/src/client/data/";

    static String outMsg;

    public static void main(String[] args) {
        Args jArgs = new Args();
        JCommander.newBuilder().addObject(jArgs).build().parse(args);

        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream()))
        {
            System.out.println("Client started!");

            if (null == jArgs.getType()) {
                BufferedReader reader = new BufferedReader(new FileReader(
                        jsonPathTest + jArgs.getFileName()));
                outMsg = reader.readLine();
            } else {
                outMsg = new Gson().toJson(jArgs);
            }

            output.writeUTF(outMsg);
            System.out.println(String.join(":\s", "Sent", outMsg));

            String inMsg = input.readUTF();
            System.out.println(String.join(":\s", "Received", inMsg));
        } catch (IOException e) {
            System.out.printf("Client failed at initiation!\nMsg:%s%n", e.getMessage());
        }
    }
}
