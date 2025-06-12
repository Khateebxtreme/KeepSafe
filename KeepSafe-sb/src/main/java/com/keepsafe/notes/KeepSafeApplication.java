package com.keepsafe.notes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.keepsafe.notes.repositories")
public class KeepSafeApplication {

	public static void main(String[] args) {
		SpringApplication.run(KeepSafeApplication.class, args);
	}

}

