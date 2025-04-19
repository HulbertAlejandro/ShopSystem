package co.edu.uniquindio.shopSystem.servicios.interfaces;

import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.CrearProductoDTO;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.EditarProductoDTO;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.InformacionProductoDTO;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.ObtenerProductoDTO;
import co.edu.uniquindio.shopSystem.modelo.documentos.Producto;

import java.util.List;
/**
 * Interfaz que gestiona el ciclo de vida completo de los productos,
 * incluyendo operaciones CRUD y consultas con diferentes niveles de detalle.
 */
public interface ProductoServicio {

    /**
     * Registra un nuevo producto en el sistema
     * @param producto DTO con los datos requeridos para la creación (nombre, precio, stock, etc)
     * @throws Exception Si fallan validaciones de datos o errores de persistencia
     */
    void crearProducto(CrearProductoDTO producto) throws Exception;

    /**
     * Actualiza la información de un producto existente
     * @param producto DTO con el ID del producto y campos modificables
     * @return Mensaje de confirmación con resultado de la operación
     * @throws Exception Si el producto no existe o los nuevos datos son inválidos
     */
    String editarProducto(EditarProductoDTO producto) throws Exception;

    /**
     * Elimina permanentemente un producto del catálogo
     * @param id Identificador único del producto
     * @return Confirmación de la eliminación
     * @throws Exception Si el producto no existe o tiene dependencias
     */
    String eliminarProducto(String id) throws Exception;

    /**
     * Obtiene el listado completo de productos activos
     * @return Lista de DTOs con información básica para listados
     */
    List<ObtenerProductoDTO> listarProductos();

    /**
     * Obtiene información técnica completa de un producto (uso interno)
     * @param id Identificador único del producto
     * @return DTO con todos los detalles del producto incluyendo datos administrativos
     * @throws Exception Si el producto no existe
     */
    InformacionProductoDTO obtenerInformacionProducto(String id) throws Exception;

    /**
     * Obtiene información pública de un producto para visualización
     * @param referencia Código único de referencia del producto (SKU)
     * @return DTO con información apta para mostrar a clientes
     * @throws Exception Si la referencia no existe
     */
    InformacionProductoDTO obtenerProducto(String referencia) throws Exception;
}
