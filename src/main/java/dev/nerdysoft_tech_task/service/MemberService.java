package dev.nerdysoft_tech_task.service;

import dev.nerdysoft_tech_task.dto.BookDto;
import dev.nerdysoft_tech_task.dto.MemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface MemberService {

    MemberDto findById(Long id);

    Set<BookDto> findBorrowedBooksByMembersName(String name);

    Page<MemberDto> findAll(Pageable pageable);

    MemberDto createMember(MemberDto dto);

    MemberDto updateMember(Long id, MemberDto dto);

    void deleteMember(Long id);
}
