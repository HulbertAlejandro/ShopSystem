package co.edu.uniquindio.shopSystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Clase de configuración de seguridad que define beans relacionados con la codificación de contraseñas.
 * En este caso, se registra un codificador de contraseñas utilizando el algoritmo BCrypt.
 */
@Configuration
public class SecurityConfig {

    /**
     * Bean que proporciona un {@link BCryptPasswordEncoder}, utilizado para cifrar contraseñas.
     * BCrypt es un algoritmo de hash seguro y ampliamente utilizado en aplicaciones web
     * para proteger contraseñas de usuario.
     *
     * @return Instancia de BCryptPasswordEncoder.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
