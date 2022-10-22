package server;

import static server.Main.db;
import static server.Main.validCommands;
import static server.Main.OK;
import static server.Main.ERROR;


public class CommandHandler {
    private final String[] userEntryArr;
    private final String command;
    private final int cell;
    private String value;

    private String status = OK;

    CommandHandler(String userEntry) {
        userEntryArr = userEntry.split("\\s+", 3);
        this.command = userEntryArr[0].toLowerCase();
        this.cell = Integer.parseInt(userEntryArr[1]);
        if (userEntryArr.length == 3) { this.value = userEntryArr[2]; }
    }

    private void setStatus() {
        if (Math.max(1, cell) != Math.min(cell, db.getLength())
                || "get".equalsIgnoreCase(command) && db.get.apply(cell).isEmpty()
                || !validCommands.contains(command)) {
            this.status = ERROR;
        }
    }

    public void processDataBase() {
        setStatus();
        if (status.equals(OK)) {
            switch (command) {
                case "get" -> System.out.println(db.get.apply(cell));
                case "set" -> db.set(cell, value);
                case "delete" -> db.delete.accept(cell);
            }
        }

        if (!"get".equals(command) || !OK.equals(status)) {
            System.out.println(status);
        }
    }
}