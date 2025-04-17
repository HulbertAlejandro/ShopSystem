package co.edu.uniquindio.shopSystem.dto.CarritoDTOs;

import org.bson.types.ObjectId;

public record ActualizarItemCarritoDTO(
        String idCliente,
        String idProducto,
        int nuevaCantidad
) {
}
