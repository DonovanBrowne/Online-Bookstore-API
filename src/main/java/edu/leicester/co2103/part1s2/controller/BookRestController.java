package edu.leicester.co2103.part1s2.controller;

import edu.leicester.co2103.part1s2.domain.Author;
import edu.leicester.co2103.part1s2.domain.Book;
import edu.leicester.co2103.part1s2.domain.Order;
import edu.leicester.co2103.part1s2.repo.AuthorRepository;
import edu.leicester.co2103.part1s2.repo.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/books")
public class BookRestController {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookRestController(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    // Endpoint #7: List all books
    @GetMapping
    public ResponseEntity<List<Book>> listAllBooks() {
        List<Book> books = new ArrayList<>();
        bookRepository.findAll().forEach(books::add); // Convert Iterable to List
        if (books.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    // Endpoint #8: Create a book
    @PostMapping
    public ResponseEntity<?> createBook(@RequestBody Book book) {
        if (bookRepository.existsByISBN(book.getISBN())) {
            return new ResponseEntity<>("A book with ISBN " + book.getISBN() + " already exists", HttpStatus.CONFLICT);
        } else {
            // Before saving the book, save the authors associated with the book
            for(Author author : book.getAuthors()) {
                if (authorRepository.existsById(author.getId())) {
                    return new ResponseEntity<>("An author with id " + author.getId() + " already exists", HttpStatus.CONFLICT);
                }
                authorRepository.save(author);
            }
            bookRepository.save(book);
            return new ResponseEntity<>("Book created successfully.", HttpStatus.CREATED);
        }
    }

    // Endpoint #9: Retrieve a single book
    @GetMapping("{ISBN}")
    public ResponseEntity<?> getBook(@PathVariable("ISBN") String ISBN) {
        Optional<Book> optionalBook = bookRepository.findByISBN(ISBN);
        if (optionalBook.isEmpty()) {
            return new ResponseEntity<>("Book with ISBN " + ISBN + " not found", HttpStatus.NOT_FOUND);
        }
        Book book = optionalBook.get();
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    // Endpoint #10: Update a book
    @PutMapping("{ISBN}")
    public ResponseEntity<?> updateBook(@PathVariable("ISBN") String ISBN, @RequestBody Book book) {
        Optional<Book> optionalBook = bookRepository.findByISBN(ISBN);
        if (optionalBook.isEmpty()) {
            return new ResponseEntity<>("Book with ISBN " + ISBN + " not found.", HttpStatus.NOT_FOUND);
        }

        Book currentBook = optionalBook.get();
        currentBook.setISBN(book.getISBN());
        currentBook.setTitle(book.getTitle());
        currentBook.setPublicationYear(book.getPublicationYear());
        currentBook.setPrice(book.getPrice());
        currentBook.setAuthors(book.getAuthors());
        currentBook.setOrders(book.getOrders());

        bookRepository.save(currentBook);
        return new ResponseEntity<>(currentBook, HttpStatus.OK);
    }

    // Endpoint #11: Delete a specific book
    @Transactional
    @DeleteMapping("{ISBN}")
    public ResponseEntity<?> deleteBook(@PathVariable("ISBN") String ISBN) {
        if (bookRepository.existsByISBN(ISBN)) {
            bookRepository.deleteByISBN(ISBN);
            return new ResponseEntity<>("Book with ISBN " + ISBN + " deleted successfully.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Book with ISBN " + ISBN + " not found.", HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint #12: List all authors of a book
    @GetMapping("{ISBN}/authors")
    public ResponseEntity<?> getAuthorsByBookISBN(@PathVariable("ISBN") String ISBN) {
        Optional<Book> optionalBook = bookRepository.findByISBN(ISBN);
        if (optionalBook.isEmpty()) {
            return new ResponseEntity<>("Book with ISBN " + ISBN + " not found.", HttpStatus.NOT_FOUND);
        }

        Book book = optionalBook.get();
        List<Author> authors = book.getAuthors();

        if (authors.isEmpty()) {
            return new ResponseEntity<>("No authors found for book with ISBN " + ISBN, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(authors, HttpStatus.OK);
    }

    // Endpoint #13: List all orders containing a specific book
    @GetMapping("/{ISBN}/orders")
    public ResponseEntity<?> getOrdersByBookISBN(@PathVariable("ISBN") String ISBN) {
        Optional<Book> optionalBook = bookRepository.findByISBN(ISBN);
        if (optionalBook.isEmpty()) {
            return new ResponseEntity<>("Book with ISBN " + ISBN + " not found.", HttpStatus.NOT_FOUND);
        }

        Book book = optionalBook.get();
        List<Order> orders = book.getOrders();

        if (orders.isEmpty()) {
            return new ResponseEntity<>("No orders found containing book with ISBN " + ISBN, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

}