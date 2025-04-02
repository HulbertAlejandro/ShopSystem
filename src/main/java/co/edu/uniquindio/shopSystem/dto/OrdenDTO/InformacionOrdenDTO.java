package co.edu.uniquindio.shopSystem.dto.OrdenDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record InformacionOrdenDTO(
        @NotNull(message = "El ID del cliente es obligatorio")
        String idCliente,
        String codigoPasarela,
        @NotNull(message = "Debe proporcionar al menos un ítem en la orden")
        List<ItemsDTO> items,
        @Min(value = 0, message = "El total debe ser mayor o igual a cero")
        float total,
        float descuento,
        float impuesto,
        String codigoCupon
) {
}
