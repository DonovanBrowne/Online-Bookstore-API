package edu.leicester.co2103.part1s2.controller;

import edu.leicester.co2103.part1s2.domain.Author;
import edu.leicester.co2103.part1s2.domain.Book;
import edu.leicester.co2103.part1s2.domain.Order;
import edu.leicester.co2103.part1s2.repo.AuthorRepository;
import edu.leicester.co2103.part1s2.repo.BookRepository;
import edu.leicester.co2103.part1s2.repo.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderRestController {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public OrderRestController(OrderRepository orderRepository, BookRepository bookRepository, AuthorRepository authorRepository) {
        this.orderRepository = orderRepository;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    // Endpoint #14: List all orders
    @GetMapping
    public ResponseEntity<List<Order>> listAllOrders() {
        List<Order> orders = new ArrayList<>();
        orderRepository.findAll().forEach(orders::add); // Convert Iterable to List
        if (orders.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // Endpoint #15: Create an order
    @PostMapping
        public ResponseEntity<?> createOrder(@RequestBody Order order) {
        if (orderRepository.existsById(order.getId())) {
            return new ResponseEntity<>("An order with id " + order.getId() + " already exists", HttpStatus.CONFLICT);
        } else {
            orderRepository.save(order);
            return new ResponseEntity<>("order created successfully.", HttpStatus.CREATED);
        }
    }
    
    // Endpoint #16: Retrieve a single order
    @GetMapping("{id}")
    public ResponseEntity<?> getOrder(@PathVariable("id") int id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isEmpty()) {
            return new ResponseEntity<>("Order with id " + id + " not found", HttpStatus.NOT_FOUND);
        }
        Order order = optionalOrder.get();
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    // Endpoint #17: Update a specific order
    @PutMapping("{id}")
    public ResponseEntity<?> updateOrder(@PathVariable("id") int id, @RequestBody Order order) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isEmpty()) {
            return new ResponseEntity<>("Order with id " + id + " not found.", HttpStatus.NOT_FOUND);
        }

        Order currentOrder = optionalOrder.get();
        currentOrder.setId(order.getId());
        currentOrder.setDatetime(order.getDatetime());
        currentOrder.setCustomerName(order.getCustomerName());
        currentOrder.setBooks(order.getBooks());

        orderRepository.save(currentOrder);
        return new ResponseEntity<>(currentOrder, HttpStatus.OK);
    }

    // Endpoint #18: List all books in an order
    @GetMapping("{id}/books")
    public ResponseEntity<?> getBooksByid(@PathVariable("id") Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isEmpty()) {
            return new ResponseEntity<>("Order with id " + id + " not found.", HttpStatus.NOT_FOUND);
        }
        Order order = optionalOrder.get();
        List<Book> books = order.getBooks();

        if (books.isEmpty()) {
            return new ResponseEntity<>("No books found for order with id " + id, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    // Endpoint #19: Add a book to an existing order
    @PostMapping("{id}/books")
    public ResponseEntity<?> addBookToOrder(@PathVariable("id") Long id, @RequestBody Book book) {
        // Check if the order exists
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isEmpty()) {
            return new ResponseEntity<>("Order with id " + id + " not found.", HttpStatus.NOT_FOUND);
        }

        Order order = optionalOrder.get();

        // Check if the book is already in the order
        List<Book> books = order.getBooks();
        if (books.contains(book)) {
            return new ResponseEntity<>("Book already exists in the order.", HttpStatus.CONFLICT);
        }

        // Check if the book already exists in the database
        Optional<Book> optionalBook = bookRepository.findByISBN(book.getISBN());
        if (optionalBook.isPresent()) {
            return new ResponseEntity<>("Book with ISBN " + book.getISBN() + " already exists.", HttpStatus.CONFLICT);
        }

        // Save the new book and its author to the database
        for (Author author : book.getAuthors()) {
            // Check if the author already exists in the database
            Optional<Author> optionalAuthor = authorRepository.findById(author.getId());
            if (optionalAuthor.isPresent()) {
                return new ResponseEntity<>("Author with id " + author.getId() + " already exists.", HttpStatus.CONFLICT);
            }
            authorRepository.save(author);
        }
        bookRepository.save(book);

        // Add the book to the order and save
        books.add(book);
        order.setBooks(books);
        orderRepository.save(order);

        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    // Endpoint #20: Remove a book from an existing order
    @DeleteMapping("{id}/books/{ISBN}")
    public ResponseEntity<?> removeBookFromOrder(@PathVariable("id") Long id, @PathVariable("ISBN") String ISBN) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isEmpty()) {
            return new ResponseEntity<>("Order with id " + id + " not found.", HttpStatus.NOT_FOUND);
        }

        Order order = optionalOrder.get();
        List<Book> books = order.getBooks();

        Book bookToRemove = null;
        for (Book book : books) {
            if (book.getISBN().equals(ISBN)) {
                bookToRemove = book;
                break;
            }
        }

        if (bookToRemove == null) {
            return new ResponseEntity<>("Book with ISBN " + ISBN + " not found in the order.", HttpStatus.NOT_FOUND);
        }

        books.remove(bookToRemove);
        order.setBooks(books);
        orderRepository.save(order);

        return new ResponseEntity<>(order, HttpStatus.OK);
    }

}