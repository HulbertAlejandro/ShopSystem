package co.edu.uniquindio.shopSystem.dto.CarritoDTOs;

import co.edu.uniquindio.shopSystem.modelo.vo.DetalleCarrito;
import java.time.LocalDateTime;
import java.util.List;

public record VistaCarritoDTO(
        String id_carrito,
        List<DetalleCarrito> detallesCarrito,
        LocalDateTime fecha
) {
}
