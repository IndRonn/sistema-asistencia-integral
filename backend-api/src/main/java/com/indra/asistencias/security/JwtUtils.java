package com.indra.asistencias.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j; // Usamos Lombok para el Logger
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
@Slf4j // 1. Genera automáticamente la variable 'log' (Mejor que LoggerFactory manual)
public class JwtUtils {

    // Secreto Hardcodeado (HITO 1/2) - En HITO final irá a variables de entorno
    private final String jwtSecret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final int jwtExpirationMs = 86400000; // 24 horas

    // 2. Transforma el String Base64 en un objeto Key criptográfico real
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 3. Genera el Token (Firma Digital)
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username) // A quién pertenece
                .setIssuedAt(new Date()) // Cuándo se creó
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Cuándo muere
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // El sello de lacre
                .compact();
    }

    // 4. Extrae el usuario del token (Para saber quién hace la petición)
    public String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    // 5. Valida si el token es auténtico
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (SecurityException e) {
            log.error("Firma JWT inválida: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Token JWT inválido: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Token JWT expirado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("La cadena claims está vacía: {}", e.getMessage());
        }
        return false;
    }
}