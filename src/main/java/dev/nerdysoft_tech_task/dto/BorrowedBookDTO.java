package dev.nerdysoft_tech_task.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.io.Serializable;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BorrowedBookDTO(

        String title,
        Integer amountBorrowed

) implements Serializable {
}
