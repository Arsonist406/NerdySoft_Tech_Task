package dev.nerdysoft_tech_task.service;

import dev.nerdysoft_tech_task.dto.BookDTO;
import dev.nerdysoft_tech_task.dto.BorrowedBookDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface BookService {

    BookDTO findById(Long id);

    Page<BookDTO> findAll(Pageable pageable);

    Set<BorrowedBookDTO> findAllBorrowedBooksTitles(Boolean showAmountBorrowed);

    BookDTO createBook(BookDTO dto);

    BookDTO updateBook(Long id, BookDTO dto);

    void deleteBook(Long id);
}
