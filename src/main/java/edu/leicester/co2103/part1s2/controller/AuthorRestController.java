package edu.leicester.co2103.part1s2.controller;

import edu.leicester.co2103.part1s2.domain.Author;
import edu.leicester.co2103.part1s2.domain.Book;
import edu.leicester.co2103.part1s2.repo.AuthorRepository;
import edu.leicester.co2103.part1s2.repo.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/authors")
public class AuthorRestController {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public AuthorRestController(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    // Endpoint #1: List all authors
    @GetMapping
    public ResponseEntity<List<Author>> listAllAuthors() {
        List<Author> authors = new ArrayList<>();
        authorRepository.findAll().forEach(authors::add); // Convert Iterable to List
        if (authors.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(authors, HttpStatus.OK);
    }

    // Endpoint #2: Create an author
    @PostMapping
    public ResponseEntity<?> createAuthor(@RequestBody Author author) {
        if (authorRepository.existsById(author.getId())) {
            return new ResponseEntity<>("An author with id " + author.getId() + " already exists", HttpStatus.CONFLICT);
        } else {
            // Before saving the author, save the books associated with the author
            for(Book book : author.getBooks()) {
                if (bookRepository.existsByISBN(book.getISBN())) {
                    return new ResponseEntity<>("A book with ISBN " + book.getISBN() + " already exists", HttpStatus.CONFLICT);
                }
                // Ensure the id is set for each book
                if (book.getISBN() == null || book.getISBN().isEmpty()) {
                    return new ResponseEntity<>("Book ISBN cannot be empty", HttpStatus.BAD_REQUEST);
                }
                bookRepository.save(book);
            }
            authorRepository.save(author);
            return new ResponseEntity<>("Author created successfully.", HttpStatus.CREATED);
        }
    }

    // Endpoint #3: Retrieve a single author
    @GetMapping("{id}")
    public ResponseEntity<?> getAuthor(@PathVariable("id") int id) {
        Optional<Author> optionalAuthor = authorRepository.findById(id);
        if (optionalAuthor.isEmpty()) {
            return new ResponseEntity<>("Author with id " + id + " not found", HttpStatus.NOT_FOUND);
        }
        Author author = optionalAuthor.get();
        return new ResponseEntity<>(author, HttpStatus.OK);
    }

    // Endpoint #4: Update an author
    @PutMapping("{id}")
    public ResponseEntity<?> updateAuthor(@PathVariable("id") int id, @RequestBody Author author) {
        Optional<Author> optionalAuthor = authorRepository.findById(id);
        if (optionalAuthor.isEmpty()) {
            return new ResponseEntity<>("Author with id " + id + " not found.", HttpStatus.NOT_FOUND);
        }

        Author currentAuthor = optionalAuthor.get();
        currentAuthor.setId(author.getId());
        currentAuthor.setName(author.getName());
        currentAuthor.setBirthyear(author.getBirthyear());
        currentAuthor.setNationality(author.getNationality());
        currentAuthor.setBooks(author.getBooks());

        authorRepository.save(currentAuthor);
        return new ResponseEntity<>(currentAuthor, HttpStatus.OK);
    }

    // Endpoint #5: Delete a specific author
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteAuthor(@PathVariable("id") int id) {
        if (authorRepository.existsById(id)) {
            authorRepository.deleteById(id);
            return new ResponseEntity<>("Author with id " + id + " deleted successfully.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Author with id " + id + " not found.", HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint #6: List all books written by a specific author
    @GetMapping("/{id}/books")
    public ResponseEntity<?> getBooksByid(@PathVariable("id") int id) {
        Optional<Author> optionalAuthor = authorRepository.findById(id);
        if (optionalAuthor.isEmpty()) {
            return new ResponseEntity<>("Author with id " + id + " not found.", HttpStatus.NOT_FOUND);
        }

        Author author = optionalAuthor.get();
        List<Book> books = author.getBooks();

        if (books.isEmpty()) {
            return new ResponseEntity<>("No books found for author with id " + id, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(books, HttpStatus.OK);
    }
}