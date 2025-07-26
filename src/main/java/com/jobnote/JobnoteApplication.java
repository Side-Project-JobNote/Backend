package com.jobnote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class JobnoteApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobnoteApplication.class, args);
	}

}
