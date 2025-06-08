package dev.nerdysoft_tech_task.service.impl;

import dev.nerdysoft_tech_task.dto.BookDTO;
import dev.nerdysoft_tech_task.dto.BorrowedBookDTO;
import dev.nerdysoft_tech_task.exception.CantBeDeletedException;
import dev.nerdysoft_tech_task.exception.NotFoundException;
import dev.nerdysoft_tech_task.exception.NotUniqueException;
import dev.nerdysoft_tech_task.mapper.BookMapper;
import dev.nerdysoft_tech_task.model.Book;
import dev.nerdysoft_tech_task.repository.BookRepository;
import dev.nerdysoft_tech_task.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDTO findById(
            Long id
    ) {
        Book book = bookRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found by id " + id));

        return bookMapper.dto(book);
    }

    @Override
    public Page<BookDTO> findAll(
            Pageable pageable
    ) {
        Page<Book> bookPage = bookRepository.findAll(pageable);

        return bookPage.map(bookMapper::dto);
    }

    @Override
    public Set<BorrowedBookDTO> findAllBorrowedBooksTitles(
            Boolean showAmountBorrowed
    ) {
        List<Book> books = bookRepository.findAll();

        Map<String, Integer> distinctNamesAndBorrowedAmountSumByName = books
                .stream()
                .filter(book -> !book.getBorrowingMembers().isEmpty())
                .collect(Collectors.toMap(
                        Book::getTitle,
                        book -> book.getBorrowingMembers().size(),
                        Integer::sum
                ));

        return distinctNamesAndBorrowedAmountSumByName
                .entrySet()
                .stream()
                .map(entry -> BorrowedBookDTO
                        .builder()
                        .title(entry.getKey())
                        .amountBorrowed(showAmountBorrowed ? entry.getValue() : null)
                        .build())
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public BookDTO createBook(
            BookDTO dto
    ) {
        Optional<Book> bookByTitleAndAuthor = bookRepository
                .findByTitleAndAuthor(dto.title(), dto.author());

        Book book;
        if (bookByTitleAndAuthor.isPresent()) {
            book = bookByTitleAndAuthor.get();

            book.setAmount(book.getAmount() + 1);
        } else {
            book = Book
                    .builder()
                    .title(dto.title())
                    .author(dto.author())
                    .amount(1)
                    .borrowingMembers(new HashSet<>())
                    .build();
        }

        Book savedBook = bookRepository.save(book);
        return bookMapper.dto(savedBook);
    }

    @Override
    @Transactional
    public BookDTO updateBook(
            Long id,
            BookDTO dto
    ) {
        Book book = bookRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found by id " + id));

        updateTitleIfHasTextAndNotEquals(book, dto);

        updateAuthorIfHasTextAndNotEquals(book, dto);

        checkIfBookWithNewTitleAndNewAuthorIsAlreadyExist(dto);

        updateAmountIfNotNullAndNotEquals(book, dto);

        Book savedBook = bookRepository.save(book);
        return bookMapper.dto(savedBook);
    }

    private void updateTitleIfHasTextAndNotEquals(
            Book book,
            BookDTO dto
    ) {
        String oldTitle = book.getTitle();
        String newTitle = dto.title();
        if (StringUtils.hasText(newTitle) && !newTitle.equals(oldTitle)) {
            book.setTitle(newTitle);
        }
    }

    private void updateAuthorIfHasTextAndNotEquals(
            Book book,
            BookDTO dto
    ) {
        String oldAuthor = book.getAuthor();
        String newAuthor = dto.author();
        if (StringUtils.hasText(newAuthor) && !newAuthor.equals(oldAuthor)) {
            book.setAuthor(newAuthor);
        }
    }

    private void checkIfBookWithNewTitleAndNewAuthorIsAlreadyExist(
            BookDTO dto
    ) {
        Optional<Book> bookByTitleAndAuthor = bookRepository
                .findByTitleAndAuthor(dto.title(), dto.author());

        if (bookByTitleAndAuthor.isPresent()) {
            throw new NotUniqueException("Book with given title and author is already exist");
        }
    }

    private void updateAmountIfNotNullAndNotEquals(
            Book book,
            BookDTO dto
    ) {
        Integer oldAmount = book.getAmount();
        Integer newAmount = dto.amount();
        if (newAmount != null && !newAmount.equals(oldAmount)) {
            book.setAmount(newAmount);
        }
    }

    @Override
    @Transactional
    public void deleteBook(
            Long id
    ) {
        Book book = bookRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found by id " + id));

        if (!book.getBorrowingMembers().isEmpty()) {
            throw new CantBeDeletedException("Book can't be deleted because it was borrowed by member");
        }

        bookRepository.delete(book);
    }
}
