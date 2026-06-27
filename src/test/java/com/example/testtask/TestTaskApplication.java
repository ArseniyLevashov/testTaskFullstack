package com.example.testtask;

import org.springframework.boot.SpringApplication;

public class TestTaskApplication {

    public static void main(String[] args) {
        SpringApplication.from(TaskApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}
