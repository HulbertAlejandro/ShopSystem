package co.edu.uniquindio.shopSystem.dto.CuponDTOs;

import co.edu.uniquindio.shopSystem.modelo.enums.EstadoCupon;
import co.edu.uniquindio.shopSystem.modelo.enums.TipoCupon;

import java.time.LocalDateTime;

public record InformacionCuponDTO(
        String id,
        String nombre,
        String codigo,
        float descuento,
        LocalDateTime fechaVencimiento,
        TipoCupon tipo,
        EstadoCupon estado
) {
}
