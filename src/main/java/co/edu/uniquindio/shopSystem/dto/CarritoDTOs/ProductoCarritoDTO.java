package co.edu.uniquindio.shopSystem.dto.CarritoDTOs;

import co.edu.uniquindio.shopSystem.modelo.enums.TipoProducto;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

public record ProductoCarritoDTO(

        @NotNull(message = "El ID del producto es obligatorio")
        String id, // Cambiado de `idEvento` a `id`

        @NotNull(message = "El ID del cliente es obligatorio")
        String idUsuario,

        @NotBlank(message = "El nombre es obligatorio")
        String nombreProducto,

        TipoProducto tipoProducto,

        @NotNull(message = "La cantidad es obligatorio")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        Integer unidades,

        @NotBlank(message = "El precio del producto es obligatorio")
        float precio

) {
}
