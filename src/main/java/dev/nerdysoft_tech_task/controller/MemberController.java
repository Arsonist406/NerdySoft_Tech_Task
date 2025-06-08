package dev.nerdysoft_tech_task.controller;

import dev.nerdysoft_tech_task.dto.BookDTO;
import dev.nerdysoft_tech_task.dto.BorrowedBookDTO;
import dev.nerdysoft_tech_task.dto.MemberDTO;
import dev.nerdysoft_tech_task.service.MemberService;
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

@Tag(name = "Members API", description = "Operations with members")
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "Get member by id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Member found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MemberDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 1,
                                                "name": "John Doe",
                                                "membershipDate": "2025-08-06T12:00:00"
                                            }
                                            """))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid id supplied"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Member not found")
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MemberDTO findById(
            @Parameter(description = "Member id")
            @PathVariable("id")
            Long id
    ) {
        return memberService.findById(id);
    }

    @Operation(summary = "Get all members")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Members found"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid Pageable supplied")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<MemberDTO> findAll(
            @Parameter(description = "Member name")
            @RequestParam(name = "name", required = false)
            String name,
            @ParameterObject
            @PageableDefault(size = 20)
            Pageable pageable
    ) {
        return memberService.findAll(name, pageable);
    }

    @Operation(summary = "Get member borrowed books")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Member found, borrowed books returned",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                                {
                                                    "id": 1,
                                                    "title": "The Great Gatsby",
                                                    "author": "Francis Fitzgerald",
                                                    "amount": 10
                                                },
                                                {
                                                    "id": 2,
                                                    "title": "Alphabet",
                                                    "author": "Some Guy",
                                                    "amount": 23
                                                }
                                            ]
                                            """))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid id supplied"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Member not found")
    })
    @GetMapping("/{id}/books")
    @ResponseStatus(HttpStatus.OK)
    public Set<BookDTO> findMemberBooks(
            @Parameter(description = "Member id")
            @PathVariable("id")
            Long id
    ) {
        return memberService.findMemberBooks(id);
    }

    @Operation(summary = "Create a new member")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Member successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MemberDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 1,
                                                "name": "John Doe",
                                                "membershipDate": "2025-08-06T12:00:00"
                                            }
                                            """))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid MemberDTO supplied")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberDTO createMember(
            @Parameter(description = "New Member data")
            @RequestBody
            @Valid
            MemberDTO dto
    ) {
        return memberService.createMember(dto);
    }

    @Operation(summary = "Update member")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Member successfully updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MemberDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 1,
                                                "name": "John Doe",
                                                "membershipDate": "2025-08-06T12:00:00"
                                            }
                                            """))),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                                    Possible errors:
                                    1. Invalid MemberDTO supplied
                                    2. Invalid id supplied
                                  """),
            @ApiResponse(
                    responseCode = "404",
                    description = "Member not found")
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MemberDTO updateMember(
            @Parameter(description = "Member id")
            @PathVariable("id")
            Long id,
            @Parameter(description = "Member new data")
            @RequestBody
            @Valid
            MemberDTO dto
    ) {
        return memberService.updateMember(id, dto);
    }

    @Operation(summary = "Delete member")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Member successfully deleted"),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                                    Possible errors:
                                    1. Invalid id supplied
                                    2. Member has borrowed books
                                  """),
            @ApiResponse(
                    responseCode = "404",
                    description = "Member not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMember(
            @Parameter(description = "Member id")
            @PathVariable("id")
            Long id
    ) {
        memberService.deleteMember(id);
    }

    @Operation(
            summary = "Update set of member's borrowed books",
            description = """
                        This endpoint manages book borrowing/returning operations for members.
                        
                        Behavior depends on current book borrowing status:
                        1. If the book (book_id) is NOT currently borrowed by the member (member_id):
                           - Adds the book to member's borrowed books set
                           - Decreases available book amount by 1
                        
                        2. If the book (book_id) IS currently borrowed by the member (member_id):
                           - Removes the book from member's borrowed books set
                           - Increases available book amount by 1
                        """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Book successfully borrowed or returned",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BorrowedBookDTO.class),
                            examples = @ExampleObject(
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
                                            """))),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                                    Possible errors:
                                    1. Invalid member_id supplied
                                    2. Invalid book_id supplied
                                    3. Amount of books with book_id is zero
                                    4. Member borrowed max allowed amount of books
                                  """),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                                    Possible errors:
                                    1. Member not found
                                    2. Book not found
                                  """)
    })
    @PatchMapping("/{member_id}/books/{book_id}")
    @ResponseStatus(HttpStatus.OK)
    public Set<BookDTO> updateBorrowedBooks(
            @Parameter(description = "Id of member that want to borrow or return book")
            @PathVariable("member_id")
            Long memberId,
            @Parameter(description = "Book id that member want to borrow or return")
            @PathVariable("book_id")
            Long bookId
    ) {
        return memberService.updateBorrowedBooks(memberId, bookId);
    }
}
