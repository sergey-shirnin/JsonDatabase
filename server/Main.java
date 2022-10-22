package server;

import java.util.Scanner;
import java.util.List;


public class Main {
    final static Scanner scanner = new Scanner(System.in);

    final static List<String> validCommands = List.of("set", "get", "delete");

    final static String EXIT = "exit";
    final static String OK = "OK";
    final static String ERROR = "ERROR";

    final static Database db = new Database(100);
    static CommandHandler handler;

    public static void main(String[] args) {
        String userEntry = scanner.nextLine();

        while (!userEntry.equalsIgnoreCase(EXIT)) {
            handler = new CommandHandler(userEntry);
            handler.processDataBase();
            userEntry = scanner.nextLine();
        }
    }
}
