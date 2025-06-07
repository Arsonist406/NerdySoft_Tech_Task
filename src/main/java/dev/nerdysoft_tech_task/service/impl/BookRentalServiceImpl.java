package dev.nerdysoft_tech_task.service.impl;

import dev.nerdysoft_tech_task.exception.BookCantBeBorrowed;
import dev.nerdysoft_tech_task.exception.NotFoundException;
import dev.nerdysoft_tech_task.model.Book;
import dev.nerdysoft_tech_task.model.Member;
import dev.nerdysoft_tech_task.repository.BookRepository;
import dev.nerdysoft_tech_task.repository.MemberRepository;
import dev.nerdysoft_tech_task.service.BookRentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookRentalServiceImpl implements BookRentalService {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    @Value("${custom.validation.borrowLimit:10}")
    private Integer borrowLimit;

    @Override
    @Transactional
    public void borrowBook(
            Long memberId,
            Long bookId
    ) {
        Book book = bookRepository
                .findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found by id " + bookId));

        checkIfBookAmountIsZero(book);

        Member member = memberRepository
                .findById(memberId)
                .orElseThrow(() -> new NotFoundException("Member not found by id " + memberId));

        checkIfMemberBorrowedMaxAllowedAmountOfBooks(member);
        checkIfMemberAlreadyBorrowedThisBook(member, book);

        book.setAmount(book.getAmount() - 1);
        member.getBorrowedBooks().add(book);

        memberRepository.save(member);
    }

    private void checkIfBookAmountIsZero(
            Book book
    ) {
        if (book.getAmount() == 0) {
            throw new BookCantBeBorrowed("Amount of books with id " + book.getId() + " is 0");
        }
    }

    private void checkIfMemberBorrowedMaxAllowedAmountOfBooks(
            Member member
    ) {
        if (member.getBorrowedBooks().size() >= borrowLimit) {
            throw new BookCantBeBorrowed("Member with id " + member.getId() +
                    " borrowed max allowed (" + borrowLimit + ") amount of books");
        }
    }

    private void checkIfMemberAlreadyBorrowedThisBook(
            Member member,
            Book book
    ) {
        if (member.getBorrowedBooks().contains(book)) {
            throw new BookCantBeBorrowed("Book with id " + book.getId() +
                    " is already borrowed by member with id " + member.getId());
        }
    }

    @Override
    @Transactional
    public void returnBook(
            Long memberId,
            Long bookId
    ) {
        Book book = bookRepository
                .findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found by id " + bookId));

        Member member = memberRepository
                .findById(memberId)
                .orElseThrow(() -> new NotFoundException("Member not found by id " + memberId));

        book.setAmount(book.getAmount() + 1);
        member.getBorrowedBooks().remove(book);

        memberRepository.save(member);
    }
}
