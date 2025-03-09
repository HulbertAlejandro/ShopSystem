package co.edu.uniquindio.shopSystem.controllers;

import co.edu.uniquindio.shopSystem.dto.CuentaDTOs.*;
import co.edu.uniquindio.shopSystem.dto.TokenDTOs.MensajeDTO;
import co.edu.uniquindio.shopSystem.dto.TokenDTOs.TokenDTO;
import co.edu.uniquindio.shopSystem.servicios.interfaces.CuentaServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AutenticacionController {


    private final CuentaServicio cuentaServicio;


    @PostMapping("/iniciar-sesion")
    public ResponseEntity<MensajeDTO<TokenDTO>> iniciarSesion(@Valid @RequestBody LoginDTO loginDTO) throws Exception{
        TokenDTO token = cuentaServicio.iniciarSesion(loginDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, token));
    }

    @PostMapping("/crear-cuenta")
    public ResponseEntity<MensajeDTO<String>> crearCuenta(@RequestBody @Valid   CrearCuentaDTO cuenta) throws Exception{
        cuentaServicio.crearCuenta(cuenta);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cuenta creada exitosamente"));
    }

    @PutMapping("/verificar-sesion")
    public ResponseEntity<MensajeDTO<TokenDTO>> verificarSesion(@Valid @RequestBody VerificacionDTO verificacionDTO) throws Exception {
        TokenDTO token = cuentaServicio.verificarCuenta(verificacionDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, token));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarCuenta(@PathVariable String id) throws Exception{
        cuentaServicio.eliminarCuenta(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cuenta eliminada exitosamente"));
    }

    @GetMapping("/obtener/{id}")
    public ResponseEntity<MensajeDTO<InformacionCuentaDTO>> obtenerInformacionCuenta(@PathVariable String id) throws Exception{
        InformacionCuentaDTO info = cuentaServicio.obtenerInformacionCuenta(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, info));
    }

    @PostMapping("/activar-cuenta")
    public ResponseEntity<MensajeDTO<String>> activarCuenta(@Valid @RequestBody ValidarCuentaDTO cuenta) throws Exception{
        cuentaServicio.activarCuenta(cuenta);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cuenta activada exitosamente"));
    }

    @PutMapping("/editar-perfil")
    public ResponseEntity<MensajeDTO<String>> editarCuenta(@Valid @RequestBody EditarCuentaDTO cuenta) throws Exception{
        cuentaServicio.editarCuenta(cuenta);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cuenta editada exitosamente"));
    }

    @GetMapping("/listar-clientes")
    public ResponseEntity<MensajeDTO<List<InformacionCuentaDTO>>> listarCuentasCliente() throws Exception {
        List<InformacionCuentaDTO> cupones = cuentaServicio.listarCuentasClientes();
        return ResponseEntity.ok(new MensajeDTO<>(false, cupones));
    }

    @DeleteMapping("/eliminar-cliente/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarCliente(@PathVariable String id) throws Exception{
        cuentaServicio.eliminarCuentaCedula(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cliente eliminado exitosamente"));
    }
}

