package co.edu.uniquindio.shopSystem.dto.OrdenDTO;

import java.time.LocalDateTime;

public record PagoDTO(
        String moneda,
        String tipoPago,
        String detalleEstado,
        String codigoAutorizacion,
        LocalDateTime fecha,
        String idPago,
        float valorTransaccion,
        String estado
) {
}
