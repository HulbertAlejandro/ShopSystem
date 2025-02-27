package co.edu.uniquindio.shopSystem.dto.CarritoDTOs;

import jakarta.validation.constraints.NotBlank;
import org.bson.types.ObjectId;

public record EliminarCarritoDTO(
        @NotBlank ObjectId idCarrito
) {
}
