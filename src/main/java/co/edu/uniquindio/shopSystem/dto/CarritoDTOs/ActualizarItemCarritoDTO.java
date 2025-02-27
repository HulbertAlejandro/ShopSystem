package co.edu.uniquindio.shopSystem.dto.CarritoDTOs;

import org.bson.types.ObjectId;

public record ActualizarItemCarritoDTO(
        ObjectId idCliente,
        ObjectId idProducto,
        int nuevaCantidad
) {
}
