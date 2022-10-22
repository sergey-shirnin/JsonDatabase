package client;

import java.io.IOException;

import java.net.Socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;


public class Main {
    private final static String SERVER_IP = "127.0.0.1";
    private final static int SERVER_PORT = 66_66;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream()))
        {
            System.out.println("Client started!");

            String outMsg = "Give me a record # 666";
            output.writeUTF(outMsg);
            System.out.println(String.join(":\s", "Sent", outMsg));

            String inMsg = input.readUTF();
            System.out.println(String.join(":\s", "Received", inMsg));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
