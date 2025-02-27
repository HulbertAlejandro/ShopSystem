package co.edu.uniquindio.shopSystem.servicios.interfaces;

import co.edu.uniquindio.shopSystem.dto.CarritoDTOs.*;
import co.edu.uniquindio.shopSystem.modelo.documentos.Carrito;
import org.bson.types.ObjectId;

import java.util.List;

public interface CarritoServicio {

    String agregarItemCarrito(ProductoCarritoDTO productoCarritoDTO) throws Exception;

    String eliminarItemCarrito(String idDetalle, String idCarrito) throws Exception;

    void eliminarCarrito(EliminarCarritoDTO eliminarCarritoDTO) throws Exception;

    VistaCarritoDTO obtenerInformacionCarrito(ObjectId id) throws Exception;

    String actualizarItemCarrito(ActualizarItemCarritoDTO actualizarItemCarritoDTO) throws Exception;

    double calcularTotalCarrito(ObjectId idCliente) throws Exception;

    String vaciarCarrito(ObjectId id) throws Exception;

    List<CarritoListDTO> listarCarritos() throws Exception;




}
