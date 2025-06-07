package dev.nerdysoft_tech_task.service;

import dev.nerdysoft_tech_task.dto.BookDto;
import dev.nerdysoft_tech_task.dto.MemberDto;
import dev.nerdysoft_tech_task.dto.MemberSearchParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface MemberService {

    MemberDto findById(Long id);

    Page<MemberDto> findAll(MemberSearchParams params, Pageable pageable);

    Set<BookDto> findMemberBorrowedBooks(String name);

    MemberDto createMember(MemberDto dto);

    MemberDto updateMember(Long id, MemberDto dto);

    void deleteMember(Long id);
}
