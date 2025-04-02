package co.edu.uniquindio.shopSystem.servicios.interfaces;

import co.edu.uniquindio.shopSystem.dto.CuponDTOs.*;

import java.util.List;

public interface CuponServicio {
    String crearCupon(CrearCuponDTO cupon) throws Exception;

    String editarCupon(EditarCuponDTO cupon) throws Exception;

    String eliminarCupon(String id) throws Exception;

    List<ObtenerCuponDTO> listarCupones();

    InformacionCuponDTO obtenerInformacionCupon(String codigo) throws Exception;
    AplicarCuponDTO aplicarCupon(String codigo) throws Exception;

    void registrarUso(String idCupon) throws Exception;
}


