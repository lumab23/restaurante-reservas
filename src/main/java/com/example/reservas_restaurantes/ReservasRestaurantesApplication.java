package com.example.reservas_restaurantes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@ComponentScan(basePackages = "com.example")
@EnableAspectJAutoProxy(proxyTargetClass = true) 
public class ReservasRestaurantesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservasRestaurantesApplication.class, args);
		System.out.println("Spring Boot Application started. Ready to integrate with Swing.");
	}

}
