package dev.nerdysoft_tech_task.controller;

import dev.nerdysoft_tech_task.service.BookRentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members/{member_id}/book_rental")
@RequiredArgsConstructor
public class BookRentalController {

    private final BookRentalService bookRentalService;

    @PatchMapping("/borrow/{book_id}")
    @ResponseStatus(HttpStatus.OK)
    public void borrowBook(
            @PathVariable("member_id") Long memberId,
            @PathVariable("book_id") Long bookId
    ) {
        bookRentalService.borrowBook(memberId, bookId);
    }

    @PatchMapping("/return/{book_id}")
    @ResponseStatus(HttpStatus.OK)
    public void returnBook(
            @PathVariable("member_id") Long memberId,
            @PathVariable("book_id") Long bookId
    ) {
        bookRentalService.returnBook(memberId, bookId);
    }
}
