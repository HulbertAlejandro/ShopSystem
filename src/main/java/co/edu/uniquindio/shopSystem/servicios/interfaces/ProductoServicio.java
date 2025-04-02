package co.edu.uniquindio.shopSystem.servicios.interfaces;

import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.CrearProductoDTO;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.EditarProductoDTO;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.InformacionProductoDTO;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.ObtenerProductoDTO;
import co.edu.uniquindio.shopSystem.modelo.documentos.Producto;

import java.util.List;

public interface ProductoServicio {
    void crearProducto(CrearProductoDTO producto) throws Exception;

    String editarProducto(EditarProductoDTO producto) throws Exception;

    String eliminarProducto(String id) throws Exception;

    List<ObtenerProductoDTO> listarProductos();

    InformacionProductoDTO obtenerInformacionProducto(String id) throws Exception;

    Producto obtenerProducto (String id) throws Exception;
}
