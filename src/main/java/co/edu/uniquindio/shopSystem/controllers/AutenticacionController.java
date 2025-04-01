package co.edu.uniquindio.shopSystem.controllers;

import co.edu.uniquindio.shopSystem.dto.CarritoDTOs.*;
import co.edu.uniquindio.shopSystem.dto.CuentaDTOs.*;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.CrearProductoDTO;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.InformacionProductoDTO;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.ObtenerProductoDTO;
import co.edu.uniquindio.shopSystem.dto.TokenDTOs.MensajeDTO;
import co.edu.uniquindio.shopSystem.dto.TokenDTOs.TokenDTO;
import co.edu.uniquindio.shopSystem.repositorios.ProductoRepo;
import co.edu.uniquindio.shopSystem.servicios.interfaces.CarritoServicio;
import co.edu.uniquindio.shopSystem.servicios.interfaces.CuentaServicio;
import co.edu.uniquindio.shopSystem.servicios.interfaces.ProductoServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AutenticacionController {


    private final CuentaServicio cuentaServicio;
    private final ProductoRepo productoRepo;
    private final ProductoServicio productoServicio;
    private final CarritoServicio carritoServicio;


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

    @PostMapping("/crear-producto")
    public ResponseEntity<MensajeDTO<String>> crearProducto(@RequestBody @Valid CrearProductoDTO producto) throws Exception{
        cuentaServicio.crearProducto(producto);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Producto creado exitosamente"));
    }

    @GetMapping("/listar-productos")
    public ResponseEntity<MensajeDTO<List<ObtenerProductoDTO>>> listarProductos() throws Exception {
        List<ObtenerProductoDTO> productos = productoServicio.listarProductos();
        return ResponseEntity.ok(new MensajeDTO<>(false, productos));
    }

    @PostMapping("/carrito/agregar-item")
    public ResponseEntity<MensajeDTO<String>> agregarItemCarrito(@Valid @RequestBody ProductoCarritoDTO productoCarritoDTO) throws Exception {
        String respuesta = carritoServicio.agregarItemCarrito(productoCarritoDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, respuesta));
    }

    @GetMapping("/producto/informacion/{id}")
    public ResponseEntity<InformacionProductoDTO> obtenerInformacionProducto(@PathVariable String id) throws Exception {
        InformacionProductoDTO eventos = productoServicio.obtenerInformacionProducto(id);
        return new ResponseEntity<>(eventos, HttpStatus.OK);
    }

    @GetMapping("/carrito/obtener-informacion/{id}")
    public ResponseEntity<MensajeDTO<VistaCarritoDTO>> obtenerInformacionCarrito(@PathVariable String id) throws Exception {
        String idCarrito = carritoServicio.obtenerIdCarrito(id);
        VistaCarritoDTO carritoDTO = carritoServicio.obtenerInformacionCarrito(idCarrito);
        return ResponseEntity.ok(new MensajeDTO<>(false, carritoDTO));
    }

    @GetMapping("/carrito/cliente/{id}")
    public ResponseEntity<VistaCarritoDTO> obtenerCarritoCliente(@PathVariable String id) throws Exception {
        VistaCarritoDTO productos = carritoServicio.obtenerInformacionCarrito(id);
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }

    @PutMapping("/carrito/actualizar-item")
    public ResponseEntity<MensajeDTO<String>> actualizarItemCarrito(@Valid @RequestBody ActualizarItemCarritoDTO actualizarItemCarritoDTO) throws Exception {
        String respuesta = carritoServicio.actualizarItemCarrito(actualizarItemCarritoDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, respuesta));
    }

    @DeleteMapping("/eliminar-producto")
    public ResponseEntity<MensajeDTO<String>> eliminarProducto(
            @RequestParam String idProducto,
            @RequestParam String idCliente) throws Exception {

        carritoServicio.eliminarItemCarrito(idProducto, idCliente);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Producto eliminado exitosamente"));
    }

}

