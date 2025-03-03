package com.campus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class CampusNexusApplication {

	public static void main(String[] args) {
		SpringApplication.run(CampusNexusApplication.class, args);
		System.out.println("---Application Started---");
	}

}
