package com.indra.asistencias.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtils {


    private final String jwtSecret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final int jwtExpirationMs = 86400000; // 24 horas

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public String generateToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();


        String rol = userPrincipal.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_EMPLEADO");

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("rol", rol)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))

                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public String getUserNameFromJwtToken(String token) {
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
        } catch (UnsupportedJwtException e) {
            log.error("Token JWT no soportado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("La cadena claims está vacía: {}", e.getMessage());
        }
        return false;
    }
}