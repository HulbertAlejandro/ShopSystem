package co.edu.uniquindio.shopSystem.controllers;

import co.edu.uniquindio.shopSystem.dto.CarritoDTOs.*;
import co.edu.uniquindio.shopSystem.dto.CuentaDTOs.*;
import co.edu.uniquindio.shopSystem.dto.CuponDTOs.*;
import co.edu.uniquindio.shopSystem.dto.OrdenDTO.CrearOrdenDTO;
import co.edu.uniquindio.shopSystem.dto.OrdenDTO.IdOrdenDTO;
import co.edu.uniquindio.shopSystem.dto.OrdenDTO.InformacionOrdenDTO;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.CrearProductoDTO;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.EditarProductoDTO;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.InformacionProductoDTO;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.ObtenerProductoDTO;
import co.edu.uniquindio.shopSystem.dto.TokenDTOs.MensajeDTO;
import co.edu.uniquindio.shopSystem.dto.TokenDTOs.TokenDTO;
import co.edu.uniquindio.shopSystem.modelo.documentos.Cuenta;
import co.edu.uniquindio.shopSystem.modelo.documentos.Orden;
import co.edu.uniquindio.shopSystem.repositorios.CarritoRepo;
import co.edu.uniquindio.shopSystem.repositorios.CuentaRepo;
import co.edu.uniquindio.shopSystem.repositorios.OrdenRepo;
import co.edu.uniquindio.shopSystem.repositorios.ProductoRepo;
import co.edu.uniquindio.shopSystem.servicios.interfaces.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mercadopago.resources.preference.Preference;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.checkerframework.checker.units.qual.C;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AutenticacionController {

    private final CuentaServicio cuentaServicio;
    private final ProductoRepo productoRepo;
    private final ProductoServicio productoServicio;
    private final CarritoServicio carritoServicio;
    private final CuponServicio cuponServicio;
    private final OrdenServicio ordenServicio;
    private final CuentaRepo cuentaRepo;
    private final OrdenRepo ordenRepo;
    private final CarritoRepo carritoRepo;

    @PostMapping("/iniciar-sesion")
    public ResponseEntity<MensajeDTO<TokenDTO>> iniciarSesion(@Valid @RequestBody LoginDTO loginDTO) throws Exception{
        TokenDTO token = cuentaServicio.iniciarSesion(loginDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, token));
    }

    @PostMapping("/crear-cuenta")
    public ResponseEntity<MensajeDTO<String>> crearCuenta(@RequestBody @Valid CrearCuentaDTO cuenta) throws Exception{
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

    @DeleteMapping("/eliminar-producto/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarProducto(@PathVariable String id) throws Exception{
        productoServicio.eliminarProducto(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Producto eliminado exitosamente"));
    }

    @PutMapping("/editar-producto")
    public ResponseEntity<MensajeDTO<String>> editarProducto(@Valid @RequestBody EditarProductoDTO producto) throws Exception{
        productoServicio.editarProducto(producto);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Producto editado exitosamente"));
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

    @GetMapping("/producto/obtener/{id}")
    public ResponseEntity<MensajeDTO<InformacionProductoDTO>> obtenerProducto(@PathVariable String id) throws Exception {
        InformacionProductoDTO producto = productoServicio.obtenerProducto(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, producto));
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

    @PostMapping("/cupon/crear")
    public ResponseEntity<MensajeDTO<String>> crearCupon(@Valid @RequestBody CrearCuponDTO cuponDTO) throws Exception {
        cuponServicio.crearCupon(cuponDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cupón creado correctamente"));
    }

    @DeleteMapping("/eliminar-cupon/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarCupon(@PathVariable String id) throws Exception{
        cuponServicio.eliminarCupon(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cupon eliminado exitosamente"));
    }

    @PutMapping("/editar-cupon")
    public ResponseEntity<MensajeDTO<String>> editarCupon(@Valid @RequestBody EditarCuponDTO cupon) throws Exception{
        cuponServicio.editarCupon(cupon);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cupon editado exitosamente"));
    }

    @GetMapping("/cupon/obtener/{codigo}")
    public ResponseEntity<MensajeDTO<InformacionCuponDTO>> obtenerCupon(@PathVariable String codigo) throws Exception {
        InformacionCuponDTO cupon = cuponServicio.obtenerInformacionCupon(codigo);
        return ResponseEntity.ok(new MensajeDTO<>(false, cupon));
    }

    @GetMapping("/cupon/aplicar/{codigo}")
    public ResponseEntity<MensajeDTO<AplicarCuponDTO>> aplicarCupon(@PathVariable String codigo) throws Exception {
        AplicarCuponDTO cupon = cuponServicio.aplicarCupon(codigo);
        return ResponseEntity.ok(new MensajeDTO<>(false, cupon));
    }

    @GetMapping("/listar-cupones")
    public ResponseEntity<MensajeDTO<List<ObtenerCuponDTO>>> listarCupones() throws Exception {
        List<ObtenerCuponDTO> cupones = cuponServicio.listarCupones();
        return ResponseEntity.ok(new MensajeDTO<>(false, cupones));
    }

    @PostMapping("/orden/realizar-pago")
    public ResponseEntity<MensajeDTO<Preference>> realizarPago(@Valid @RequestBody IdOrdenDTO idOrden) throws Exception {
        System.out.println("ID DE ORDEN: " + idOrden.idOrden());
        Preference preference = ordenServicio.realizarPago(idOrden.idOrden());
        return ResponseEntity.ok().body(new MensajeDTO<>(false, preference));
    }

    // Crear una nueva orden
    @PostMapping("/orden/crear")
    public ResponseEntity<MensajeDTO<String>> crearOrden(@RequestBody CrearOrdenDTO crearOrdenDTO) throws Exception {
        String idOrden = ordenServicio.crearOrden(crearOrdenDTO);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, idOrden));
    }

    @GetMapping("/orden/obtener/{idOrden}")
    public ResponseEntity<MensajeDTO<InformacionOrdenDTO>> obtenerOrdenCliente(@PathVariable String idOrden) throws Exception {
        InformacionOrdenDTO ordenDTO = ordenServicio.obtenerOrdenCliente(idOrden);
        System.out.println(ordenDTO.idOrden() + "    ID ORDEN   ");
        return ResponseEntity.ok(new MensajeDTO<>(false, ordenDTO));
    }

    // Obtener todas las órdenes de un usuario
    @GetMapping("/orden/usuario/{idUsuario}")
    public ResponseEntity<MensajeDTO<List<InformacionOrdenDTO>>> obtenerOrdenesUsuario(@PathVariable String idUsuario) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.findById(idUsuario);
        Cuenta cuenta = cuentaOptional.get();
        List<InformacionOrdenDTO> ordenes = ordenServicio.ordenesUsuario(new ObjectId(cuenta.getId()));
        return ResponseEntity.ok().body(new MensajeDTO<>(false, ordenes));
    }

    @PostMapping("/mercadopago/notificacion")
    public ResponseEntity<String> recibirNotificacion(@RequestBody Map<String, Object> request) {
        try {
            ordenServicio.recibirNotificacionMercadoPago(request);
            return ResponseEntity.ok("Notificación recibida correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error procesando la notificación");
        }
    }

    @GetMapping("/exportar-backup")
    public ResponseEntity<byte[]> exportarBackup() throws IOException {

        Map<String, Object> datos = new HashMap<>();
        datos.put("ordenes", ordenRepo.findAll());
        datos.put("productos", productoRepo.findAll());
        datos.put("cuentas", cuentaRepo.findAll());
        datos.put("carritos", carritoRepo.findAll());

        // Serializar con ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Soporte para LocalDateTime
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print

        byte[] jsonBytes = objectMapper.writeValueAsBytes(datos);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment().filename("backup.json").build());

        return new ResponseEntity<>(jsonBytes, headers, HttpStatus.OK);
    }

}
