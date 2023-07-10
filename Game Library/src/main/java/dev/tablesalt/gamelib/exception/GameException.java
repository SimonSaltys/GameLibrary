package dev.tablesalt.gamelib.exception;

public class GameException extends RuntimeException {

    @Override
    public String getMessage() {
        return "REPORT: " + super.getMessage();
    }
}
