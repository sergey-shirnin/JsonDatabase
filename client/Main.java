package client;

import com.beust.jcommander.JCommander;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.net.Socket;


public class Main {

    private final static String SERVER_IP = "127.0.0.1";
    private final static int SERVER_PORT = 1_666;

    public static void main(String[] args) {
        Args jArgs = new Args();
        JCommander.newBuilder().addObject(jArgs).build().parse(args);

        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream()))
        {
            System.out.println("Client started!");

            String outMsg = jArgs.getRequestString();
            output.writeUTF(outMsg);
            System.out.println(String.join(":\s", "Sent", outMsg));

            String inMsg = input.readUTF();
            System.out.println(String.join(":\s", "Received", inMsg));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
