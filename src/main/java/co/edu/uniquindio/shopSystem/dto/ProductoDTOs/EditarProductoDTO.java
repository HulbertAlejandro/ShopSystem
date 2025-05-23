package co.edu.uniquindio.shopSystem.dto.ProductoDTOs;

import co.edu.uniquindio.shopSystem.modelo.enums.TipoProducto;

public record EditarProductoDTO(
        String codigo,
        String referencia,
        String nombre,
        String imageUrl,
        TipoProducto tipoProducto,
        int unidades,
        float precio,
        String descripcion
) {}

