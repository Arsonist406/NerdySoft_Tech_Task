package dev.nerdysoft_tech_task.repository;

import dev.nerdysoft_tech_task.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends
        JpaRepository<Member, Long>
{
    Optional<Member> findByName(String name);
}
