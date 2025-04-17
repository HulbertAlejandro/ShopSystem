package co.edu.uniquindio.shopSystem.servicios.interfaces;

import co.edu.uniquindio.shopSystem.dto.CarritoDTOs.*;
import co.edu.uniquindio.shopSystem.modelo.documentos.Carrito;
import org.bson.types.ObjectId;

import java.util.List;

public interface CarritoServicio {

    /**
     * Agrega un producto al carrito de compras
     * @param productoCarritoDTO DTO con la información del producto y cliente
     * @return ID del item agregado al carrito
     * @throws Exception Si el producto no existe, no hay stock o falla la operación
     */
    String agregarItemCarrito(ProductoCarritoDTO productoCarritoDTO) throws Exception;

    /**
     * Elimina un item específico del carrito
     * @param idDetalle Identificador único del detalle del carrito
     * @param idCliente Identificador del cliente dueño del carrito
     * @return Mensaje de confirmación de la operación
     * @throws Exception Si el item o cliente no existen
     */
    String eliminarItemCarrito(String idDetalle, String idCliente) throws Exception;

    /**
     * Elimina completamente el carrito de un cliente
     * @param eliminarCarritoDTO DTO con los datos necesarios para la eliminación
     * @throws Exception Si el carrito no existe o hay errores en la operación
     */
    void eliminarCarrito(EliminarCarritoDTO eliminarCarritoDTO) throws Exception;

    /**
     * Obtiene la información completa del carrito
     * @param id Identificador único del carrito
     * @return DTO con todos los detalles del carrito
     * @throws Exception Si el carrito no existe
     */
    VistaCarritoDTO obtenerInformacionCarrito(String id) throws Exception;

    /**
     * Actualiza la cantidad de un producto en el carrito
     * @param actualizarItemCarritoDTO DTO con los datos de actualización
     * @return Mensaje de confirmación de la operación
     * @throws Exception Si el item no existe o la cantidad es inválida
     */
    String actualizarItemCarrito(ActualizarItemCarritoDTO actualizarItemCarritoDTO) throws Exception;

    /**
     * Calcula el valor total del carrito con impuestos y descuentos
     * @param idCliente Identificador del cliente dueño del carrito
     * @return Valor total del carrito como double
     * @throws Exception Si el carrito no existe o hay errores en el cálculo
     */
    double calcularTotalCarrito(String idCliente) throws Exception;

    /**
     * Vacía todos los items del carrito manteniendo la instancia
     * @param id Identificador del carrito
     * @return Mensaje de confirmación de la operación
     * @throws Exception Si el carrito no existe
     */
    String vaciarCarrito(String id) throws Exception;

    /**
     * Lista todos los carritos existentes en el sistema
     * @return Lista de DTOs con información resumida de los carritos
     * @throws Exception Si no hay carritos registrados
     */
    List<CarritoListDTO> listarCarritos() throws Exception;

    /**
     * Obtiene el identificador único del carrito asociado a un cliente
     * @param id Identificador del cliente
     * @return ID del carrito en formato String
     * @throws Exception Si el cliente no tiene carrito asociado
     */
    String obtenerIdCarrito(String id) throws Exception;


}
