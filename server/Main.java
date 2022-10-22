package server;

import java.io.IOException;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.net.ServerSocket;
import java.net.Socket;


public class Main {
    private final static int PORT = 66_66;

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server started!");
            try (Socket socket = server.accept();
                 DataInputStream input = new DataInputStream(socket.getInputStream());
                 DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {

                String inMsg = input.readUTF();
                System.out.println(String.join(":\s", "Received", inMsg));

                String requestCell = inMsg.split("\\s*#\\s*")[1];
                String outMsg = String.join("\s", "A record #", requestCell, "was sent!");
                output.writeUTF(outMsg);
                System.out.println(String.join(":\s", "Sent", outMsg));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
