package dev.nerdysoft_tech_task.exception;

public class BookCantBeBorrowed extends RuntimeException {
    public BookCantBeBorrowed(String message) {
        super(message);
    }
}
