package dev.nerdysoft_tech_task.service.impl;

import dev.nerdysoft_tech_task.dto.BookDTO;
import dev.nerdysoft_tech_task.dto.MemberDTO;
import dev.nerdysoft_tech_task.exception.BookCantBeBorrowedException;
import dev.nerdysoft_tech_task.exception.CantBeDeletedException;
import dev.nerdysoft_tech_task.mapper.BookMapper;
import dev.nerdysoft_tech_task.mapper.MemberMapper;
import dev.nerdysoft_tech_task.model.Book;
import dev.nerdysoft_tech_task.model.Member;
import dev.nerdysoft_tech_task.repository.MemberRepository;
import dev.nerdysoft_tech_task.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberMapper memberMapper;

    @Mock
    private BookService bookService;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private MemberServiceImpl memberService;


    @Test
    void findById_whenFound_returnMember() {
        Member member = new Member(1L, "Name", LocalDateTime.now(), new HashSet<>());
        MemberDTO expected = new MemberDTO(1L, "Name", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(member.getMembershipDate()));

        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(member));
        when(memberMapper.toDTO(member))
                .thenReturn(expected);

        MemberDTO actual = memberService.findById(1L);

        assertEquals(expected, actual);
        verify(memberRepository).findById(1L);
        verify(memberMapper).toDTO(member);
    }

    @Test
    void findMemberBooks_whenFound_returnBorrowedBooksByMember() {
        Book book1 = new Book(1L, "Title1", "Name Surname1", 10, new HashSet<>());
        Book book2 = new Book(2L, "Title2", "Name Surname2", 5, new HashSet<>());
        Book book3 = new Book(3L, "Title3", "Name Surname3", 13, new HashSet<>());
        Set<Book> books = Set.of(book1, book2, book3);
        BookDTO dto1 = new BookDTO(1L, "Title1", "Name Surname1", 10);
        BookDTO dto2 = new BookDTO(2L, "Title2", "Name Surname2", 5);
        BookDTO dto3 = new BookDTO(3L, "Title3", "Name Surname3", 13);
        Set<BookDTO> expected = Set.of(dto1, dto2, dto3);
        Member member = new Member(1L, "Name", LocalDateTime.now(), books);

        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(member));
        when(bookMapper.toDTO(book1))
                .thenReturn(dto1);
        when(bookMapper.toDTO(book2))
                .thenReturn(dto2);
        when(bookMapper.toDTO(book3))
                .thenReturn(dto3);

        Set<BookDTO> actual = memberService.findMemberBooks(1L);

        assertEquals(expected, actual);
        verify(memberRepository).findById(1L);
        verify(bookMapper, times(3)).toDTO(any(Book.class));
    }

    @Test
    void findAll_whenNameIsNull_returnAllMembers() {
        List<Member> members = List.of(
                new Member(1L, "Name1", LocalDateTime.now(), new HashSet<>()),
                new Member(2L, "Name2", LocalDateTime.now(), new HashSet<>()),
                new Member(3L, "Name3", LocalDateTime.now(), new HashSet<>())
        );
        List<MemberDTO> membersDTOs = List.of(
                new MemberDTO(1L, "Name1", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(members.get(0).getMembershipDate())),
                new MemberDTO(2L, "Name2", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(members.get(1).getMembershipDate())),
                new MemberDTO(3L, "Name3", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(members.get(2).getMembershipDate()))
        );
        Pageable pageable = PageRequest.of(0, 10);
        Page<Member> page =  new PageImpl<>(members, pageable, membersDTOs.size());
        Page<MemberDTO> expected =  new PageImpl<>(membersDTOs, pageable, membersDTOs.size());

        when(memberRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);
        when(memberMapper.toDTO(members.get(0)))
                .thenReturn(membersDTOs.get(0));
        when(memberMapper.toDTO(members.get(1)))
                .thenReturn(membersDTOs.get(1));
        when(memberMapper.toDTO(members.get(2)))
                .thenReturn(membersDTOs.get(2));

        Page<MemberDTO> actual = memberService.findAll(null, pageable);

        assertEquals(expected, actual);
        verify(memberRepository).findAll(any(Specification.class), eq(pageable));
        verify(memberMapper, times(3)).toDTO(any(Member.class));
    }

    @Test
    void findAll_whenNameIsNotNull_returnAllMembersWithGIvenName() {
        List<Member> members = List.of(
                new Member(1L, "Name1", LocalDateTime.now(), new HashSet<>()),
                new Member(4L, "Name1", LocalDateTime.now(), new HashSet<>())
        );
        List<MemberDTO> membersDTOs = List.of(
                new MemberDTO(1L, "Name1", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(members.get(0).getMembershipDate())),
                new MemberDTO(4L, "Name1", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(members.get(1).getMembershipDate()))
        );
        Pageable pageable = PageRequest.of(0, 10);
        Page<Member> page =  new PageImpl<>(members, pageable, membersDTOs.size());
        Page<MemberDTO> expected =  new PageImpl<>(membersDTOs, pageable, membersDTOs.size());

        when(memberRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);
        when(memberMapper.toDTO(members.get(0)))
                .thenReturn(membersDTOs.get(0));
        when(memberMapper.toDTO(members.get(1)))
                .thenReturn(membersDTOs.get(1));

        Page<MemberDTO> actual = memberService.findAll("Name1", pageable);

        assertEquals(expected.getContent().get(0), actual.getContent().get(0));
        assertEquals(expected.getContent().get(1), actual.getContent().get(1));
        verify(memberRepository).findAll(any(Specification.class), eq(pageable));
        verify(memberMapper, times(2)).toDTO(any(Member.class));
    }

    @Test
    void createMember_whenCreated_returnNewMember() {
        Member member = new Member(1L, "Name", LocalDateTime.now(), new HashSet<>());
        MemberDTO expected = new MemberDTO(1L, "Name", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(member.getMembershipDate()));

        when(memberRepository.save(any(Member.class)))
                .thenReturn(member);
        when(memberMapper.toDTO(member))
                .thenReturn(expected);

        MemberDTO actual = memberService.createMember(expected);

        assertEquals(expected, actual);
        verify(memberRepository).save(any(Member.class));
        verify(memberMapper).toDTO(member);
    }

    @Test
    void updateMember_whenUpdated_returnUpdatedMemberDTO() {
        Member member = new Member(1L, "Name", LocalDateTime.now(), new HashSet<>());
        MemberDTO expected = new MemberDTO(1L, "NewName", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(member.getMembershipDate()));

        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class)))
                .thenReturn(member);
        when(memberMapper.toDTO(member))
                .thenReturn(expected);

        MemberDTO actual = memberService.updateMember(1L, expected);

        assertEquals(expected, actual);
        verify(memberRepository).findById(1L);
        verify(memberRepository).save(any(Member.class));
        verify(memberMapper).toDTO(member);
    }

    @Test
    void deleteMember_whenDeleted_returnNothing() {
        Member member = new Member(1L, "Name", LocalDateTime.now(), new HashSet<>());

        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(member));
        doNothing().when(memberRepository)
                .delete(member);

        memberService.deleteMember(1L);

        verify(memberRepository).findById(1L);
        verify(memberRepository).delete(member);
    }

    @Test
    void deleteMember_whenMemberHasBorrowedBooks_throwsCantBeDeletedException() {
        Member member = new Member(1L, "Name", LocalDateTime.now(), Set.of(mock(Book.class)));

        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(member));

        assertThrows(
                CantBeDeletedException.class,
                () -> memberService.deleteMember(1L)
        );

        verify(memberRepository).findById(1L);
    }

    @Test
    void updateBorrowedBooks_whenBookWithGivenBookIdIsAlreadyBorrowedByMember_removeBookFromMembersBorrowedBooksAndReturnUpdatedSetOfMembersBorrowedBooks() {
        Book book1 = new Book(1L, "Title1", "Name Surname1", 1, new HashSet<>());
        Book book2 = new Book(2L, "Title2", "Name Surname2", 1, new HashSet<>());
        Set<Book> twoBooks = new HashSet<>();
        twoBooks.add(book1);
        twoBooks.add(book2);
        Set<Book> oneBook = new HashSet<>();
        oneBook.add(book2);
        Member memberWithTwoBooks = new Member(1L, "Name", LocalDateTime.now(), twoBooks);
        Member memberWithOneBook = new Member(1L, "Name", memberWithTwoBooks.getMembershipDate(), oneBook);
        BookDTO dto1 = new BookDTO(1L, "Title1", "Name Surname1", 1);
        BookDTO dto2 = new BookDTO(2L, "Title2", "Name Surname2", 2);
        BookDTO updatedBookDto = new BookDTO(1L, "Title1", "Name Surname1", 2);
        Set<BookDTO> expected = Set.of(dto2);

        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(memberWithTwoBooks));
        when(bookService.findById(1L))
                .thenReturn(dto1);
        when(bookService.updateBook(eq(1L), any(BookDTO.class)))
                .thenReturn(updatedBookDto);
        when(memberRepository.save(memberWithOneBook))
                .thenReturn(memberWithOneBook);
        when(bookMapper.toDTO(book2))
                .thenReturn(dto2);

        Set<BookDTO> actual = memberService.updateBorrowedBooks(1L, 1L);

        assertEquals(expected, actual);
        verify(memberRepository).findById(1L);
        verify(bookService).findById(1L);
        verify(bookService).updateBook(eq(1L), any(BookDTO.class));
        verify(memberRepository).save(any(Member.class));
        verify(bookMapper).toDTO(book2);
    }

    @Test
    void updateBorrowedBooks_whenBookWithGivenBookIdIsNotBorrowedByMember_addBookToMembersBorrowedBooksAndReturnUpdatedSetOfMembersBorrowedBooks() {
        ReflectionTestUtils.setField(memberService, "borrowLimit", 10);
        Book book1 = new Book(1L, "Title1", "Name Surname1", 1, new HashSet<>());
        Book book2 = new Book(2L, "Title2", "Name Surname2", 1, new HashSet<>());
        Set<Book> twoBooks = new HashSet<>();
        twoBooks.add(book1);
        twoBooks.add(book2);
        Set<Book> oneBook = new HashSet<>();
        oneBook.add(book2);
        Member memberWithTwoBooks = new Member(1L, "Name", LocalDateTime.now(), twoBooks);
        Member memberWithOneBook = new Member(1L, "Name", memberWithTwoBooks.getMembershipDate(), oneBook);
        BookDTO dto1 = new BookDTO(1L, "Title1", "Name Surname1", 1);
        BookDTO dto2 = new BookDTO(2L, "Title2", "Name Surname2", 2);
        BookDTO updatedBookDto = new BookDTO(1L, "Title1", "Name Surname1", 2);
        Set<BookDTO> expected = Set.of(dto1, dto2);

        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(memberWithOneBook));
        when(bookService.findById(1L))
                .thenReturn(dto1);
        when(bookService.updateBook(eq(1L), any(BookDTO.class)))
                .thenReturn(updatedBookDto);
        when(bookMapper.toEntity(updatedBookDto))
                .thenReturn(book1);
        when(memberRepository.save(memberWithTwoBooks))
                .thenReturn(memberWithTwoBooks);
        when(bookMapper.toDTO(book1))
                .thenReturn(dto1);
        when(bookMapper.toDTO(book2))
                .thenReturn(dto2);

        Set<BookDTO> actual = memberService.updateBorrowedBooks(1L, 1L);

        assertEquals(expected, actual);
        verify(memberRepository).findById(1L);
        verify(bookService).findById(1L);
        verify(bookService).updateBook(eq(1L), any(BookDTO.class));
        verify(bookMapper).toEntity(updatedBookDto);
        verify(memberRepository).save(memberWithTwoBooks);
        verify(bookMapper).toDTO(book1);
        verify(bookMapper).toDTO(book2);
    }

    @Test
    void updateBorrowedBooks_whenBookWithGivenBookIdIsNotBorrowedByMemberAndBookAmountIsZero_throwsBookCantBeBorrowedException() {
        Member memberWithOneBook = new Member(1L, "Name", LocalDateTime.now(), new HashSet<>());
        BookDTO dto = new BookDTO(1L, "Title1", "Name Surname1", 0);

        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(memberWithOneBook));
        when(bookService.findById(1L))
                .thenReturn(dto);

        assertThrows(
                BookCantBeBorrowedException.class,
                () -> memberService.updateBorrowedBooks(1L, 1L)
        );

        verify(memberRepository).findById(1L);
        verify(bookService).findById(1L);
    }

    @Test
    void updateBorrowedBooks_whenBookWithGivenBookIdIsNotBorrowedByMemberAndMemberBorrowedMaxAllowedAmountOfBooks_throwsBookCantBeBorrowedException() {
        ReflectionTestUtils.setField(memberService, "borrowLimit", 1);
        Book book = new Book(2L, "Title2", "Name Surname2", 1, new HashSet<>());
        Member memberWithOneBook = new Member(1L, "Name", LocalDateTime.now(), Set.of(book));
        BookDTO dto = new BookDTO(1L, "Title", "Name Surname1", 1);

        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(memberWithOneBook));
        when(bookService.findById(1L))
                .thenReturn(dto);

        assertThrows(
                BookCantBeBorrowedException.class,
                () -> memberService.updateBorrowedBooks(1L, 1L)
        );

        verify(memberRepository).findById(1L);
        verify(bookService).findById(1L);
    }
}