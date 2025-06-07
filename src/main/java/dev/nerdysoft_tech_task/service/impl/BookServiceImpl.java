package dev.nerdysoft_tech_task.service.impl;

import dev.nerdysoft_tech_task.dto.BookDto;
import dev.nerdysoft_tech_task.dto.BookSearchParams;
import dev.nerdysoft_tech_task.exception.CantBeDeletedException;
import dev.nerdysoft_tech_task.exception.NotFoundException;
import dev.nerdysoft_tech_task.mapper.BookMapper;
import dev.nerdysoft_tech_task.model.Book;
import dev.nerdysoft_tech_task.repository.BookRepository;
import dev.nerdysoft_tech_task.service.BookService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    @Transactional(readOnly = true)
    public BookDto findById(
            Long id
    ) {
        Book book = bookRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found by id " + id));

        return bookMapper.dto(book);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookDto> findAll(
            BookSearchParams params,
            Pageable pageable
    ) {
        Page<Book> bookPage = bookRepository.findAll(buildSpecification(params), pageable);

        return bookPage.map(bookMapper::dto);
    }

    private Specification<Book> buildSpecification(
            BookSearchParams params
    ) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            addEqualPredicateIfHasText(builder, predicates, root,
                    "title", params.title());

            addEqualPredicateIfHasText(builder, predicates, root,
                    "author", params.author());

            addGreaterThanOrEqualPredicateIfNotNull(builder, predicates, root,
                    "amount", params.fromAmount());

            addLessThanOrEqualPredicateIfNotNull(builder, predicates, root,
                    "amount", params.toAmount());

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void addEqualPredicateIfHasText(
            CriteriaBuilder builder,
            List<Predicate> predicates,
            Root<?> root,
            String fieldName,
            String value
    ) {
        if (StringUtils.hasText(value)) {
            predicates.add(builder.equal(
                    root.get(fieldName),
                    value
            ));
        }
    }

    private void addGreaterThanOrEqualPredicateIfNotNull(
            CriteriaBuilder builder,
            List<Predicate> predicates,
            Root<?> root,
            String fieldName,
            Integer value
    ) {
        if (value != null) {
            predicates.add(builder.greaterThanOrEqualTo(
                    root.get(fieldName),
                    value
            ));
        }
    }

    private void addLessThanOrEqualPredicateIfNotNull(
            CriteriaBuilder builder,
            List<Predicate> predicates,
            Root<?> root,
            String fieldName,
            Integer value
    ) {
        if (value != null) {
            predicates.add(builder.lessThanOrEqualTo(
                    root.get(fieldName),
                    value
            ));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> findAllBorrowedBooksTitles() {
        List<Book> books = bookRepository.findAll();

        return books
                .stream()
                .filter(book -> !book.getBorrowingMembers().isEmpty())
                .map(Book::getTitle)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Integer> findAllBorrowedBooksTitlesWithBorrowedAmount() {
        List<Book> books = bookRepository.findAll();

        return books
                .stream()
                .filter(book -> !book.getBorrowingMembers().isEmpty())
                .collect(Collectors.toMap(
                        Book::getTitle,
                        book -> book.getBorrowingMembers().size()
                ));
    }

    @Override
    @Transactional
    public BookDto createBook(
            BookDto dto
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
    public BookDto updateBook(
            Long id,
            BookDto dto
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
            BookDto dto
    ) {
        String oldTitle = book.getTitle();
        String newTitle = dto.title();
        if (StringUtils.hasText(newTitle) && !newTitle.equals(oldTitle)) {
            book.setTitle(newTitle);
        }
    }

    private void updateAuthorIfHasTextAndNotEquals(
            Book book,
            BookDto dto
    ) {
        String oldAuthor = book.getAuthor();
        String newAuthor = dto.author();
        if (StringUtils.hasText(newAuthor) && !newAuthor.equals(oldAuthor)) {
            book.setAuthor(newAuthor);
        }
    }

    private void updateAmountIfNotNullAndNotEquals(
            Book book,
            BookDto dto
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
