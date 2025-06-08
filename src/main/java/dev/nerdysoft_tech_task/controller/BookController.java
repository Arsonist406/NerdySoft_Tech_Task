package dev.nerdysoft_tech_task.controller;

import dev.nerdysoft_tech_task.dto.BookDTO;
import dev.nerdysoft_tech_task.dto.BorrowedBookDTO;
import dev.nerdysoft_tech_task.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookDTO findById(
            @PathVariable("id") Long id
    ) {
        return bookService.findById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<BookDTO> findAll(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return bookService.findAll(pageable);
    }

    @GetMapping("/borrowed")
    @ResponseStatus(HttpStatus.OK)
    public Set<BorrowedBookDTO> findAllBorrowedBooksTitles(
            @RequestParam("showAmountBorrowed") Boolean showAmountBorrowed
    ) {
        return bookService.findAllBorrowedBooksTitles(showAmountBorrowed);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO createBook(
            @RequestBody @Valid BookDTO dto
    ) {
        return bookService.createBook(dto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookDTO updateBook(
            @PathVariable("id") Long id,
            @RequestBody @Valid BookDTO dto
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
