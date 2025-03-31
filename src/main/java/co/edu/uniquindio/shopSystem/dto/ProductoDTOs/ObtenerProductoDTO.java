package co.edu.uniquindio.shopSystem.dto.ProductoDTOs;

import co.edu.uniquindio.shopSystem.modelo.enums.TipoProducto;

public record ObtenerProductoDTO(
        String codigo,
        String referencia,
        String nombre,
        TipoProducto tipoProducto,
        String imageUrl,
        int unidades,
        float precio,
        String descripcion
) {}
