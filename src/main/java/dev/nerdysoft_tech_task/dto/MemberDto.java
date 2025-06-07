package dev.nerdysoft_tech_task.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record MemberDto(

        Long id,

        @NotBlank(message = "Сan't be blank")
        String name,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        String joinedAt

) implements Serializable {
}