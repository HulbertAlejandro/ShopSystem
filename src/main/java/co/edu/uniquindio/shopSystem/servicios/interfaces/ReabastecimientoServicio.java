package co.edu.uniquindio.shopSystem.servicios.interfaces;

import co.edu.uniquindio.shopSystem.dto.AbastecimientoDTOs.MostrarOrdenReabastecimientoDTO;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.ObtenerProductoDTO;
import com.google.api.client.testing.util.MockSleeper;

import java.util.List;

public interface ReabastecimientoServicio {

    List<MostrarOrdenReabastecimientoDTO> listarOrdenesReabastecimiento() throws Exception;

}
