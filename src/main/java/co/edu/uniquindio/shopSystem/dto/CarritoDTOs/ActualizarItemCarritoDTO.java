package co.edu.uniquindio.shopSystem.dto.CarritoDTOs;

public record ActualizarItemCarritoDTO(
        String idCliente,
        String idProducto,
        int nuevaCantidad
) {
}
