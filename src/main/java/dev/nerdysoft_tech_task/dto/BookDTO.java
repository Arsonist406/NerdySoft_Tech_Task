package dev.nerdysoft_tech_task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record BookDTO(

        Long id,

        @Size(message = "Min length is 3 characters", min = 3)
        @Pattern(message = "Must start with capital letter",
                regexp = "^[A-Z].*")
        @NotBlank(message = "Can't be blank")
        String title,

        @NotBlank(message = "Can't be blank")
        @Pattern(message = "Should contains two capital words with title and surname and space between",
                regexp = "^[A-Z][a-z]+ [A-Z][a-z]+$")
        String author,

        @PositiveOrZero(message = "Should be positive value")
        Integer amount

) implements Serializable {
}