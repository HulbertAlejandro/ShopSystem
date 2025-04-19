package co.edu.uniquindio.shopSystem.servicios.interfaces;

import co.edu.uniquindio.shopSystem.dto.CuponDTOs.*;

import java.util.List;

public interface CuponServicio {
    /**
     * Crea un nuevo cupón en el sistema con los datos proporcionados.
     * @param cupon Objeto DTO con los datos necesarios para la creación del cupón
     * @return Mensaje de confirmación o identificador del cupón creado
     * @throws Exception Si falla la validación de datos o ocurre un error en el sistema
     */
    String crearCupon(CrearCuponDTO cupon) throws Exception;

    /**
     * Modifica los datos de un cupón existente.
     * @param cupon Objeto DTO con los datos actualizados del cupón
     * @return Mensaje de confirmación de la operación
     * @throws Exception Si el cupón no existe o los datos son inválidos
     */
    String editarCupon(EditarCuponDTO cupon) throws Exception;

    /**
     * Elimina permanentemente un cupón del sistema.
     * @param id Identificador único del cupón a eliminar
     * @return Mensaje de confirmación de la eliminación
     * @throws Exception Si el cupón no existe o hay errores en el proceso
     */
    String eliminarCupon(String id) throws Exception;

    /**
     * Obtiene el listado completo de cupones registrados en el sistema.
     * @return Lista de DTOs con información básica de los cupones
     * @throws Exception Si ocurre un error al acceder a los datos
     */
    List<ObtenerCuponDTO> listarCupones() throws Exception;

    /**
     * Recupera información detallada de un cupón específico.
     * @param codigo Código único del cupón a consultar
     * @return DTO con todos los datos relevantes del cupón
     * @throws Exception Si el cupón no existe o hay errores en la consulta
     */
    InformacionCuponDTO obtenerInformacionCupon(String codigo) throws Exception;

    /**
     * Aplica un cupón a una transacción y devuelve el resultado de la aplicación.
     * @param codigo Código del cupón a aplicar
     * @return DTO con el resultado de la aplicación del cupón
     * @throws Exception Si el cupón es inválido, está inactivo o ha expirado
     */
    AplicarCuponDTO aplicarCupon(String codigo) throws Exception;

    /**
     * Registra el uso de un cupón para llevar el control de sus utilizaciones.
     * @param idCupon Identificador único del cupón utilizado
     * @throws Exception Si ocurre un error al registrar el uso
     */
    void registrarUso(String idCupon) throws Exception;
}


