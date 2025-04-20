package co.edu.uniquindio.shopSystem.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Clase de configuración para personalizar el comportamiento de serialización/deserialización de JSON
 * usando Jackson dentro de la aplicación Spring Boot.
 */
@Configuration
public class JacksonConfig {

    /**
     * Bean que configura un {@link ObjectMapper} personalizado.
     * Se registra el módulo {@link JavaTimeModule} para permitir la correcta
     * serialización y deserialización de tipos de fecha y hora de Java 8 (como LocalDate, LocalDateTime, etc.).
     *
     * @return una instancia personalizada de ObjectMapper.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}