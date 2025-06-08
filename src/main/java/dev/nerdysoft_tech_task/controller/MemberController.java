package dev.nerdysoft_tech_task.controller;

import dev.nerdysoft_tech_task.dto.BookDTO;
import dev.nerdysoft_tech_task.dto.MemberDTO;
import dev.nerdysoft_tech_task.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MemberDTO findById(
            @PathVariable("id") Long id
    ) {
        return memberService.findById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<MemberDTO> findAll(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return memberService.findAll(pageable);
    }

    @GetMapping("/books")
    @ResponseStatus(HttpStatus.OK)
    public Set<BookDTO> findBorrowedBooksByMembersName(
            @RequestParam("member_name") String memberName
    ) {
        return memberService.findBorrowedBooksByMembersName(memberName);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberDTO createMember(
            @RequestBody @Valid MemberDTO dto
    ) {
        return memberService.createMember(dto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MemberDTO updateMember(
            @PathVariable("id") Long id,
            @RequestBody @Valid MemberDTO dto
    ) {
        return memberService.updateMember(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMember(
            @PathVariable("id") Long id
    ) {
        memberService.deleteMember(id);
    }

    @PatchMapping("/{member_id}/books/{book_id}")
    @ResponseStatus(HttpStatus.OK)
    public Set<BookDTO> updateBorrowedBooks(
            @PathVariable("member_id") Long memberId,
            @PathVariable("book_id") Long bookId
    ) {
        return memberService.updateBorrowedBooks(memberId, bookId);
    }
}
