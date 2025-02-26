package co.edu.uniquindio.shopSystem.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Component
public class JWTUtils {

    public String generarToken(String email, Map<String, Object> claims) {
        Instant now = Instant.now();

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(1L, ChronoUnit.HOURS)))
                .signWith(getKey())
                .compact();
    }

    public Jws<Claims> parseJwt(String jwtString) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, IllegalArgumentException {
        JwtParser jwtParser = Jwts.parser().verifyWith(getKey()).build();
        return jwtParser.parseSignedClaims(jwtString);
    }

    private SecretKey getKey() {
        String claveSecreta = "secretsecretsecretsecretsecretsecretsecretsecret"; // Usa un valor seguro y variable en producción
        byte[] secretKeyBytes = claveSecreta.getBytes();
        return Keys.hmacShaKeyFor(secretKeyBytes);
    }

    // Función para verificar si el token ha expirado
    public boolean esTokenExpirado(String token) {
        try {
            Claims claims = parseJwt(token).getBody();
            Date fechaExpiracion = claims.getExpiration();
            return fechaExpiracion.before(new Date());
        } catch (ExpiredJwtException e) {
            return true; // Si el token ya ha expirado, retornamos true
        } catch (Exception e) {
            // En caso de otros errores, podemos retornar false o manejar la excepción de otra manera
            throw new RuntimeException("Error al verificar si el token ha expirado");
        }
    }

    // Función para obtener el correo (o subject) desde el token
    public String obtenerCorreoDesdeToken(String token) {
        Claims claims = parseJwt(token).getBody();
        return claims.getSubject(); // Asume que el correo está en el 'subject' del token
    }
}