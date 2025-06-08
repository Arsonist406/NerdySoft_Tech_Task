package dev.nerdysoft_tech_task.mapper;

import dev.nerdysoft_tech_task.dto.BookDTO;
import dev.nerdysoft_tech_task.model.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {

    Book toEntity(BookDTO dto);

    BookDTO toDTO(Book book);

}
