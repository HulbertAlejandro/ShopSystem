package co.edu.uniquindio.shopSystem.dto.TokenDTOs;

public record MensajeDTO<T>(
        boolean error,
        T respuesta
) {
}
