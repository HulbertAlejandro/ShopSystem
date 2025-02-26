package co.edu.uniquindio.shopSystem.dto.CuponDTOs;

import co.edu.uniquindio.shopSystem.modelo.enums.EstadoCupon;
import co.edu.uniquindio.shopSystem.modelo.enums.TipoCupon;

import java.time.LocalDateTime;

public record ObtenerCuponDTO(
        String id,
        String codigo,
        float descuento,
        String nombre,
        TipoCupon tipo,
        EstadoCupon estado,
        LocalDateTime fechaVencimiento
) { }
