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

@Service
public class EmailServicioImpl implements EmailServicio {


    @Override
    @Async
    public void enviarCorreo(EmailDTO emailDTO) throws Exception {

        String destinatarioLimpio = emailDTO.destinatario().trim().replace("\"", "");

        Email email = EmailBuilder.startingBlank()
                .from("aseguradoralayo@gmail.com")
                .to(destinatarioLimpio)
                //.cc("assdsds", "sdsdsdsd", "aasasas")
                .withSubject(emailDTO.asunto())
                .withPlainText(emailDTO.cuerpo())
                .buildEmail();

        try (Mailer mailer = MailerBuilder
                .withSMTPServer("smtp.gmail.com", 587, "aseguradoralayo@gmail.com", "malx djiy tewv bdud") // Correo como usuario SMTP
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withDebugLogging(true)
                .buildMailer()) {

            mailer.sendMail(email);
        }
    }

    @Override
    public void enviarCorreoRecuperacion(String correo_destino) throws Exception {

    }


    public void enviarCorreoPago(EmailDTO emailDTO, File archivoAdjunto) throws Exception {
        // Limpiar el destinatario para evitar caracteres indeseados
        String destinatarioLimpio = emailDTO.destinatario().trim().replace("\"", "");

        // Crear un DataSource a partir del archivo adjunto
        DataSource archivoDataSource = new FileDataSource(archivoAdjunto);

        // Construir el correo
        Email email = EmailBuilder.startingBlank()
                .from("aseguradoralayo@gmail.com")
                .to(destinatarioLimpio)
                .withSubject(emailDTO.asunto())
                .withPlainText(emailDTO.cuerpo())  // Texto plano (puedes cambiar a HTML si lo necesitas)
                .withAttachment(archivoAdjunto.getName(), archivoDataSource)  // Agregar el archivo adjunto como DataSource
                .buildEmail();

        // Configurar el mailer y enviar el correo
        try (Mailer mailer = MailerBuilder
                .withSMTPServer("smtp.gmail.com", 587, "aseguradoralayo@gmail.com", "malx djiy tewv bdud")
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withDebugLogging(true)
                .buildMailer()) {

            // Enviar el correo
            mailer.sendMail(email);
        }
    }

    //METODOS DE PRUEBA DE JUNIT


    @Override
    @Async
    public void enviarCorreoPrueba(EmailDTO emailDTO) throws Exception {

        String destinatarioLimpio = emailDTO.destinatario().trim().replace("\"", "");

        Email email = EmailBuilder.startingBlank()
                .from("aseguradoralayoo@gmail.com")
                .to(destinatarioLimpio)
                .withSubject(emailDTO.asunto())
                .withPlainText(emailDTO.cuerpo())
                .buildEmail();

        try (Mailer mailer = MailerBuilder
                .withSMTPServer("smtp.gmail.com", 587, "aseguradoralayo@gmail.com", "malx djiy tewv bdud") // Correo como usuario SMTP
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withDebugLogging(true)
                .buildMailer()) {

            mailer.sendMail(email);
        }
    }

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
