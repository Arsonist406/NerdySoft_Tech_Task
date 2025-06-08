package dev.nerdysoft_tech_task.exception;

public class BookCantBeBorrowedException extends RuntimeException {
    public BookCantBeBorrowedException(String message) {
        super(message);
    }
}
