package com.indra.asistencias;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import java.util.TimeZone;

@SpringBootApplication
@EnableCaching
public class BackendApiApplication {

    @PostConstruct
    void started() {
        // zona Horaria
        TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));
    }

	public static void main(String[] args) {

		SpringApplication.run(BackendApiApplication.class, args);
	}
}
