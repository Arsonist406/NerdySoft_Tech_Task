package dev.nerdysoft_tech_task.exception;

public class BookCantBeBorrowed extends BusinessException {
    public BookCantBeBorrowed(String message) {
        super(message);
    }
}
