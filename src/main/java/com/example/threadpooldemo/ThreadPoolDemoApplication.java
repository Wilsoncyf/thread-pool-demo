package com.example.threadpooldemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.Executors;

@SpringBootApplication
public class ThreadPoolDemoApplication {

    public static void main(String[] args) {
        Executors.newFixedThreadPool(1);
        SpringApplication.run(ThreadPoolDemoApplication.class, args);
    }

}
