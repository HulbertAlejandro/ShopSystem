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

    public Jws<Claims> parseJwt(String jwtString) throws ExpiredJwtException,
            UnsupportedJwtException, MalformedJwtException, IllegalArgumentException {
        JwtParser jwtParser = Jwts.parser().verifyWith(getKey()).build();
        return jwtParser.parseSignedClaims(jwtString);
    }

    private SecretKey getKey() {
        String claveSecreta = "secretsecretsecretsecretsecretsecretsecretsecret";
        byte[] secretKeyBytes = claveSecreta.getBytes();
        return Keys.hmacShaKeyFor(secretKeyBytes);
    }

    /**
     * Verifica si el token ha expirado
     * @param token Token JWT
     * @return true si el token ha expirado, false en caso contrario
     */
    public boolean esTokenExpirado(String token) {
        try {
            Claims claims = parseJwt(token).getBody();
            Date fechaExpiracion = claims.getExpiration();
            return fechaExpiracion.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar si el token ha expirado", e);
        }
    }

    /**
     * Obtiene el email (subject) del token
     * @param token Token JWT
     * @return Email del usuario
     */
    public String obtenerCorreoDesdeToken(String token) {
        Claims claims = parseJwt(token).getBody();
        return claims.getSubject();
    }

    /**
     * Obtiene la cédula del usuario desde los claims del token
     * @param token Token JWT
     * @return Cédula del usuario
     * @throws RuntimeException Si el token no contiene la cédula o es inválido
     */
    public String obtenerCedulaDesdeToken(String token) {
        try {
            Claims claims = parseJwt(token).getBody();
            String cedula = claims.get("cedula", String.class);

            if (cedula == null || cedula.isEmpty()) {
                throw new RuntimeException("El token no contiene la cédula del usuario");
            }

            return cedula;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener la cédula desde el token", e);
        }
    }

    /**
     * Obtiene todos los claims del token
     * @param token Token JWT
     * @return Mapa con todos los claims
     */
    public Map<String, Object> obtenerTodosClaims(String token) {
        try {
            return parseJwt(token).getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los claims del token", e);
        }
    }
}