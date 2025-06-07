package dev.nerdysoft_tech_task.service;

public interface BookRentalService {

    void borrowBook(Long memberId, Long bookId);

    void returnBook(Long memberId, Long bookId);
}
