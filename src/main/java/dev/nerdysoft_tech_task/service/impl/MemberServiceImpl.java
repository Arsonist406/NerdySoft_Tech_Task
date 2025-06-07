package dev.nerdysoft_tech_task.service.impl;

import dev.nerdysoft_tech_task.dto.BookDto;
import dev.nerdysoft_tech_task.dto.MemberDto;
import dev.nerdysoft_tech_task.exception.BookCantBeBorrowedException;
import dev.nerdysoft_tech_task.exception.CantBeDeletedException;
import dev.nerdysoft_tech_task.exception.NotFoundException;
import dev.nerdysoft_tech_task.exception.NotUniqueException;
import dev.nerdysoft_tech_task.mapper.BookMapper;
import dev.nerdysoft_tech_task.mapper.MemberMapper;
import dev.nerdysoft_tech_task.model.Book;
import dev.nerdysoft_tech_task.model.Member;
import dev.nerdysoft_tech_task.repository.MemberRepository;
import dev.nerdysoft_tech_task.service.BookService;
import dev.nerdysoft_tech_task.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final BookMapper bookMapper;

    @Override
    @Transactional(readOnly = true)
    public MemberDto findById(
            Long id
    ) {
        Member member = memberRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Member not found by id " + id));

        return memberMapper.dto(member);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<BookDto> findBorrowedBooksByMembersName(
            String name
    ) {
        Member member = memberRepository
                .findByName(name)
                .orElseThrow(() -> new NotFoundException("Member not found by name " + name));

        Set<Book> borrowedBooks = member.getBorrowedBooks();

        return borrowedBooks
                .stream()
                .map(bookMapper::dto)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberDto> findAll(
            Pageable pageable
    ) {
        Page<Member> memberPage = memberRepository.findAll(pageable);

        return memberPage.map(memberMapper::dto);
    }


    @Override
    @Transactional
    public MemberDto createMember(
            MemberDto dto
    ) {
        Member member = Member
                .builder()
                .name(dto.name())
                .joinedAt(LocalDateTime.now())
                .borrowedBooks(new HashSet<>())
                .build();

        member = memberRepository.save(member);
        return memberMapper.dto(member);
    }

    @Override
    @Transactional
    public MemberDto updateMember(
            Long id,
            MemberDto dto
    ) {
        Member member = memberRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Member not found by id " + id));

        updateNameIfHasTextAndNotEquals(member, dto);

        member = memberRepository.save(member);
        return memberMapper.dto(member);
    }

    private void updateNameIfHasTextAndNotEquals(
            Member member,
            MemberDto dto
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
}
