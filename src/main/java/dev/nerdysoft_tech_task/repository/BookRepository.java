package dev.nerdysoft_tech_task.repository;

import dev.nerdysoft_tech_task.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends
        JpaRepository<Book, Long>
{
    Optional<Book> findByTitleAndAuthor(String title, String author);
}
