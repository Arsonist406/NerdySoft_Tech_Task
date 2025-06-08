package dev.nerdysoft_tech_task.exception;

public class CantBeDeletedException extends RuntimeException {
    public CantBeDeletedException(String message) {
        super(message);
    }
}
