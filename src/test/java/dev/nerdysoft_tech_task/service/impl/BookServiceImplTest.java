package dev.nerdysoft_tech_task.service.impl;

import dev.nerdysoft_tech_task.dto.BookDTO;
import dev.nerdysoft_tech_task.dto.BorrowedBookDTO;
import dev.nerdysoft_tech_task.exception.CantBeDeletedException;
import dev.nerdysoft_tech_task.exception.NotFoundException;
import dev.nerdysoft_tech_task.exception.NotUniqueException;
import dev.nerdysoft_tech_task.mapper.BookMapper;
import dev.nerdysoft_tech_task.model.Book;
import dev.nerdysoft_tech_task.model.Member;
import dev.nerdysoft_tech_task.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void findById_whenFound_returnBookDto() {
        Book book = new Book(1L, "Title", "Name Surname", 1, new HashSet<>());
        BookDTO dto = new BookDTO(1L, "Title", "Name Surname", 1);

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));
        when(bookMapper.dto(book))
                .thenReturn(dto);

        BookDTO actual = bookService.findById(1L);

        assertEquals(dto, actual);
        verify(bookRepository).findById(1L);
        verify(bookMapper).dto(book);
    }

    @Test
    void findAll_whenSuccessfully_returnPageOfBookDTO() {
        List<Book> books = List.of(
                new Book(1L, "Title1", "Name Surname1", 10, new HashSet<>()),
                new Book(2L, "Title2", "Name Surname2", 5, new HashSet<>()),
                new Book(3L, "Title3", "Name Surname3", 13, new HashSet<>())
        );
        List<BookDTO> bookDTOS = List.of(
                new BookDTO(1L, "Title1", "Name Surname1", 10),
                new BookDTO(2L, "Title2", "Name Surname2", 5),
                new BookDTO(3L, "Title3", "Name Surname3", 13)
        );
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> page =  new PageImpl<>(books, pageable, bookDTOS.size());
        Page<BookDTO> expected =  new PageImpl<>(bookDTOS, pageable, bookDTOS.size());

        when(bookRepository.findAll(pageable))
                .thenReturn(page);
        when(bookMapper.dto(books.get(0)))
                .thenReturn(bookDTOS.get(0));
        when(bookMapper.dto(books.get(1)))
                .thenReturn(bookDTOS.get(1));
        when(bookMapper.dto(books.get(2)))
                .thenReturn(bookDTOS.get(2));

        Page<BookDTO> actual = bookService.findAll(pageable);

        assertEquals(expected, actual);
        verify(bookRepository).findAll(pageable);
        verify(bookMapper, times(3)).dto(any(Book.class));
    }

    @Test
    void findAllBorrowedBooksTitles_whenShowAmountBorrowedIsTrue_returnBorrowedBookDTOsWithoutAmount() {
        Set<Member> borrowingMemberIs1 = Set.of(mock(Member.class));
        List<Book> books = List.of(
                new Book(1L, "Title1", "Name Surname1", 1, borrowingMemberIs1),
                new Book(11L, "Title1", "Name Surname11", 11, borrowingMemberIs1),
                new Book(2L, "Title2", "Name Surname2", 2, borrowingMemberIs1),
                new Book(3L, "Title3", "Name Surname3", 3, borrowingMemberIs1),
                new Book(4L, "Title4", "Name Surname4", 4, borrowingMemberIs1)
        );
        Set<BorrowedBookDTO> expected = Set.of(
                new BorrowedBookDTO("Title1", null),
                new BorrowedBookDTO("Title2", null),
                new BorrowedBookDTO("Title3", null),
                new BorrowedBookDTO("Title4", null)
        );

        when(bookRepository.findAll())
                .thenReturn(books);

        Set<BorrowedBookDTO> actual = bookService.findAllBorrowedBooksTitles(false);

        assertEquals(expected, actual);
        verify(bookRepository).findAll();
    }

    @Test
    void findAllBorrowedBooksTitles_whenShowAmountBorrowedIsFalse_returnBorrowedBookDTOsWithBorrowedAmountSumByName() {
        Set<Member> borrowingMemberIs1 = Set.of(mock(Member.class));
        Set<Member> borrowingMemberIs11 = Set.of(
                mock(Member.class), mock(Member.class), mock(Member.class), mock(Member.class),
                mock(Member.class), mock(Member.class), mock(Member.class), mock(Member.class),
                mock(Member.class), mock(Member.class), mock(Member.class));
        Set<Member> borrowingMemberIs2 = Set.of(mock(Member.class), mock(Member.class));
        Set<Member> borrowingMemberIs3 = Set.of(mock(Member.class), mock(Member.class), mock(Member.class));
        Set<Member> borrowingMemberIs0 = new HashSet<>();
        List<Book> books = List.of(
                new Book(1L, "Title1", "Name Surname1", 1, borrowingMemberIs1),
                new Book(11L, "Title1", "Name Surname11", 11, borrowingMemberIs11),
                new Book(2L, "Title2", "Name Surname2", 2, borrowingMemberIs2),
                new Book(3L, "Title3", "Name Surname3", 3, borrowingMemberIs3),
                new Book(4L, "Title4", "Name Surname4", 4, borrowingMemberIs0)
        );
        Set<BorrowedBookDTO> expected = Set.of(
                new BorrowedBookDTO("Title1", 12),
                new BorrowedBookDTO("Title2", 2),
                new BorrowedBookDTO("Title3", 3)
        );

        when(bookRepository.findAll())
                .thenReturn(books);

        Set<BorrowedBookDTO> actual = bookService.findAllBorrowedBooksTitles(true);

        assertEquals(expected, actual);
        verify(bookRepository).findAll();
    }

    @Test
    void createBook_whenNewBookCreated_returnNewBookDTO() {
        Book book = new Book(1L, "Title", "Name Surname", 1, new HashSet<>());
        BookDTO dto = new BookDTO(1L, "Title", "Name Surname", 1);

        when(bookRepository.findByTitleAndAuthor(dto.title(), dto.author()))
                .thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class)))
                .thenReturn(book);
        when(bookMapper.dto(book))
                .thenReturn(dto);

        BookDTO actual = bookService.createBook(dto);

        assertEquals(dto, actual);
        verify(bookRepository).findByTitleAndAuthor(dto.title(), dto.author());
        verify(bookRepository).save(any(Book.class));
        verify(bookMapper).dto(book);
    }

    @Test
    void createBook_whenBookWithSameTitleAndAuthorIsAlreadyCreated_returnAlreadyCreatedBookDTOWithAmountPlus1() {
        Book book = new Book(1L, "Title", "Name Surname", 1, new HashSet<>());
        Book bookWithAmount2 = new Book(1L, "Title", "Name Surname", 2, new HashSet<>());
        BookDTO dto = new BookDTO(1L, "Title", "Name Surname", 1);
        BookDTO expected = new BookDTO(1L, "Title", "Name Surname", 2);

        when(bookRepository.findByTitleAndAuthor(dto.title(), dto.author()))
                .thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class)))
                .thenReturn(bookWithAmount2);
        when(bookMapper.dto(bookWithAmount2))
                .thenReturn(expected);

        BookDTO actual = bookService.createBook(dto);

        assertEquals(expected, actual);
        verify(bookRepository).findByTitleAndAuthor(dto.title(), dto.author());
        verify(bookRepository).save(any(Book.class));
        verify(bookMapper).dto(book);
    }

    @Test
    void updateBook_whenUpdated_returnUpdatedBookDTO() {
        Book book = new Book(1L, "Title", "Name Surname", 1, new HashSet<>());
        BookDTO dto = new BookDTO(1L, "NewTitle", "NewName NewSurname", 32);
        Book updatedBook = new Book(1L, "NewTitle", "NewName NewSurname", 32, new HashSet<>());

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));
        when(bookRepository.findByTitleAndAuthor(dto.title(), dto.author()))
                .thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class)))
                .thenReturn(updatedBook);
        when(bookMapper.dto(book))
                .thenReturn(dto);

        BookDTO actual = bookService.updateBook(1L, dto);

        assertEquals(dto, actual);
        verify(bookRepository).findById(1L);
        verify(bookRepository).findByTitleAndAuthor(dto.title(), dto.author());
        verify(bookRepository).save(updatedBook);
        verify(bookMapper).dto(book);
    }

    @Test
    void updateBook_whenBookWithNewTitleAndNewAuthorIsAlreadyExist_throwsNotUniqueException() {
        Book book = new Book(1L, "Title", "Name Surname", 1, new HashSet<>());
        Book bookWithSameTitleAndAuthor = new Book(3L, "Title", "Name Surname", 23, new HashSet<>());
        BookDTO dto = new BookDTO(1L, "NewTitle", "NewName NewSurname", 32);

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));
        when(bookRepository.findByTitleAndAuthor(dto.title(), dto.author()))
                .thenReturn(Optional.of(bookWithSameTitleAndAuthor));

        assertThrows(
                NotUniqueException.class,
                () -> bookService.updateBook(1L, dto)
        );
        verify(bookRepository).findById(1L);
        verify(bookRepository).findByTitleAndAuthor(dto.title(), dto.author());
    }

    @Test
    void deleteBook_whenDeleted_returnNothing() {
        Book book = new Book(1L, "Title", "Name Surname", 1, new HashSet<>());

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));
        doNothing().when(bookRepository)
                .delete(book);

        bookService.deleteBook(1L);

        verify(bookRepository).findById(1L);
        verify(bookRepository).delete(book);
    }

    @Test
    void deleteBook_whenBookIsBorrowedBySomeMember_throwsCantBeDeletedException() {
        Book book = new Book(1L, "Title", "Name Surname", 1, Set.of(mock(Member.class)));

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        assertThrows(
                CantBeDeletedException.class,
                () -> bookService.deleteBook(1L)
        );

        verify(bookRepository).findById(1L);
    }
}