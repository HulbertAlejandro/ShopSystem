package co.edu.uniquindio.shopSystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de CORS (Cross-Origin Resource Sharing) para la aplicación.
 * Define políticas de seguridad para las solicitudes entre dominios.
 */
@Configuration
public class CorsConfig {

    /**
     * Configura las reglas CORS para la aplicación
     * @return Configurador personalizado de CORS
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // Permitir solo rutas que empiezan con "/api/"
                        .allowedOrigins("https://shop-system-frontend-822c9.web.app") // Permite tu frontend
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos permitidos
                        .allowedHeaders("*") // Permite todos los headers
                        .allowCredentials(true); // Permite credenciales (tokens, cookies, etc.)
            }
        };
    }
}
