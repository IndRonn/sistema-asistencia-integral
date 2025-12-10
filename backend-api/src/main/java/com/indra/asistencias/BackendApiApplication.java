package com.indra.asistencias;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class BackendApiApplication {

    @PostConstruct
    void started() {
        // Forzamos al Backend a trabajar en la misma zona que tú (Perú/Bogotá)
        TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));
    }

	public static void main(String[] args) {

		SpringApplication.run(BackendApiApplication.class, args);
	}
}
