package dev.nerdysoft_tech_task.service;

import dev.nerdysoft_tech_task.dto.BookDTO;
import dev.nerdysoft_tech_task.dto.MemberDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface MemberService {

    MemberDTO findById(Long id);

    Set<BookDTO> findMemberBooks(Long id);

    Page<MemberDTO> findAll(String name, Pageable pageable);

    MemberDTO createMember(MemberDTO dto);

    MemberDTO updateMember(Long id, MemberDTO dto);

    void deleteMember(Long id);

    Set<BookDTO> updateBorrowedBooks(Long memberId, Long bookId);
}
