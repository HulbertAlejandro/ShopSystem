package co.edu.uniquindio.shopSystem.dto.CuentaDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CrearCuentaDTO(
        @NotBlank(message = "La cédula es obligatoria") @Length(max = 10, message = "Ingrese una cédula valida") String cedula,
        @NotBlank(message = "El tamano del nombre no es adecuado") @Length(max = 20) String nombre,
        @Length(max = 10, message = "Ingrese un telefono valido") String telefono,
        @Length(max = 30, message = "Ingrese una direccion valida") String direccion,
        @NotBlank @Length(max = 30, message = "Ingrese un correo valido") @Email String correo,
        @NotBlank @Length(min = 6, message = "La contraseña debe tener mas de 6 caracteres") String password,
        String confirmaPassword
){
}
