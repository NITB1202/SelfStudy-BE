package com.example.selfstudybe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.cloudinary.*;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class SelfStudyBeApplication {

	public static void main(String[] args) {
		// Load .env file
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())
		);

		SpringApplication.run(SelfStudyBeApplication.class, args);
	}

}
