package dev.nerdysoft_tech_task.controller;

import dev.nerdysoft_tech_task.dto.BookDto;
import dev.nerdysoft_tech_task.dto.BookSearchParams;
import dev.nerdysoft_tech_task.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookDto findById(
            @PathVariable("id") Long id
    ) {
        return bookService.findById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<BookDto> findAll(
            BookSearchParams params,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return bookService.findAll(params, pageable);
    }

    @GetMapping("/borrowed_books")
    @ResponseStatus(HttpStatus.OK)
    public Set<String> findAllBorrowedBooksTitles() {
        return bookService.findAllBorrowedBooksTitles();
    }

    @GetMapping("/borrowed_books_with_borrowed_amount")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Integer> findAllBorrowedBooksTitlesWithBorrowedAmount() {
        return bookService.findAllBorrowedBooksTitlesWithBorrowedAmount();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto createBook(
            @RequestBody @Valid BookDto dto
    ) {
        return bookService.createBook(dto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookDto updateBook(
            @PathVariable("id") Long id,
            @RequestBody @Valid BookDto dto
    ) {
        return bookService.updateBook(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(
            @PathVariable("id") Long id
    ) {
        bookService.deleteBook(id);
    }
}
