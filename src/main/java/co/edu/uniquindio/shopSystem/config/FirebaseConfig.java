package co.edu.uniquindio.shopSystem.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Clase de configuración para inicializar Firebase en la aplicación Spring Boot.
 */
@Configuration
public class FirebaseConfig {

    /**
     * Método encargado de inicializar Firebase utilizando un archivo de credenciales JSON.
     *
     * @return instancia de FirebaseApp si se inicializa correctamente; de lo contrario, retorna null si ya existe una instancia.
     * @throws IOException si ocurre un error al leer el archivo de credenciales.
     */
    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        Resource resource = new ClassPathResource("shopsystem-f50f1-firebase-adminsdk-fbsvc-9bd84225c7.json");
        if (!resource.exists()) {
            throw new FileNotFoundException("Firebase credentials file not found.");
        }
        InputStream serviceAccount = resource.getInputStream();

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        }
        return null;
    }
}
