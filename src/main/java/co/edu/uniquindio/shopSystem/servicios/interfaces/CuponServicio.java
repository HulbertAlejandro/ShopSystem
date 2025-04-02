package co.edu.uniquindio.shopSystem.servicios.interfaces;

import co.edu.uniquindio.shopSystem.dto.CuponDTOs.CrearCuponDTO;
import co.edu.uniquindio.shopSystem.dto.CuponDTOs.EditarCuponDTO;
import co.edu.uniquindio.shopSystem.dto.CuponDTOs.ObtenerCuponDTO;

import java.util.List;

public interface CuponServicio {
    String crearCupon(CrearCuponDTO cupon) throws Exception;

    String editarCupon(EditarCuponDTO cupon) throws Exception;

    String eliminarCupon(String id) throws Exception;

    List<ObtenerCuponDTO> listarCupones();
}
