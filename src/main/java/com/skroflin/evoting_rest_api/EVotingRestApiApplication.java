package com.skroflin.evoting_rest_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

// Remove exclusion when connecting to a db
@SpringBootApplication
public class EVotingRestApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EVotingRestApiApplication.class, args);
    }

}