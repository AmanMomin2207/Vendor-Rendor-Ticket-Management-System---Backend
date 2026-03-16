package com.sankey.ticketmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity
public class TicketmanagamentApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicketmanagamentApplication.class, args);
	}

}
