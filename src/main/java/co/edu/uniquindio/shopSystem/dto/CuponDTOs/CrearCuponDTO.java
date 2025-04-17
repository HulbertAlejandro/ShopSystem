package co.edu.uniquindio.shopSystem.dto.CuponDTOs;

import co.edu.uniquindio.shopSystem.modelo.enums.EstadoCupon;
import co.edu.uniquindio.shopSystem.modelo.enums.TipoCupon;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

public record CrearCuponDTO(
        @NotBlank(message = "El código del cupón es obligatorio")
        @Length(max = 10, message = "El código del cupón no debe exceder los 10 caracteres") String codigo,

        @NotBlank(message = "El nombre del cupón es obligatorio")
        @Length(max = 50, message = "El nombre del cupón no debe exceder los 50 caracteres") String nombre,

        @NotBlank(message = "La descripcion del cupón es obligatoria")
        String descripcion,

        @Positive(message = "El descuento debe ser un número positivo")
        float descuento,

        @NotNull(message = "El tipo de cupón es obligatorio")
        TipoCupon tipo,

        @NotNull(message = "El estado del cupón es obligatorio")
        EstadoCupon estado,

        @FutureOrPresent(message = "La fecha de vencimiento debe estar en el futuro")
        @NotNull(message = "La fecha de vencimiento es obligatoria")
        LocalDateTime fechaInicio,

        @FutureOrPresent(message = "La fecha de vencimiento debe estar en el futuro")
        @NotNull(message = "La fecha de vencimiento es obligatoria")
        LocalDateTime fechaVencimiento
) {}

