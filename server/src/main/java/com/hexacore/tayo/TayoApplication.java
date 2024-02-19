package com.hexacore.tayo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class TayoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TayoApplication.class, args);
    }

}
