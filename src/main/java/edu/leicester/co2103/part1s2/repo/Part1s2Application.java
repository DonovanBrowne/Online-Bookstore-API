package edu.leicester.co2103.part1s2.repo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("edu.leicester.co2103.part1s2.domain")
public class Part1s2Application {

    public static void main(String[] args) {
        SpringApplication.run(Part1s2Application.class, args);
    }

}
