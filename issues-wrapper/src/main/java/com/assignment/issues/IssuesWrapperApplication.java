// Author: Salih Eren Yuzbazzozlu
package com.assignment.issues;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
public class IssuesWrapperApplication {

	public static void main(String[] args) {
		// Load .env if it exists in parent dir (where the assignment instructions say to put it)
		String envDir = ".";
		if (Files.exists(Paths.get("../.env"))) {
			envDir = "..";
		}
		
		if (Files.exists(Paths.get(envDir + "/.env"))) {
			Dotenv dotenv = Dotenv.configure().directory(envDir).filename(".env").load();
			dotenv.entries().forEach(entry -> {
				// Don't overwrite existing system properties
				if (System.getProperty(entry.getKey()) == null) {
					System.setProperty(entry.getKey(), entry.getValue());
				}
			});
		}
		SpringApplication.run(IssuesWrapperApplication.class, args);
	}
}
