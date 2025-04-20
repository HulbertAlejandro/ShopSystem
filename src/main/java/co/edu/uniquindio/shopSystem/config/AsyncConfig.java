package co.edu.uniquindio.shopSystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuración para habilitar y personalizar el procesamiento asíncrono en la aplicación.
 * Permite la ejecución de métodos anotados con @Async en hilos separados.
 */
@Configuration
@EnableAsync // Habilita el soporte para métodos asíncronos
public class AsyncConfig implements AsyncConfigurer {

    /**
     * Configuración base para el funcionamiento de:
     * - @Async en servicios
     * - Ejecución en segundo plano de tareas
     *
     * Configuración por defecto:
     * - Usa SimpleAsyncTaskExecutor
     * - No hay pool de hilos (crea nuevo hilo por cada tarea)
     * - No manejo personalizado de excepciones
     */
}
