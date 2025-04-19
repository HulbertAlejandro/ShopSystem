package co.edu.uniquindio.shopSystem.exceptions;

import co.edu.uniquindio.shopSystem.dto.TokenDTOs.MensajeDTO;
import co.edu.uniquindio.shopSystem.dto.ValidacionDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.ArrayList;
import java.util.List;


/**
 * Manejador global de excepciones para la aplicación Spring Boot.
 * Centraliza el tratamiento de errores y formatea respuestas consistentes para la API.
 */
@RestControllerAdvice
public class ExcepcionesGlobales {

    /**
     * Captura todas las excepciones no manejadas específicamente
     * @param e Excepción generada
     * @return ResponseEntity con mensaje de error genérico y estado HTTP 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MensajeDTO<String>> generalException(Exception e){
        return ResponseEntity.internalServerError().body(
                new MensajeDTO<>(true, e.getMessage())
        );
    }

    /**
     * Maneja errores de validación de datos en las solicitudes
     * @param ex Excepción de validación generada por Spring
     * @return ResponseEntity con lista detallada de errores de validación y estado HTTP 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MensajeDTO<List<ValidacionDTO>>> validationException(
            MethodArgumentNotValidException ex
    ) {
        List<ValidacionDTO> errores = new ArrayList<>();
        BindingResult results = ex.getBindingResult();

        // Extrae y formatea los errores de cada campo
        for (FieldError e: results.getFieldErrors()) {
            errores.add(new ValidacionDTO(e.getField(), e.getDefaultMessage()));
        }

        return ResponseEntity.badRequest().body(
                new MensajeDTO<>(true, errores)
        );
    }
}
