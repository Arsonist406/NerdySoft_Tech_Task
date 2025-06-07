package dev.nerdysoft_tech_task.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

public record MemberSearchParams(

        String name,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        String joinedAfter,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        String joinedBefore
) {
}
