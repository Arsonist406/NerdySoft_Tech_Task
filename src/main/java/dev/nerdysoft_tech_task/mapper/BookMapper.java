package dev.nerdysoft_tech_task.mapper;

import dev.nerdysoft_tech_task.dto.BookDto;
import dev.nerdysoft_tech_task.model.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {

    Book book(BookDto dto);

    BookDto dto(Book book);

}
