package edu.leicester.co2103.part1s2.repo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan("edu.leicester.co2103.part1s2.domain")
@ComponentScan("edu.leicester.co2103.part1s2.controller")
@ComponentScan("edu.leicester.co2103.part1s2.repo")
public class Part1s2Application {

    public static void main(String[] args) {
        SpringApplication.run(Part1s2Application.class, args);
    }

}
