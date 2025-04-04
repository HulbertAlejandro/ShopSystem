package co.edu.uniquindio.shopSystem.servicios.interfaces;

import co.edu.uniquindio.shopSystem.dto.CuentaDTOs.*;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.CrearProductoDTO;
import co.edu.uniquindio.shopSystem.dto.TokenDTOs.TokenDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface CuentaServicio {

    void crearCuenta(CrearCuentaDTO cuenta) throws Exception;

    String editarCuenta(EditarCuentaDTO cuenta) throws Exception;

    String eliminarCuenta(String id) throws Exception;

    String eliminarCuentaCedula(String id) throws Exception;


    InformacionCuentaDTO obtenerInformacionCuenta(String id) throws Exception;

    TokenDTO refreshToken(TokenDTO tokenDTO) throws Exception;

    String enviarCodigoRecuperacionPassword(EnviarCodigoDTO enviarCodigoDTO) throws Exception;

    String cambiarPassword(CambiarPasswordDTO cambiarPasswordDTO) throws Exception;

    TokenDTO iniciarSesion(LoginDTO loginDTO) throws Exception;

    List<InformacionCuentaDTO> listarCuentasClientes() throws Exception;

    String activarCuenta(ValidarCuentaDTO validarCuentaDTO) throws Exception;

    TokenDTO verificarCuenta(VerificacionDTO verificacionDTO) throws Exception;

    void crearProducto(@Valid CrearProductoDTO producto) throws Exception;
}
