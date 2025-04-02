package co.edu.uniquindio.shopSystem.servicios.interfaces;

import co.edu.uniquindio.shopSystem.dto.OrdenDTO.CrearOrdenDTO;
import co.edu.uniquindio.shopSystem.dto.OrdenDTO.InformacionOrdenDTO;
import co.edu.uniquindio.shopSystem.modelo.documentos.Orden;
import com.mercadopago.resources.preference.Preference;

import java.util.Map;

public interface OrdenServicio {


    String crearOrden(CrearOrdenDTO crearOrdenDTO) throws Exception;

    Preference realizarPago(String idOrden) throws Exception;

    void recibirNotificacionMercadoPago(Map<String, Object> request) throws Exception;


    Orden obtenerOrden(String codigo) throws Exception;

    InformacionOrdenDTO obtenerOrdenCliente(String codigo) throws Exception;
}
