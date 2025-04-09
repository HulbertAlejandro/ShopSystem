package co.edu.uniquindio.shopSystem.servicios.interfaces;

import co.edu.uniquindio.shopSystem.dto.CuentaDTOs.*;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.CrearProductoDTO;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.EditarProductoDTO;
import co.edu.uniquindio.shopSystem.dto.TokenDTOs.TokenDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface CuentaServicio {

    /**
     * Crea una nueva cuenta de usuario. Si el correo corresponde al administrador, activa la cuenta inmediatamente;
     * de lo contrario, la cuenta queda inactiva y se envía un código de activación al correo.
     *
     * @param cuenta Objeto con los datos necesarios para crear la cuenta.
     * @throws Exception Si ya existe una cuenta con la cédula o correo proporcionado.
     */
    void crearCuenta(CrearCuentaDTO cuenta) throws Exception;

    /**
     * Edita la información personal y de acceso de una cuenta existente.
     *
     * @param cuenta DTO con los nuevos datos de la cuenta.
     * @return ID de la cuenta modificada.
     * @throws Exception Si la cuenta no está activa.
     */
    String editarCuenta(EditarCuentaDTO cuenta) throws Exception;

    /**
     * Elimina lógicamente una cuenta por su ID, cambiando su estado a ELIMINADO.
     *
     * @param id ID de la cuenta a eliminar.
     * @return Mensaje de confirmación.
     * @throws Exception Si la cuenta no está activa.
     */
    String eliminarCuenta(String id) throws Exception;

    /**
     * Elimina lógicamente una cuenta buscando por cédula, cambiando su estado a ELIMINADO.
     *
     * @param id Cédula de la cuenta a eliminar.
     * @return Mensaje de confirmación.
     * @throws Exception Si la cuenta no existe.
     */
    String eliminarCuentaCedula(String id) throws Exception;

    /**
     * Obtiene la información personal y de contacto de una cuenta activa.
     *
     * @param id ID de la cuenta.
     * @return DTO con la información de la cuenta, o null si la cuenta no existe o no está activa.
     * @throws Exception Si ocurre un error al obtener la cuenta.
     */
    InformacionCuentaDTO obtenerInformacionCuenta(String id) throws Exception;

    /**
     * Refresca el token JWT si el token actual ha expirado.
     *
     * @param tokenDTO DTO que contiene el token actual.
     * @return Un nuevo TokenDTO con un token JWT actualizado.
     * @throws Exception Si el token aún es válido o si la cuenta asociada no está activa.
     */
    TokenDTO refreshToken(TokenDTO tokenDTO) throws Exception;

    /**
     * Genera y envía un código de recuperación de contraseña al correo asociado con la cuenta.
     *
     * @param enviarCodigoDTO DTO con el correo del usuario que solicita el código.
     * @return Mensaje de confirmación del envío.
     * @throws Exception Si el correo no está registrado, la cuenta no está activa, o ocurre un error al enviar el correo.
     */
    String enviarCodigoRecuperacionPassword(EnviarCodigoDTO enviarCodigoDTO) throws Exception;

    /**
     * Cambia la contraseña de una cuenta si el código de verificación es válido y no ha expirado.
     *
     * @param cambiarPasswordDTO DTO con el correo, nuevo password y código de verificación.
     * @return Mensaje de éxito si el cambio es correcto.
     * @throws Exception Si el correo no está registrado, la cuenta no está activa, el código es incorrecto o ha expirado.
     */
    String cambiarPassword(CambiarPasswordDTO cambiarPasswordDTO) throws Exception;

    /**
     * Inicia sesión de un usuario verificando credenciales y enviando un código de activación por correo.
     *
     * @param loginDTO DTO con correo y contraseña.
     * @return Token de autenticación.
     * @throws Exception Si el usuario no existe, está eliminado, no está activo o las credenciales son incorrectas.
     */
    TokenDTO iniciarSesion(LoginDTO loginDTO) throws Exception;

    /**
     * Lista las cuentas de usuarios con rol cliente.
     *
     * @return Lista de DTOs con la información de cada cliente.
     * @throws Exception Si ocurre un error al consultar los datos.
     */
    List<InformacionCuentaDTO> listarCuentasClientes() throws Exception;

    /**
     * Activa una cuenta si el código de verificación de registro es válido y no ha expirado.
     *
     * @param validarCuentaDTO DTO con correo y código de verificación.
     * @return Mensaje de éxito si la cuenta se activa correctamente.
     * @throws Exception Si la cuenta ya está activa, no existe o el código es inválido o expiró.
     */
    String activarCuenta(ValidarCuentaDTO validarCuentaDTO) throws Exception;

    /**
     * Verifica una cuenta para permitir el inicio de sesión después de ingresar el código enviado por correo.
     *
     * @param verificacionDTO DTO con el correo y código de verificación.
     * @return Token de sesión si la verificación es exitosa.
     * @throws Exception Si el código es inválido, ha expirado o la cuenta no está disponible.
     */
    TokenDTO verificarCuenta(VerificacionDTO verificacionDTO) throws Exception;

    /**
     * Crea un nuevo producto, validando que no exista previamente y que el precio sea válido.
     *
     * @param producto DTO con la información del nuevo producto.
     * @throws Exception Si ya existe un producto con la misma referencia o el precio es inválido.
     */
    void crearProducto(@Valid CrearProductoDTO producto) throws Exception;
}
