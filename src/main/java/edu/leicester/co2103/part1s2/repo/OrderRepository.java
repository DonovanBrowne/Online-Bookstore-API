package edu.leicester.co2103.part1s2.repo;

import edu.leicester.co2103.part1s2.domain.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderRepository extends CrudRepository<Order,Integer> {
    Optional<Order> findById(Long id);
}
