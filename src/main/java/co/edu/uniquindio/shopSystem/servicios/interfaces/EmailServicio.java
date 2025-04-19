package co.edu.uniquindio.shopSystem.servicios.interfaces;

import co.edu.uniquindio.shopSystem.dto.EmailDTOs.EmailDTO;
import org.springframework.scheduling.annotation.Async;

/**
 * Interfaz que define los servicios para el envío de correos electrónicos en el sistema.
 * Incluye funcionalidades para envíos generales, recuperación de contraseña y pruebas.
 */
public interface EmailServicio {

    /**
     * Envía un correo electrónico genérico con el contenido especificado
     * @param emailDTO Objeto con los datos del correo (destinatario, asunto, contenido)
     * @throws Exception Si ocurre un error en el envío o hay datos inválidos
     */
    void enviarCorreo (EmailDTO emailDTO) throws Exception;

    /**
     * Envía de forma asíncrona un correo de recuperación de contraseña
     * @param correo_destino Dirección de correo del destinatario
     * @throws Exception Si falla el envío o el correo no existe
     * @apiNote La ejecución se realiza en un hilo separado gracias a @Async
     */
    @Async
    void enviarCorreoRecuperacion(String correo_destino) throws Exception;

    /**
     * Versión de prueba para el envío de correos genéricos (usado en entornos de desarrollo)
     * @param emailDTO Objeto con datos simulados del correo
     * @throws Exception Si ocurre un error durante la simulación
     */
    void enviarCorreoPrueba(EmailDTO emailDTO) throws Exception;

    /**
     * Versión de prueba para correos de recuperación (usado en entornos de desarrollo)
     * @param correo_destino Dirección de correo simulada
     * @throws Exception Si falla la simulación del envío
     */
    void enviarCorreoRecuperacionPrueba(String correo_destino) throws Exception;
}
