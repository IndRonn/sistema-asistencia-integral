package com.indra.asistencias;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // La contraseña que quieres usar
        String rawPassword = "12345678";

        // Generando el Hash
        String encodedPassword = encoder.encode(rawPassword);

        System.out.println("=========================================");
        System.out.println("🔑 Password Plano: " + rawPassword);
        System.out.println("🔒 Hash BCrypt:    " + encodedPassword);
        System.out.println("=========================================");

        // Copia el valor que salga después de "Hash BCrypt:"
    }
}