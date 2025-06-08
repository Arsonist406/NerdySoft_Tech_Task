package dev.nerdysoft_tech_task.service.impl;

import dev.nerdysoft_tech_task.dto.BookDTO;
import dev.nerdysoft_tech_task.dto.BorrowedBookDTO;
import dev.nerdysoft_tech_task.exception.CantBeDeletedException;
import dev.nerdysoft_tech_task.exception.NotFoundException;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    @Transactional(readOnly = true)
    public BookDTO findById(
            Long id
    ) {
        Book book = bookRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found by id " + id));

        return bookMapper.dto(book);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookDTO> findAll(
            Pageable pageable
    ) {
        Page<Book> bookPage = bookRepository.findAll(pageable);

        return bookPage.map(bookMapper::dto);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<BorrowedBookDTO> findAllBorrowedBooksTitles(
            Boolean showAmountBorrowed
    ) {
        List<Book> books = bookRepository.findAll();

        return books
                .stream()
                .filter(book -> !book.getBorrowingMembers().isEmpty())
                .map(book -> BorrowedBookDTO
                        .builder()
                        .title(book.getTitle())
                        .amountBorrowed(showAmountBorrowed ? book.getBorrowingMembers().size() : null)
                        .build())
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public BookDTO createBook(
            BookDTO dto
    ) {
        Optional<Book> optional = bookRepository
                .findByTitleAndAuthor(dto.title(), dto.author());

        Book book;
        if (optional.isPresent()) {
            book = optional.get();

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

        book = bookRepository.save(book);
        return bookMapper.dto(book);
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

        updateAmountIfNotNullAndNotEquals(book, dto);

        book = bookRepository.save(book);
        return bookMapper.dto(book);
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
