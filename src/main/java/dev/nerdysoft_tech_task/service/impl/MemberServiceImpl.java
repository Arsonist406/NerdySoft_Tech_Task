package dev.nerdysoft_tech_task.service.impl;

import dev.nerdysoft_tech_task.dto.BookDTO;
import dev.nerdysoft_tech_task.dto.MemberDTO;
import dev.nerdysoft_tech_task.exception.BookCantBeBorrowedException;
import dev.nerdysoft_tech_task.exception.CantBeDeletedException;
import dev.nerdysoft_tech_task.exception.NotFoundException;
import dev.nerdysoft_tech_task.mapper.BookMapper;
import dev.nerdysoft_tech_task.mapper.MemberMapper;
import dev.nerdysoft_tech_task.model.Book;
import dev.nerdysoft_tech_task.model.Member;
import dev.nerdysoft_tech_task.repository.MemberRepository;
import dev.nerdysoft_tech_task.service.BookService;
import dev.nerdysoft_tech_task.service.MemberService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final BookService bookService;
    private final BookMapper bookMapper;

    @Value("${custom.validation.bookBorrowLimit:10}")
    private Integer borrowLimit;

    @Override
    public MemberDTO findById(
            Long id
    ) {
        Member member = memberRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Member not found by id " + id));

        return memberMapper.toDTO(member);
    }

    @Override
    public Set<BookDTO> findMemberBooks(
            Long id
    ) {
        Member member = memberRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Member not found by id " + id));

        Set<Book> borrowedBooks = member.getBorrowedBooks();

        return borrowedBooks
                .stream()
                .map(bookMapper::toDTO)
                .collect(Collectors.toSet());
    }

    @Override
    public Page<MemberDTO> findAll(
            String name,
            Pageable pageable
    ) {
        Page<Member> memberPage = memberRepository.findAll(addNameSpecification(name), pageable);

        return memberPage.map(memberMapper::toDTO);
    }

    private Specification<Member> addNameSpecification(
            String name
    ) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(name)) {
                predicates.add(builder.equal(
                        root.get("name"),
                        name
                ));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    @Transactional
    public MemberDTO createMember(
            MemberDTO dto
    ) {
        Member member = Member
                .builder()
                .name(dto.name())
                .membershipDate(LocalDateTime.now())
                .borrowedBooks(new HashSet<>())
                .build();

        Member savedMember = memberRepository.save(member);
        return memberMapper.toDTO(savedMember);
    }

    @Override
    @Transactional
    public MemberDTO updateMember(
            Long id,
            MemberDTO dto
    ) {
        Member member = memberRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Member not found by id " + id));

        updateNameIfHasTextAndNotEquals(member, dto);

        Member savedMember = memberRepository.save(member);
        return memberMapper.toDTO(savedMember);
    }

    private void updateNameIfHasTextAndNotEquals(
            Member member,
            MemberDTO dto
    ) {
        String oldName = member.getName();
        String newName = dto.name();
        if (StringUtils.hasText(newName) && !newName.equals(oldName)) {
            member.setName(newName);
        }
    }

    @Override
    @Transactional
    public void deleteMember(
            Long id
    ) {
        Member member = memberRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Member not found by id " + id));

        if (!member.getBorrowedBooks().isEmpty()) {
            throw new CantBeDeletedException("Member can't be deleted because he hasn't return all borrowed books yet");
        }

        memberRepository.delete(member);
    }

    @Override
    @Transactional
    public Set<BookDTO> updateBorrowedBooks(
            Long memberId,
            Long bookId
    ) {
        Member member = memberRepository
                .findById(memberId)
                .orElseThrow(() -> new NotFoundException("Member not found by id " + memberId));

        Set<Long> membersBorrowedBooksIds = member.getBorrowedBooks()
                .stream()
                .map(Book::getId)
                .collect(Collectors.toSet());

        BookDTO bookDto = bookService.findById(bookId);

        if (membersBorrowedBooksIds.contains(bookId)) {
            returnBook(member, bookDto);
        } else {
            borrowBook(member, bookDto);
        }

        Member savedMember = memberRepository.save(member);
        return savedMember.getBorrowedBooks()
                .stream()
                .map(bookMapper::toDTO)
                .collect(Collectors.toSet());
    }

    private void returnBook(
            Member member,
            BookDTO bookDto
    ) {
        BookDTO bookDtoWithAmountPlus1 = BookDTO
                .builder()
                .id(bookDto.id())
                .title(bookDto.title())
                .author(bookDto.author())
                .amount(bookDto.amount() + 1)
                .build();

        BookDTO updatedBookDTO = bookService.updateBook(bookDtoWithAmountPlus1.id(), bookDtoWithAmountPlus1);
        member.getBorrowedBooks()
                .removeIf(book -> book.getId().equals(updatedBookDTO.id()));
    }

    private void borrowBook(
            Member member,
            BookDTO bookDto
    ) {
        checkIfBookAmountIsZero(bookDto);
        checkIfMemberBorrowedMaxAllowedAmountOfBooks(member);

        BookDTO bookDtoWithAmountMinus1 = BookDTO
                .builder()
                .id(bookDto.id())
                .title(bookDto.title())
                .author(bookDto.author())
                .amount(bookDto.amount() - 1)
                .build();

        BookDTO updatedBookDTO = bookService.updateBook(bookDtoWithAmountMinus1.id(), bookDtoWithAmountMinus1);
        member.getBorrowedBooks()
                .add(bookMapper.toEntity(updatedBookDTO));
    }

    private void checkIfBookAmountIsZero(
            BookDTO dto
    ) {
        if (dto.amount() == 0) {
            throw new BookCantBeBorrowedException("Amount of books with id " + dto.id() + " is 0");
        }
    }

    private void checkIfMemberBorrowedMaxAllowedAmountOfBooks(
            Member member
    ) {
        if (member.getBorrowedBooks().size() >= borrowLimit) {
            throw new BookCantBeBorrowedException("Member with id " + member.getId() +
                    " borrowed max allowed (" + borrowLimit + ") amount of books");
        }
    }
}
