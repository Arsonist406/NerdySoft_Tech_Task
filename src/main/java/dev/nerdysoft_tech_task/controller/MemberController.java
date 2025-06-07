package dev.nerdysoft_tech_task.controller;

import dev.nerdysoft_tech_task.dto.BookDto;
import dev.nerdysoft_tech_task.dto.MemberDto;
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
    public MemberDto findById(
            @PathVariable("id") Long id
    ) {
        return memberService.findById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<MemberDto> findAll(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return memberService.findAll(pageable);
    }

    @GetMapping("/{name}/borrowed_books")
    @ResponseStatus(HttpStatus.OK)
    public Set<BookDto> findMemberBorrowedBooks(
            @PathVariable("name") String name
    ) {
        return memberService.findMemberBorrowedBooks(name);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberDto createMember(
            @RequestBody @Valid MemberDto dto
    ) {
        return memberService.createMember(dto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MemberDto updateMember(
            @PathVariable("id") Long id,
            @RequestBody @Valid MemberDto dto
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
}
