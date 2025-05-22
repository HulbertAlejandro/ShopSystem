package co.edu.uniquindio.shopSystem.dto.AbastecimientoDTOs;

import co.edu.uniquindio.shopSystem.modelo.enums.EstadoReabastecimiento;
import co.edu.uniquindio.shopSystem.modelo.vo.ProductoReabastecido;

import java.time.LocalDateTime;
import java.util.List;

public record MostrarOrdenReabastecimientoDTO(
        String id,
        LocalDateTime fechaCreacion,
        List<ProductoReabastecerDTO>productos,
        EstadoReabastecimiento estadoReabastecimiento
) {
}
