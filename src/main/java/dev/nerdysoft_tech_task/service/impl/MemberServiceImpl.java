package dev.nerdysoft_tech_task.service.impl;

import dev.nerdysoft_tech_task.dto.BookDto;
import dev.nerdysoft_tech_task.dto.MemberDto;
import dev.nerdysoft_tech_task.dto.MemberSearchParams;
import dev.nerdysoft_tech_task.exception.CantBeDeletedException;
import dev.nerdysoft_tech_task.exception.NotFoundException;
import dev.nerdysoft_tech_task.mapper.BookMapper;
import dev.nerdysoft_tech_task.mapper.MemberMapper;
import dev.nerdysoft_tech_task.model.Book;
import dev.nerdysoft_tech_task.model.Member;
import dev.nerdysoft_tech_task.repository.MemberRepository;
import dev.nerdysoft_tech_task.service.MemberService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    public Page<MemberDto> findAll(
            MemberSearchParams params,
            Pageable pageable
    ) {
        Page<Member> memberPage = memberRepository.findAll(buildSpecification(params), pageable);

        return memberPage.map(memberMapper::dto);
    }

    private Specification<Member> buildSpecification(
            MemberSearchParams params
    ) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            addEqualPredicateIfHasText(builder, predicates, root,
                    "name", params.name());

            addGreaterThanOrEqualPredicateIfNotNull(builder, predicates, root,
                    "joinedAt", LocalDateTime.parse(params.joinedAfter()));

            addLessThanOrEqualPredicateIfNotNull(builder, predicates, root,
                    "joinedAt", LocalDateTime.parse(params.joinedBefore()));

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void addEqualPredicateIfHasText(
            CriteriaBuilder builder,
            List<Predicate> predicates,
            Root<?> root,
            String fieldName,
            String value
    ) {
        if (StringUtils.hasText(value)) {
            predicates.add(builder.equal(
                    root.get(fieldName),
                    value
            ));
        }
    }

    private void addGreaterThanOrEqualPredicateIfNotNull(
            CriteriaBuilder builder,
            List<Predicate> predicates,
            Root<?> root,
            String fieldName,
            LocalDateTime value
    ) {
        if (value != null) {
            predicates.add(builder.greaterThanOrEqualTo(
                    root.get(fieldName),
                    value
            ));
        }
    }

    private void addLessThanOrEqualPredicateIfNotNull(
            CriteriaBuilder builder,
            List<Predicate> predicates,
            Root<?> root,
            String fieldName,
            LocalDateTime value
    ) {
        if (value != null) {
            predicates.add(builder.lessThanOrEqualTo(
                    root.get(fieldName),
                    value
            ));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Set<BookDto> findMemberBorrowedBooks(
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
