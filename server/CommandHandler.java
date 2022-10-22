package server;


import static server.Main.db;
import static server.Main.validCommands;
import static server.Main.OK;
import static server.Main.ERROR;


public class CommandHandler {
    private final String command;
    private Integer cell;
    private String value;

    String status = OK;

    CommandHandler(String request) {
        String[] requestArr = request.split("\\s+", 3);
        this.command = requestArr[0];
        if (requestArr.length > 1) { this.cell = Integer.parseInt(requestArr[1]); }
        if (requestArr.length > 2) { this.value = requestArr[2]; }
    }


    private void setStatus() {
        if (null != cell && Math.max(1, cell) != Math.min(cell, db.getSize())
                || null != cell && "get".equalsIgnoreCase(command) && db.get.apply(cell).isEmpty()
                || !validCommands.contains(command)) {
            this.status = ERROR;
        }
    }

    public String processDataBase() {
        setStatus();
        if (status.equals(OK)) {
            switch (command) {
                case "get" -> {
                    return db.get.apply(cell);
                }
                case "set" ->  {
                    db.set(cell, value);
                    return status;
                }
                case "delete" -> {
                    db.delete.accept(cell);
                    return status;
                }
            }
        }
        return status;
    }
}
