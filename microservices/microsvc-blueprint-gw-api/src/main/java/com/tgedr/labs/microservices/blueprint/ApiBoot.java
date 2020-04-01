package com.tgedr.labs.microservices.blueprint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ApiBoot {
	public static void main(String[] args) {
		SpringApplication.run(ApiBoot.class, args);
	}
}
