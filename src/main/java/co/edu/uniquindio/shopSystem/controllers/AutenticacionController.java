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
import co.edu.uniquindio.shopSystem.repositorios.CuentaRepo;
import co.edu.uniquindio.shopSystem.repositorios.ProductoRepo;
import co.edu.uniquindio.shopSystem.servicios.interfaces.*;
import com.mercadopago.resources.preference.Preference;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    /**
     * Autentica un usuario y genera token JWT
     * @param loginDTO Credenciales de acceso (email y contraseña)
     * @return Token JWT y datos de autenticación
     */
    @PostMapping("/iniciar-sesion")
    public ResponseEntity<MensajeDTO<TokenDTO>> iniciarSesion(@Valid @RequestBody LoginDTO loginDTO) throws Exception{
        TokenDTO token = cuentaServicio.iniciarSesion(loginDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, token));
    }

    /**
     * Crea una nueva cuenta de usuario
     * @param cuenta Datos de registro del usuario
     * @return Confirmación de creación exitosa
     */
    @PostMapping("/crear-cuenta")
    public ResponseEntity<MensajeDTO<String>> crearCuenta(@RequestBody @Valid CrearCuentaDTO cuenta) throws Exception{
        cuentaServicio.crearCuenta(cuenta);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cuenta creada exitosamente"));
    }

    /**
     * Verifica y renueva la sesión del usuario
     * @param verificacionDTO Contiene token de refresco y validación de seguridad
     * @return Nuevo token JWT actualizado
     */
    @PutMapping("/verificar-sesion")
    public ResponseEntity<MensajeDTO<TokenDTO>> verificarSesion(@Valid @RequestBody VerificacionDTO verificacionDTO) throws Exception {
        TokenDTO token = cuentaServicio.verificarCuenta(verificacionDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, token));
    }

    /**
     * Elimina permanentemente una cuenta del sistema
     * @param id Identificador único de la cuenta
     * @return Confirmación de eliminación exitosa
     * @apiNote Requiere permisos de administrador
     */
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarCuenta(@PathVariable String id) throws Exception{
        cuentaServicio.eliminarCuenta(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cuenta eliminada exitosamente"));
    }

    /**
     * Obtiene información detallada de una cuenta
     * @param id Identificador único de la cuenta
     * @return DTO con toda la información del perfil
     * @throws Exception Si la cuenta no existe o no tiene permisos
     */
    @GetMapping("/obtener/{id}")
    public ResponseEntity<MensajeDTO<InformacionCuentaDTO>> obtenerInformacionCuenta(@PathVariable String id) throws Exception{
        InformacionCuentaDTO info = cuentaServicio.obtenerInformacionCuenta(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, info));
    }

    /**
     * Activa una cuenta previamente registrada
     * @param cuenta DTO con código de verificación y credenciales
     * @return Confirmación de activación exitosa
     */
    @PostMapping("/activar-cuenta")
    public ResponseEntity<MensajeDTO<String>> activarCuenta(@Valid @RequestBody ValidarCuentaDTO cuenta) throws Exception{
        cuentaServicio.activarCuenta(cuenta);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cuenta activada exitosamente"));
    }

    /**
     * Actualiza la información del perfil del usuario
     * @param cuenta DTO con campos actualizables del perfil
     * @return Confirmación de actualización exitosa
     * @apiNote Campos editables: nombre, teléfono, dirección
     */
    @PutMapping("/editar-perfil")
    public ResponseEntity<MensajeDTO<String>> editarCuenta(@Valid @RequestBody EditarCuentaDTO cuenta) throws Exception{
        cuentaServicio.editarCuenta(cuenta);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cuenta editada exitosamente"));
    }

    /**
     * Obtiene listado completo de clientes registrados
     * @return Lista de DTOs con información básica de clientes
     * @throws Exception Si no hay clientes registrados
     * @apiNote Requiere permisos de administrador
     */
    @GetMapping("/listar-clientes")
    public ResponseEntity<MensajeDTO<List<InformacionCuentaDTO>>> listarCuentasCliente() throws Exception {
        List<InformacionCuentaDTO> cupones = cuentaServicio.listarCuentasClientes();
        return ResponseEntity.ok(new MensajeDTO<>(false, cupones));
    }

    /**
     * Elimina un cliente por su número de cédula
     * @param id Número de identificación del cliente
     * @return Confirmación de eliminación exitosa
     * @apiNote Eliminación física de la base de datos
     */
    @DeleteMapping("/eliminar-cliente/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarCliente(@PathVariable String id) throws Exception{
        cuentaServicio.eliminarCuentaCedula(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cliente eliminado exitosamente"));
    }

    /**
     * Crea un nuevo producto en el catálogo
     * @param producto DTO con todos los datos del producto
     * @return Confirmación de creación exitosa
     * @apiNote Requiere permisos de administrador
     */
    @PostMapping("/crear-producto")
    public ResponseEntity<MensajeDTO<String>> crearProducto(@RequestBody @Valid CrearProductoDTO producto) throws Exception{
        cuentaServicio.crearProducto(producto);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Producto creado exitosamente"));
    }

    /**
     * Obtiene el listado completo de productos disponibles
     * @return Lista de DTOs con información básica de productos
     * @throws Exception Si no hay productos registrados
     */
    @GetMapping("/listar-productos")
    public ResponseEntity<MensajeDTO<List<ObtenerProductoDTO>>> listarProductos() throws Exception {
        List<ObtenerProductoDTO> productos = productoServicio.listarProductos();
        return ResponseEntity.ok(new MensajeDTO<>(false, productos));
    }

    /**
     * Elimina un producto del sistema permanentemente
     * @param id Identificador único del producto
     * @return Confirmación de eliminación exitosa
     * @apiNote Realiza eliminación física en la base de datos
     */
    @DeleteMapping("/eliminar-producto/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarProducto(@PathVariable String id) throws Exception{
        productoServicio.eliminarProducto(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Producto eliminado exitosamente"));
    }

    /**
     * Actualiza la información de un producto en el sistema
     * @param producto DTO con campos editables del producto (precio, stock, etc)
     * @return Confirmación de actualización exitosa
     * @apiNote Requiere permisos de administrador
     */
    @PutMapping("/editar-producto")
    public ResponseEntity<MensajeDTO<String>> editarProducto(@Valid @RequestBody EditarProductoDTO producto) throws Exception{
        productoServicio.editarProducto(producto);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Producto editado exitosamente"));
    }

    /**
     * Añade un producto al carrito de compras
     * @param productoCarritoDTO Contiene ID del producto y cliente
     * @return Mensaje con resultado de la operación
     */
    @PostMapping("/carrito/agregar-item")
    public ResponseEntity<MensajeDTO<String>> agregarItemCarrito(@Valid @RequestBody ProductoCarritoDTO productoCarritoDTO) throws Exception {
        String respuesta = carritoServicio.agregarItemCarrito(productoCarritoDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, respuesta));
    }

    /**
     * Obtiene información técnica completa de un producto
     * @param id Identificador único del producto
     * @return DTO con detalles técnicos y administrativos
     */
    @GetMapping("/producto/informacion/{id}")
    public ResponseEntity<InformacionProductoDTO> obtenerInformacionProducto(@PathVariable String id) throws Exception {
        InformacionProductoDTO eventos = productoServicio.obtenerInformacionProducto(id);
        return new ResponseEntity<>(eventos, HttpStatus.OK);
    }

    /**
     * Obtiene información básica de un producto para clientes
     * @param id Identificador único del producto
     * @return DTO con datos públicos del producto
     */
    @GetMapping("/producto/obtener/{id}")
    public ResponseEntity<MensajeDTO<InformacionProductoDTO>> obtenerProducto(@PathVariable String id) throws Exception {
        InformacionProductoDTO producto = productoServicio.obtenerProducto(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, producto));
    }

    /**
     * Obtiene el contenido completo del carrito
     * @param id Identificador del cliente
     * @return DTO con lista de productos y totales
     */
    @GetMapping("/carrito/obtener-informacion/{id}")
    public ResponseEntity<MensajeDTO<VistaCarritoDTO>> obtenerInformacionCarrito(@PathVariable String id) throws Exception {
        String idCarrito = carritoServicio.obtenerIdCarrito(id);
        VistaCarritoDTO carritoDTO = carritoServicio.obtenerInformacionCarrito(idCarrito);
        return ResponseEntity.ok(new MensajeDTO<>(false, carritoDTO));
    }

    /**
     * Obtiene el contenido completo del carrito de un cliente
     * @param id Identificador único del cliente
     * @return Vista completa del carrito con productos y totales
     * @apiNote Si el carrito no existe, se crea automáticamente
     */
    @GetMapping("/carrito/cliente/{id}")
    public ResponseEntity<VistaCarritoDTO> obtenerCarritoCliente(@PathVariable String id) throws Exception {
        VistaCarritoDTO productos = carritoServicio.obtenerInformacionCarrito(id);
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }

    /**
     * Modifica la cantidad de un producto en el carrito
     * @param actualizarItemCarritoDTO Contiene nuevos valores y referencias
     * @return Confirmación de actualización
     */
    @PutMapping("/carrito/actualizar-item")
    public ResponseEntity<MensajeDTO<String>> actualizarItemCarrito(@Valid @RequestBody ActualizarItemCarritoDTO actualizarItemCarritoDTO) throws Exception {
        String respuesta = carritoServicio.actualizarItemCarrito(actualizarItemCarritoDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, respuesta));
    }

    /**
     * Remueve completamente un producto del carrito
     * @param idProducto Identificador único del producto
     * @param idCliente Identificador del cliente
     * @return Confirmación de eliminación
     * @apiNote Elimina todas las unidades del producto seleccionado
     */
    @DeleteMapping("/eliminar-producto")
    public ResponseEntity<MensajeDTO<String>> eliminarProducto(
            @RequestParam String idProducto,
            @RequestParam String idCliente) throws Exception {

        carritoServicio.eliminarItemCarrito(idProducto, idCliente);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Producto eliminado exitosamente"));
    }

    /**
     * Registra un nuevo cupón en el sistema
     * @param cuponDTO Requiere:
     *        - código: Identificador único
     *        - tipo: Descuento/Envío gratis
     *        - valor: Porcentaje o valor fijo
     *        - fechaVencimiento: Formato ISO 8601
     * @return Confirmación de creación
     * @apiNote Requiere rol de administrador
     */
    @PostMapping("/cupon/crear")
    public ResponseEntity<MensajeDTO<String>> crearCupon(@Valid @RequestBody CrearCuponDTO cuponDTO) throws Exception {
        cuponServicio.crearCupon(cuponDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cupón creado correctamente"));
    }

    /**
     * Elimina permanentemente un cupón del sistema
     * @param id Identificador único del cupón
     * @return Confirmación de eliminación
     * @apiNote No afecta órdenes históricas que usaron el cupón
     */
    @DeleteMapping("/eliminar-cupon/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarCupon(@PathVariable String id) throws Exception{
        cuponServicio.eliminarCupon(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cupon eliminado exitosamente"));
    }

    /**
     * Actualiza propiedades de un cupón
     * @param cupon DTO con:
     *        - id: Identificador a modificar
     * @return Confirmación de actualización
     * @apiNote No permite modificar el código del cupón
     */
    @PutMapping("/editar-cupon")
    public ResponseEntity<MensajeDTO<String>> editarCupon(@Valid @RequestBody EditarCuponDTO cupon) throws Exception{
        cuponServicio.editarCupon(cupon);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cupon editado exitosamente"));
    }

    /**
     * Obtiene detalles técnicos de un cupón
     * @param codigo Código único del cupón
     * @return Información completa incluyendo usos restantes y estado
     */
    @GetMapping("/cupon/obtener/{codigo}")
    public ResponseEntity<MensajeDTO<InformacionCuponDTO>> obtenerCupon(@PathVariable String codigo) throws Exception {
        InformacionCuponDTO cupon = cuponServicio.obtenerInformacionCupon(codigo);
        return ResponseEntity.ok(new MensajeDTO<>(false, cupon));
    }

    /**
     * Valida y aplica un cupón al carrito activo
     * @param codigo Código del cupón a aplicar
     * @return Detalles del descuento aplicado y nuevo total
     * @throws Exception Si el cupón es inválido o está expirado
     */
    @GetMapping("/cupon/aplicar/{codigo}")
    public ResponseEntity<MensajeDTO<AplicarCuponDTO>> aplicarCupon(@PathVariable String codigo) throws Exception {
        AplicarCuponDTO cupon = cuponServicio.aplicarCupon(codigo);
        return ResponseEntity.ok(new MensajeDTO<>(false, cupon));
    }

    /**
     * Obtiene el listado completo de cupones registrados en el sistema
     * @return Lista de DTOs con información básica de cupones (activos e inactivos)
     * @throws Exception Si ocurre error al acceder a la base de datos
     * @apiNote Incluye cupones expirados pero no los eliminados
     */
    @GetMapping("/listar-cupones")
    public ResponseEntity<MensajeDTO<List<ObtenerCuponDTO>>> listarCupones() throws Exception {
        List<ObtenerCuponDTO> cupones = cuponServicio.listarCupones();
        return ResponseEntity.ok(new MensajeDTO<>(false, cupones));
    }

    /**
     * Genera la preferencia de pago en MercadoPago para una orden
     * @param idOrden DTO con identificador único de la orden
     * @return Objeto Preference de MercadoPago con datos para el checkout
     * @throws Exception Si la orden no existe o hay error en la conexión con MP
     * @apiNote Devuelve URL de pago y datos necesarios para el flujo de checkout
     */
    @PostMapping("/orden/realizar-pago")
    public ResponseEntity<MensajeDTO<Preference>> realizarPago(@Valid @RequestBody IdOrdenDTO idOrden) throws Exception {
        System.out.println("ID DE ORDEN: " + idOrden.idOrden());
        Preference preference = ordenServicio.realizarPago(idOrden.idOrden());
        return ResponseEntity.ok().body(new MensajeDTO<>(false, preference));
    }

    /**
     * Crea una nueva orden de compra a partir del carrito
     * @param crearOrdenDTO Debe contener:
     *        - ID del cliente
     *        - Lista de productos
     *        - Datos de envío
     *        - Cupón aplicado (opcional)
     * @return ID de la orden creada
     * @throws Exception Si el carrito está vacío o hay error de validación
     */
    @PostMapping("/orden/crear")
    public ResponseEntity<MensajeDTO<String>> crearOrden(@RequestBody CrearOrdenDTO crearOrdenDTO) throws Exception {
        String idOrden = ordenServicio.crearOrden(crearOrdenDTO);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, idOrden));
    }

    /**
     * Consulta la información completa de una orden específica
     * @param idOrden Identificador único de la orden
     * @return DTO con detalles de la orden incluyendo:
     *         - Productos comprados
     *         - Montos totales
     *         - Estado actual
     *         - Datos de envío
     * @throws Exception Si la orden no existe
     */
    @GetMapping("/orden/obtener/{idOrden}")
    public ResponseEntity<MensajeDTO<InformacionOrdenDTO>> obtenerOrdenCliente(@PathVariable String idOrden) throws Exception {
        InformacionOrdenDTO ordenDTO = ordenServicio.obtenerOrdenCliente(idOrden);
        System.out.println(ordenDTO.idOrden() + "    ID ORDEN   ");
        return ResponseEntity.ok(new MensajeDTO<>(false, ordenDTO));
    }

    /**
     * Obtiene el historial completo de órdenes de un cliente
     * @param idUsuario Identificador único del cliente
     * @return Lista de órdenes con información resumida
     * @throws Exception Si el usuario no existe o no tiene órdenes
     * @apiNote Las órdenes se devuelven en orden cronológico inverso
     */
    @GetMapping("/orden/usuario/{idUsuario}")
    public ResponseEntity<MensajeDTO<List<InformacionOrdenDTO>>> obtenerOrdenesUsuario(@PathVariable String idUsuario) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.findById(idUsuario);
        Cuenta cuenta = cuentaOptional.get();
        List<InformacionOrdenDTO> ordenes = ordenServicio.ordenesUsuario(new ObjectId(cuenta.getId()));
        return ResponseEntity.ok().body(new MensajeDTO<>(false, ordenes));
    }

    /**
     * Endpoint para recibir notificaciones de MercadoPago
     * @param request Datos de la notificación en formato Map
     * @return Confirmación de recepción
     * @apiNote Actualiza el estado de las órdenes según los cambios de pago
     */
    @PostMapping("/mercadopago/notificacion")
    public ResponseEntity<String> recibirNotificacion(@RequestBody Map<String, Object> request) {
        try {
            ordenServicio.recibirNotificacionMercadoPago(request);
            return ResponseEntity.ok("Notificación recibida correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error procesando la notificación");
        }
    }
}
