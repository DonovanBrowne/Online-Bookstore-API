package edu.leicester.co2103.part1s2.repo;

import edu.leicester.co2103.part1s2.domain.Book;
import org.springframework.data.repository.CrudRepository;


import java.util.Optional;
public interface BookRepository extends CrudRepository<Book,Integer> {
    boolean existsByISBN(String isbn);

    Optional<Book> findByISBN(String isbn);


    void deleteByISBN(String isbn);
}
