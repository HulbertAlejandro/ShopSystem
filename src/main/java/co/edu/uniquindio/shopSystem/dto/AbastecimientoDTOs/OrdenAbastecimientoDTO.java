package co.edu.uniquindio.shopSystem.dto.AbastecimientoDTOs;

import co.edu.uniquindio.shopSystem.modelo.enums.EstadoReabastecimiento;

public record OrdenAbastecimientoDTO(
        String referenciaProducto,
        String nombreProducto,
        int cantidadAbastecer,
        EstadoReabastecimiento estadoReabastecimiento   
) {
}
