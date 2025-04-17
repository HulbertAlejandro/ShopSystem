package co.edu.uniquindio.shopSystem.dto.CarritoDTOs;

import co.edu.uniquindio.shopSystem.modelo.enums.TipoProducto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ItemsCarritoDTO(
        @NotNull(message = "El ID del producto es obligatorio")
        String id,
        @NotNull(message = "El ID del cliente es obligatorio")
        String idUsuario,
        @NotBlank(message = "El nombre es obligatorio")
        String nombreProducto,
        TipoProducto tipoProducto,
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        Integer unidades,
        @NotNull(message = "El precio del producto es obligatorio")
        Float precio
) {
}
