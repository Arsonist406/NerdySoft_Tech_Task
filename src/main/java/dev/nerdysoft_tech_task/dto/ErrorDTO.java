package dev.nerdysoft_tech_task.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class ErrorDTO {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String path;
    private Set<ErrorDetail> details;

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorDetail {
        private String value;
        private String message;
    }
}
