package dev.nerdysoft_tech_task.exception;

public class BookCantBeBorrowedException extends BusinessException {
    public BookCantBeBorrowedException(String message) {
        super(message);
    }
}
