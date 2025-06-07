package dev.nerdysoft_tech_task.service;

import dev.nerdysoft_tech_task.dto.BookDto;
import dev.nerdysoft_tech_task.dto.BookSearchParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.Set;

public interface BookService {

    BookDto findById(Long id);

    Page<BookDto> findAll(BookSearchParams params, Pageable pageable);

    Set<String> findAllBorrowedBooksTitles();

    Map<String, Integer> findAllBorrowedBooksTitlesWithBorrowedAmount();

    BookDto createBook(BookDto dto);

    BookDto updateBook(Long id, BookDto dto);

    void deleteBook(Long id);
}
