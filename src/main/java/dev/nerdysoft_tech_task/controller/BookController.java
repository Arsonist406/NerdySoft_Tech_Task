package dev.nerdysoft_tech_task.controller;

import dev.nerdysoft_tech_task.dto.BookDTO;
import dev.nerdysoft_tech_task.dto.BorrowedBookDTO;
import dev.nerdysoft_tech_task.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Tag(name = "Book API", description = "Operations with books")
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @Operation(summary = "Get book by id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Book found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 1,
                                                "title": "The Great Gatsby",
                                                "author": "Francis Fitzgerald",
                                                "amount": 10
                                            }
                                            """))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid id supplied"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book not found")
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookDTO findById(
            @Parameter(description = "Book id")
            @PathVariable("id")
            Long id
    ) {
        return bookService.findById(id);
    }

    @Operation(summary = "Get all books")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Books found"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid Pageable supplied")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<BookDTO> findAll(
            @ParameterObject
            @PageableDefault(size = 20)
            Pageable pageable
    ) {
        return bookService.findAll(pageable);
    }

    @Operation(summary = "Get all borrowed books with or without borrowed amount")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Books found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BorrowedBookDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "With amounts",
                                            value = """
                                                    [
                                                        {
                                                            "title": "The Great Gatsby",
                                                            "amountBorrowed": 3
                                                        },
                                                        {
                                                            "title": "Alphabet",
                                                            "amountBorrowed": 1
                                                        }
                                                    ]
                                                    """),
                                    @ExampleObject(
                                            name = "Without amounts",
                                            value = """
                                                    [
                                                        {
                                                            "title": "The Great Gatsby"
                                                        },
                                                        {
                                                            "title": "Alphabet"
                                                        }
                                                    ]
                                                    """)})),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid showAmountBorrowed supplied")
    })
    @GetMapping("/borrowed")
    @ResponseStatus(HttpStatus.OK)
    public Set<BorrowedBookDTO> findAllBorrowedBooksTitles(
            @Parameter(description = "To show borrowed amount")
            @RequestParam("showAmountBorrowed")
            Boolean showAmountBorrowed
    ) {
        return bookService.findAllBorrowedBooksTitles(showAmountBorrowed);
    }

    @Operation(summary = "Create a new book")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Book successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 1,
                                                "title": "The Great Gatsby",
                                                "author": "Francis Fitzgerald",
                                                "amount": 10
                                            }
                                            """))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid BookDTO supplied")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO createBook(
            @Parameter(description = "New book data")
            @RequestBody
            @Valid
            BookDTO dto
    ) {
        return bookService.createBook(dto);
    }

    @Operation(summary = "Update existing book")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Book successfully updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 1,
                                                "title": "The Great Gatsby",
                                                "author": "Francis Fitzgerald",
                                                "amount": 10
                                            }
                                            """))),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                                    Possible errors:
                                    1. Invalid BookDTO supplied
                                    2. Invalid id supplied
                                    3. Book with given title and author already exists
                                  """),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book not found")
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookDTO updateBook(
            @Parameter(description = "Book id")
            @PathVariable("id")
            Long id,
            @Parameter(description = "Book new data")
            @RequestBody
            @Valid
            BookDTO dto
    ) {
        return bookService.updateBook(id, dto);
    }

    @Operation(summary = "Delete existing book")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Book successfully deleted"),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                                    Possible errors:
                                    1. Invalid id supplied
                                    2. Book was borrowed by some member
                                  """),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(
            @Parameter(description = "Book id")
            @PathVariable("id")
            Long id
    ) {
        bookService.deleteBook(id);
    }
}
