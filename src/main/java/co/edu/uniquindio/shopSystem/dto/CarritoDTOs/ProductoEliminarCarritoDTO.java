package co.edu.uniquindio.shopSystem.dto.CarritoDTOs;

import org.bson.types.ObjectId;

public record ProductoEliminarCarritoDTO(
        ObjectId idDetalle,
        ObjectId idCarrito
) {
}