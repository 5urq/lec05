package com.team02.lec05;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class Lec05Application {

	public static void main(String[] args) {
		SpringApplication.run(Lec05Application.class, args);
	}

}
