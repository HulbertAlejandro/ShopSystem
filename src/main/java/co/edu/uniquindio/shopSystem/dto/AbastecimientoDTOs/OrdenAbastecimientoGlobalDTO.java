package co.edu.uniquindio.shopSystem.dto.AbastecimientoDTOs;

import co.edu.uniquindio.shopSystem.modelo.enums.EstadoReabastecimiento;

import java.util.List;

public record OrdenAbastecimientoGlobalDTO(
        List<OrdenAbastecimientoDTO> productos,
        int proveedorId,
        String observaciones
) {
}