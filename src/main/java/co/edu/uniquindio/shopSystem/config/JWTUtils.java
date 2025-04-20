package co.edu.uniquindio.shopSystem.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

/**
 * Clase utilitaria para la creación, validación y análisis de tokens JWT.
 * Se encarga de firmar los tokens y extraer la información (claims) que contienen.
 */
@Component
public class JWTUtils {

    /**
     * Genera un token JWT a partir del correo electrónico y un mapa de claims personalizados.
     *
     * @param email Correo electrónico del usuario (se guarda como 'subject' en el token).
     * @param claims Claims personalizados a incluir en el token (por ejemplo: rol, cédula, nombre).
     * @return Token JWT generado, firmado y listo para enviar al cliente.
     */
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

    /**
     * Parsea y valida un JWT firmado. Lanza excepciones si el token es inválido, expirado o mal formado.
     *
     * @param jwtString Token JWT en formato String.
     * @return Objeto Jws con los claims firmados.
     * @throws ExpiredJwtException Si el token ha expirado.
     * @throws UnsupportedJwtException Si el formato del token no es soportado.
     * @throws MalformedJwtException Si el token está mal formado.
     * @throws IllegalArgumentException Si el token es nulo o vacío.
     */
    public Jws<Claims> parseJwt(String jwtString) throws ExpiredJwtException,
            UnsupportedJwtException, MalformedJwtException, IllegalArgumentException {
        JwtParser jwtParser = Jwts.parser().verifyWith(getKey()).build();
        return jwtParser.parseSignedClaims(jwtString);
    }

    /**
     * Método interno que retorna la clave secreta usada para firmar los tokens.
     * Esta clave debe mantenerse segura y tener una longitud adecuada para HMAC SHA.
     *
     * @return Llave secreta para la firma HMAC-SHA.
     */
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