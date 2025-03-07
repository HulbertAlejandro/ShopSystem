package co.edu.uniquindio.shopSystem.dto.CuentaDTOs;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record EditarCuentaDTO(
        @NotBlank @Length( max = 10) String cedula,
        @NotBlank @Length( max = 50) String nombre,
        @NotBlank @Length( max = 50) String correo,
        @NotBlank @Length( max = 50) String direccion,
        @NotBlank @Length( max = 50) String telefono,
        @NotBlank @Length( max = 50) String password,
        @NotBlank @Length( max = 50) String confirmaPassword
) {
}
