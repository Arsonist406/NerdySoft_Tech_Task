package dev.nerdysoft_tech_task.dto;

public record BookSearchParams(

        String title,

        String author,

        Integer fromAmount,

        Integer toAmount
) {
}
