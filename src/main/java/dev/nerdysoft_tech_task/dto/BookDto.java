package dev.nerdysoft_tech_task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.io.Serializable;

public record BookDto(

        Long id,

        @Size(message = "Min lenght is 3 characters", min = 3)
        @Pattern(message = "Must start with capital letter",
                regexp = "^[A-Z\u0410-\u042F\u0404\u0406\u0407\u0490].*")
        @NotBlank(message = "Сan't be blank")
        String title,

        @NotBlank(message = "Сan't be blank")
        @Pattern(message = "Should contain two capital words with name and surname and space between",
                regexp = "^[A-Z][a-z]+ [A-Z][a-z]+$")
        String author,

        @PositiveOrZero(message = "Can't be smaller then 0")
        Integer amount

) implements Serializable {
}