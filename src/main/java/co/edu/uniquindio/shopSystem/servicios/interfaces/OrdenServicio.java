package co.edu.uniquindio.shopSystem.servicios.interfaces;

import co.edu.uniquindio.shopSystem.dto.OrdenDTO.CrearOrdenDTO;
import co.edu.uniquindio.shopSystem.dto.OrdenDTO.InformacionOrdenDTO;
import co.edu.uniquindio.shopSystem.modelo.documentos.Orden;
import com.mercadopago.resources.preference.Preference;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;
/**
 * Interfaz que define las operaciones para la gestión de órdenes de compra,
 * incluyendo creación, procesamiento de pagos y consultas. Integra funcionalidades
 * con MercadoPago para el procesamiento de transacciones.
 */
public interface OrdenServicio {

    /**
     * Crea una nueva orden de compra en el sistema
     * @param crearOrdenDTO Objeto con la información requerida para crear la orden (productos, cliente, etc)
     * @return ID único de la orden generada
     * @throws Exception Si hay errores de validación o fallos al persistir la orden
     */
    String crearOrden(CrearOrdenDTO crearOrdenDTO) throws Exception;

    /**
     * Inicia el proceso de pago para una orden a través de MercadoPago
     * @param idOrden Identificador único de la orden a pagar
     * @return Objeto Preference de MercadoPago con los datos para completar el pago
     * @throws Exception Si la orden no existe o falla la comunicación con MercadoPago
     */
    Preference realizarPago(String idOrden) throws Exception;

    /**
     * Procesa notificaciones de cambios de estado de pagos desde MercadoPago (Webhook)
     * @param request Mapa con los parámetros de la notificación recibida
     * @throws Exception Si hay inconsistencias en los datos de la notificación
     */
    void recibirNotificacionMercadoPago(Map<String, Object> request) throws Exception;

    /**
     * Obtiene los datos completos de una orden (uso interno)
     * @param codigo Identificador único de la orden
     * @return Entidad completa de la orden con todos sus detalles
     * @throws Exception Si la orden no existe o hay errores de acceso
     */
    Orden obtenerOrden(String codigo) throws Exception;

    /**
     * Obtiene información de una orden para visualización del cliente
     * @param codigo Identificador único de la orden
     * @return DTO con información filtrada y adaptada para el cliente final
     * @throws Exception Si la orden no existe o hay errores de formato
     */
    InformacionOrdenDTO obtenerOrdenCliente(String codigo) throws Exception;

    /**
     * Lista el histórico de órdenes de un cliente específico
     * @param idCliente Identificador único del cliente en formato MongoDB ObjectId
     * @return Lista de DTOs con información resumida de las órdenes
     * @throws Exception Si el cliente no existe o hay errores en la consulta
     */
    List<InformacionOrdenDTO> ordenesUsuario(ObjectId idCliente) throws Exception;
}
