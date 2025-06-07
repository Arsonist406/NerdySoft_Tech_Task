package dev.nerdysoft_tech_task.exception;

public class CantBeDeletedException extends BusinessException {
    public CantBeDeletedException(String message) {
        super(message);
    }
}
