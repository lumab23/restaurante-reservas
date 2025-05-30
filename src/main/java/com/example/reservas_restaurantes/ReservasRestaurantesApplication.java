package com.example.reservas_restaurantes;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@ComponentScan(basePackages = "com.example")
@EnableAspectJAutoProxy(proxyTargetClass = true) 
public class ReservasRestaurantesApplication {
	// Removendo a inicialização do Spring daqui, pois agora será feita pelo MainApplication
}
