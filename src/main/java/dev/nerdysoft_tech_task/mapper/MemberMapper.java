package dev.nerdysoft_tech_task.mapper;

import dev.nerdysoft_tech_task.dto.MemberDTO;
import dev.nerdysoft_tech_task.model.Member;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    MemberDTO toDTO(Member member);

}