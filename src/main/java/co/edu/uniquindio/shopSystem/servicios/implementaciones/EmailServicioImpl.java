package co.edu.uniquindio.shopSystem.servicios.implementaciones;

import co.edu.uniquindio.shopSystem.dto.EmailDTOs.EmailDTO;
import co.edu.uniquindio.shopSystem.servicios.interfaces.EmailServicio;

import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Implementación del servicio {@link EmailServicio} que permite el envío de correos electrónicos
 * utilizando la biblioteca Simple Java Mail y el servidor SMTP de Gmail.
 */
@Service
public class EmailServicioImpl implements EmailServicio {

    /**
     * Envía un correo electrónico de forma asíncrona utilizando los datos proporcionados en el {@link EmailDTO}.
     *
     * @param emailDTO objeto que contiene el destinatario, asunto y cuerpo del correo
     * @throws Exception si ocurre algún error al construir o enviar el correo
     */
    @Override
    @Async
    public void enviarCorreo(EmailDTO emailDTO) throws Exception {

        String destinatarioLimpio = emailDTO.destinatario().trim().replace("\"", "");

        Email email = EmailBuilder.startingBlank()
                .from("supermercadossg@gmail.com")
                .to(destinatarioLimpio)
                //.cc("assdsds", "sdsdsdsd", "aasasas")
                .withSubject(emailDTO.asunto())
                .withPlainText(emailDTO.cuerpo())
                .buildEmail();

        try (Mailer mailer = MailerBuilder
                .withSMTPServer("smtp.gmail.com", 587, "supermercadossg@gmail.com", "uojj dorx iyxf brek") // Correo como usuario SMTP
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withDebugLogging(true)
                .buildMailer()) {

            mailer.sendMail(email);
        }
    }

    /**
     * Método pendiente de implementación que enviaría un correo de recuperación de contraseña.
     *
     * @param correo_destino dirección de correo electrónico del destinatario
     * @throws Exception si ocurre algún error durante el proceso
     */
    @Override
    public void enviarCorreoRecuperacion(String correo_destino) throws Exception {

    }

    /**
     * Envía un correo con un archivo adjunto, útil por ejemplo para enviar comprobantes de pago.
     *
     * @param emailDTO objeto con los datos del correo
     * @param archivoAdjunto archivo a adjuntar en el correo
     * @throws Exception si ocurre algún error durante el envío
     */
    public void enviarCorreoPago(EmailDTO emailDTO, File archivoAdjunto) throws Exception {
        // Limpiar el destinatario para evitar caracteres indeseados
        String destinatarioLimpio = emailDTO.destinatario().trim().replace("\"", "");

        // Crear un DataSource a partir del archivo adjunto
        DataSource archivoDataSource = new FileDataSource(archivoAdjunto);

        // Construir el correo
        Email email = EmailBuilder.startingBlank()
                .from("supermercadossg@gmail.com")
                .to(destinatarioLimpio)
                .withSubject(emailDTO.asunto())
                .withPlainText(emailDTO.cuerpo())  // Texto plano (puedes cambiar a HTML si lo necesitas)
                .withAttachment(archivoAdjunto.getName(), archivoDataSource)  // Agregar el archivo adjunto como DataSource
                .buildEmail();

        // Configurar el mailer y enviar el correo
        try (Mailer mailer = MailerBuilder
                .withSMTPServer("smtp.gmail.com", 587, "supermercadossg@gmail.com", "uojj dorx iyxf brek")
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withDebugLogging(true)
                .buildMailer()) {

            // Enviar el correo
            mailer.sendMail(email);
        }
    }

    //METODOS DE PRUEBA DE JUNIT

    /**
     * Método de prueba que envía un correo electrónico de forma asíncrona.
     *
     * @param emailDTO objeto con la información del correo de prueba
     * @throws Exception si ocurre algún error durante el envío
     */
    @Override
    @Async
    public void enviarCorreoPrueba(EmailDTO emailDTO) throws Exception {

        String destinatarioLimpio = emailDTO.destinatario().trim().replace("\"", "");

        Email email = EmailBuilder.startingBlank()
                .from("supermercadossg@gmail.com")
                .to(destinatarioLimpio)
                .withSubject(emailDTO.asunto())
                .withPlainText(emailDTO.cuerpo())
                .buildEmail();

        try (Mailer mailer = MailerBuilder
                .withSMTPServer("smtp.gmail.com", 587, "supermercadossg@gmail.com", "uojj dorx iyxf brek") // Correo como usuario SMTP
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withDebugLogging(true)
                .buildMailer()) {

            mailer.sendMail(email);
        }
    }

    /**
     * Método de prueba que simula el envío de un correo de recuperación con un código fijo ("000000").
     *
     * @param correo_destino dirección del destinatario
     * @throws Exception si ocurre un error al enviar el correo
     */
    @Async
    @Override
    public void enviarCorreoRecuperacionPrueba(String correo_destino) throws Exception {

        String destinatarioLimpio = correo_destino.trim().replace("\"", "");

        Email email = EmailBuilder.startingBlank()
                .from("aseguradoralayo@gmail.com")
                .to(destinatarioLimpio)
                .withSubject("Codigo de recuperacion de contraseña de Aseguradora LAYO")
                .withPlainText("000000")
                .buildEmail();

        try (Mailer mailer = MailerBuilder
                .withSMTPServer("smtp.gmail.com", 587, "aseguradoralayo@gmail.com", "malx djiy tewv bdud") // Correo como usuario SMTP
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withDebugLogging(true)
                .buildMailer()) {

            mailer.sendMail(email);
        }
    }
}
