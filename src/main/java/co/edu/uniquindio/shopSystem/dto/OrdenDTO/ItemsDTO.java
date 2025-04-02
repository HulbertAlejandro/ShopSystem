package co.edu.uniquindio.shopSystem.dto.OrdenDTO;

public record ItemsDTO(
        String referencia,
        String nombre,
        int cantidad,
        float precio,
        String idDetalleCarrito
    ) {
}
