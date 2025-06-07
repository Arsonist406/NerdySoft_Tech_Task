package dev.nerdysoft_tech_task.mapper;

import dev.nerdysoft_tech_task.dto.MemberDto;
import dev.nerdysoft_tech_task.model.Member;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    MemberDto dto(Member member);

}