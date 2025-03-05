package co.edu.uniquindio.shopSystem.dto.CuentaDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record VerificacionDTO(
        @NotNull @Email String correo,
        @NotNull String codigo) {
}
