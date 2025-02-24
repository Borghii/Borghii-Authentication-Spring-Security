package com.authentication.borghi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BorghiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BorghiApplication.class, args);
	}

}
