package com.pharmquest.pharmquest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PharmquestApplication {

	public static void main(String[] args) {
		SpringApplication.run(PharmquestApplication.class, args);
	}

}
