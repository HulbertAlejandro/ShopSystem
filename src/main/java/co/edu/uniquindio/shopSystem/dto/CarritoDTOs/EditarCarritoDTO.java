package co.edu.uniquindio.shopSystem.dto.CarritoDTOs;

import co.edu.uniquindio.shopSystem.modelo.vo.DetalleCarrito;
import jakarta.validation.constraints.NotBlank;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

public record EditarCarritoDTO(
        @Length(min = 1, message = "El carrito debe contener eventos") List<DetalleCarrito> items,
        @NotBlank(message = "La fecha no se encuentra") LocalDateTime fecha,
        @NotBlank(message = "No se ingreso el campo de cédula") String id,
        @NotBlank ObjectId idUsuario
) {
}