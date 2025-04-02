package co.edu.uniquindio.shopSystem.servicios.implementaciones;

import co.edu.uniquindio.shopSystem.config.JWTUtils;
import co.edu.uniquindio.shopSystem.dto.CuentaDTOs.*;
import co.edu.uniquindio.shopSystem.dto.EmailDTOs.EmailDTO;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.CrearProductoDTO;
import co.edu.uniquindio.shopSystem.dto.TokenDTOs.TokenDTO;
import co.edu.uniquindio.shopSystem.modelo.documentos.Carrito;
import co.edu.uniquindio.shopSystem.modelo.documentos.Cuenta;
import co.edu.uniquindio.shopSystem.modelo.documentos.Producto;
import co.edu.uniquindio.shopSystem.modelo.enums.EstadoCuenta;
import co.edu.uniquindio.shopSystem.modelo.enums.Rol;
import co.edu.uniquindio.shopSystem.modelo.vo.CodigoValidacion;
import co.edu.uniquindio.shopSystem.modelo.vo.Usuario;
import co.edu.uniquindio.shopSystem.repositorios.CarritoRepo;
import co.edu.uniquindio.shopSystem.repositorios.CuentaRepo;
import co.edu.uniquindio.shopSystem.repositorios.ProductoRepo;
import co.edu.uniquindio.shopSystem.servicios.interfaces.CuentaServicio;
import co.edu.uniquindio.shopSystem.servicios.interfaces.EmailServicio;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class CuentaServicioImpl implements CuentaServicio {

    private final EmailServicio emailServicio;
    private final CarritoRepo carritoRepo;
    private JWTUtils jwtUtils;
    private final CuentaRepo cuentaRepo;
    private final ProductoRepo productoRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public CuentaServicioImpl(CuentaRepo cuentaRepo, EmailServicio emailServicio, BCryptPasswordEncoder passwordEncoder, JWTUtils jwtUtils, ProductoRepo productoRepo, CarritoRepo carritoRepo) {
        this.cuentaRepo = cuentaRepo;
        this.emailServicio = emailServicio;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.productoRepo = productoRepo;
        this.carritoRepo = carritoRepo;
    }

    // Servicio para gestión de tokens JWT y operaciones CRUD de cuentas de usuario
// Autor: [Tu nombre o usuario]
// Fecha: [Fecha de creación o modificación]

    /**
     * Método para refrescar un token JWT expirado.
     * Verifica la expiración del token, estado de la cuenta y genera nuevo token si es necesario.
     * @param tokenDTO DTO con el token actual
     * @return TokenDTO con el nuevo token generado
     * @throws Exception Si el token sigue válido o la cuenta no está activa
     */
    @Override
    public TokenDTO refreshToken(TokenDTO tokenDTO) throws Exception {
        String tokenActual = tokenDTO.token();

        // Verifica si el token ha expirado
        if (jwtUtils.esTokenExpirado(tokenActual)) {
            // Extrae el correo electrónico (u otro identificador) del token
            String correoUsuario = jwtUtils.obtenerCorreoDesdeToken(tokenActual);

            // Obtiene la cuenta usando el correo extraído
            Cuenta cuenta = obtenerPorEmail(correoUsuario);

            // Verifica que la cuenta esté activa
            if (cuenta.getEstadoCuenta() != EstadoCuenta.ACTIVO) {
                throw new Exception("La cuenta no está activa.");
            }

            // Construye los claims para el nuevo token
            Map<String, Object> claims = construirClaims(cuenta);

            // Genera un nuevo token con los mismos claims y correo electrónico
            String nuevoToken = jwtUtils.generarToken(cuenta.getEmail(),  claims);

            // Retorna el nuevo token en un TokenDTO
            return new TokenDTO(nuevoToken);
        } else {
            throw new Exception("El token aún es válido, no es necesario refrescarlo.");
        }
    }

    /**
     * Método para crear una nueva cuenta de usuario.
     * Realiza validaciones, crea entidades relacionadas y envía código de activación.
     * @param cuenta DTO con datos de la nueva cuenta
     * @throws Exception Si ya existen cuentas con misma cédula o correo
     */
    @Override
    public void crearCuenta(CrearCuentaDTO cuenta) throws Exception {
        System.out.println("Datos de la cuenta que ingresaron: "+ cuenta.password() + "  " + cuenta.confirmaPassword());

        // Validaciones de existencia previa
        if (existeCedula(cuenta.cedula())) {
            throw new Exception("Ya existe una cuenta con esta cedula");
        }

        if (existeCorreo(cuenta.correo())) {
            throw new Exception("Ya existe una cuenta con este correo");
        }

        // Configuración de la nueva cuenta
        Cuenta nuevaCuenta = new Cuenta();
        nuevaCuenta.setEmail(cuenta.correo());
        nuevaCuenta.setPassword(passwordEncoder.encode(cuenta.password()));

        // Asignación especial para cuenta de administrador
        if ("admin@gmail.com".equals(cuenta.correo()) && "1234567".equals(cuenta.password())) {
            nuevaCuenta.setRol(Rol.ADMINISTRADOR);
            nuevaCuenta.setEstadoCuenta(EstadoCuenta.ACTIVO);
        } else {
            nuevaCuenta.setRol(Rol.CLIENTE);
            nuevaCuenta.setEstadoCuenta(EstadoCuenta.INACTIVO);
        }

        // Configuración de metadatos y entidades relacionadas
        nuevaCuenta.setFechaRegistro(LocalDateTime.now());
        nuevaCuenta.setUsuario(Usuario.builder()
                .cedula(cuenta.cedula())
                .direccion(cuenta.direccion())
                .nombre(cuenta.nombre())
                .telefono(cuenta.telefono()).build());

        // Generación y asignación de código de activación
        String codigoActivacion = generarCodigoValidacion();
        nuevaCuenta.setCodigoValidacionRegistro(
                new CodigoValidacion(
                        codigoActivacion,
                        LocalDateTime.now()
                )
        );

        cuentaRepo.save(nuevaCuenta);

        // Creación de carrito asociado al usuario
        Carrito carrito = Carrito.builder()
                .idUsuario(nuevaCuenta.getUsuario().getCedula())
                .fecha(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        carritoRepo.save(carrito);

        // Envío de correo de activación solo para cuentas no administrativas
        if (!"admin@gmail.com".equals(cuenta.correo())) {
            emailServicio.enviarCorreo(new EmailDTO("Codigo de activación de cuenta de SG Supermercados",
                    "El código de activación asignado para activar la cuenta es el siguiente: " + codigoActivacion, nuevaCuenta.getEmail()));
        }
    }

    /**
     * Verifica existencia de cédula en el repositorio
     * @param cedula Número de identificación a verificar
     * @return true si la cédula ya está registrada
     */
    private boolean existeCedula(String cedula) {
        return cuentaRepo.buscarCuentaPorCedula(cedula).isPresent();
    }

    /**
     * Verifica existencia de correo electrónico en el repositorio
     * @param correo Dirección de correo a verificar
     * @return true si el correo ya está registrado
     */
    private boolean existeCorreo(String correo) {
        return cuentaRepo.buscarCuentaPorCorreo(correo).isPresent();
    }

    /**
     * Genera código alfanumérico aleatorio de 6 caracteres para activación de cuenta
     * @return String con el código generado
     */
    private String generarCodigoValidacion() {
        String cadena = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            int indice = (int) (Math.random() * cadena.length());
            resultado.append(cadena.charAt(indice));
        }

        return resultado.toString();
    }

    /**
     * Actualiza información de una cuenta existente
     * @param cuenta DTO con los nuevos datos de la cuenta
     * @return ID de la cuenta actualizada
     * @throws Exception Si la cuenta no está activa
     */
    @Override
    public String editarCuenta(EditarCuentaDTO cuenta) throws Exception {
        Cuenta cuentaUsuario = obtenerCuenta(cuenta.correo());

        if (cuentaUsuario.getEstadoCuenta() != EstadoCuenta.ACTIVO) {
            throw new Exception("La cuenta no está activa");
        }

        // Actualización de campos permitidos
        cuentaUsuario.getUsuario().setNombre(cuenta.nombre());
        cuentaUsuario.getUsuario().setDireccion(cuenta.direccion());
        cuentaUsuario.getUsuario().setTelefono(cuenta.telefono());
        cuentaUsuario.setEmail(cuenta.correo());

        cuentaRepo.save(cuentaUsuario);

        return cuentaUsuario.getId();
    }

    /**
     * Elimina lógicamente una cuenta cambiando su estado a ELIMINADO (eliminación suave)
     * @param id Identificador único de la cuenta
     * @return Mensaje de confirmación
     * @throws Exception Si la cuenta no está activa o no existe
     */
    @Override
    public String eliminarCuenta(String id) throws Exception {
        Cuenta cuentaUsuario = obtenerCuentaId(id);

        if (cuentaUsuario.getEstadoCuenta() != EstadoCuenta.ACTIVO) {
            throw new Exception("La cuenta no está activa");
        }

        cuentaUsuario.setEstadoCuenta(EstadoCuenta.ELIMINADO);
        cuentaRepo.save(cuentaUsuario);

        return "Eliminado";
    }

    /**
     * Elimina una cuenta usando número de cédula como identificador
     * @param id Número de cédula del usuario
     * @return Mensaje de confirmación
     * @throws Exception Si la cuenta no existe
     */
    @Override
    public String eliminarCuentaCedula(String id) throws Exception {
        Cuenta cuentaUsuario = obtenerCuentaCedula(id);

        cuentaUsuario.setEstadoCuenta(EstadoCuenta.ELIMINADO);
        cuentaRepo.save(cuentaUsuario);

        return "Eliminado";
    }

    /**
     * Obtiene una cuenta usando número de cédula
     * @param id Número de cédula a buscar
     * @return Cuenta encontrada
     * @throws Exception Si no se encuentra la cuenta
     */
    private Cuenta obtenerCuentaCedula(String id) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.findById(id);
        if (cuentaOptional.isEmpty()) {
            throw new Exception("La cuenta con la cedula: " + id + " no existe");
        }

        return cuentaOptional.get();
    }

    /**
     * Obtiene información pública de una cuenta activa
     * @param id Identificador único de la cuenta
     * @return DTO con información básica de la cuenta
     * @throws Exception Si la cuenta no existe
     */
    @Override
    public InformacionCuentaDTO obtenerInformacionCuenta(String id) throws Exception {
        Cuenta cuentaUsuario = obtenerCuentaId(id);
        if (cuentaUsuario == null || cuentaUsuario.getEstadoCuenta() != EstadoCuenta.ACTIVO) {
            return null;
        }
        return new InformacionCuentaDTO(
                cuentaUsuario.getUsuario().getCedula(),
                cuentaUsuario.getUsuario().getNombre(),
                cuentaUsuario.getUsuario().getTelefono(),
                cuentaUsuario.getUsuario().getDireccion(),
                cuentaUsuario.getEmail()
        );
    }

    /**
     * Envía código de recuperación de contraseña al correo registrado
     * @param enviarCodigoDTO DTO con correo electrónico
     * @return Mensaje de confirmación
     * @throws Exception Si el correo no existe, cuenta inactiva o error en envío
     */
    @Override
    public String enviarCodigoRecuperacionPassword(EnviarCodigoDTO enviarCodigoDTO) throws Exception {
        String correo = enviarCodigoDTO.correo();
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarCuentaPorCorreo(correo);

        if (cuentaOptional.isEmpty()) {
            throw new Exception("El correo no está registrado");
        }

        Cuenta cuentaUsuario = cuentaOptional.get();

        if (cuentaUsuario.getEstadoCuenta() != EstadoCuenta.ACTIVO) {
            throw new Exception("La cuenta no está activa");
        }

        String codigoValidacion = generarCodigoValidacion();

        cuentaUsuario.setCodigoValidacionPassword(
                new CodigoValidacion(
                        codigoValidacion,
                        LocalDateTime.now()
                )
        );

        try {
            emailServicio.enviarCorreo(new EmailDTO(
                    "Código de recuperación de contraseña de Aseguradora LAYO",
                    "El código de recuperación asignado para reestablecer la contraseña es el siguiente: " + codigoValidacion,
                    cuentaUsuario.getEmail()
            ));
        } catch (Exception e) {
            throw new Exception("Error al enviar el correo: " + e.getMessage());
        }

        cuentaRepo.save(cuentaUsuario);

        return "Se ha enviado un código a su correo, con una duración de 15 minutos";
    }

    /**
     * Cambia la contraseña usando código de verificación
     * @param cambiarPasswordDTO DTO con datos para cambio de contraseña
     * @return Mensaje de confirmación
     * @throws Exception Si código es inválido, expirado o cuenta inactiva
     */
    @Override
    public String cambiarPassword(CambiarPasswordDTO cambiarPasswordDTO) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarCuentaPorCorreo(cambiarPasswordDTO.correo());

        if (cuentaOptional.isEmpty()) {
            throw new Exception("El correo no está registrado");
        }

        Cuenta cuentaUsuario = cuentaOptional.get();

        if (cuentaUsuario.getEstadoCuenta() != EstadoCuenta.ACTIVO) {
            throw new Exception("La cuenta no está activa");
        }

        CodigoValidacion codigoValidacion = cuentaUsuario.getCodigoValidacionPassword();

        if (codigoValidacion.getCodigoValidacion().equals(cambiarPasswordDTO.codigoVerificacion())) {
            if (codigoValidacion.getFechaCreacion().plusMinutes(15).isAfter(LocalDateTime.now())) {
                cuentaUsuario.setPassword(passwordEncoder.encode(cambiarPasswordDTO.passwordNueva()));
                cuentaRepo.save(cuentaUsuario);
            } else {
                throw new Exception("Su código de verificación ya expiró");
            }
        } else {
            throw new Exception("El código no es correcto");
        }

        return "La clave se ha cambiado correctamente";
    }

    /**
     * Proceso de autenticación de usuario con generación de token JWT
     * @param loginDTO Credenciales de acceso
     * @return DTO con token JWT
     * @throws Exception Si credenciales son inválidas, cuenta eliminada o inactiva
     */
    @Override
    public TokenDTO iniciarSesion(LoginDTO loginDTO) throws Exception {
        try {
            Cuenta cuenta = obtenerPorEmail(loginDTO.correo());

            System.out.println("Credenciales del usuario encontrado: " + cuenta.getEmail());
            System.out.println("Estado de la cuenta: " + cuenta.getEstadoCuenta());

            if (cuenta.getEstadoCuenta() == EstadoCuenta.ELIMINADO) {
                throw new Exception("La cuenta no esta registrada");
            }

            if (cuenta.getEstadoCuenta() != EstadoCuenta.ACTIVO) {
                throw new Exception("La cuenta no está activa");
            }

            if (!passwordEncoder.matches(loginDTO.password(), cuenta.getPassword())) {
                throw new Exception("La contraseña es incorrecta");
            }

            Map<String, Object> map = construirClaims(cuenta);
            String codigoActivacion = generarCodigoValidacion();

            cuenta.setCodigoValidacionSesion(
                    new CodigoValidacion(
                            codigoActivacion,
                            LocalDateTime.now()
                    )
            );

            cuentaRepo.save(cuenta);

            // Nota: Envío de correo deshabilitado temporalmente para cuentas no administrativas
            if (!"admin@gmail.com".equals(cuenta.getEmail())) {
                emailServicio.enviarCorreo(new EmailDTO("Codigo de verificacion para iniciar sesion en SG Supermercados",
                        "El código de verificacion asignado para activar la cuenta es el siguiente: " + codigoActivacion, cuenta.getEmail()));
            }

            return new TokenDTO(jwtUtils.generarToken(cuenta.getEmail(), map));
        } catch (Exception e) {
            System.out.println("Error en la autenticación: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error en la autenticación: " + e.getMessage());
        }
    }

    /**
     * Obtiene lista de cuentas de clientes con información básica
     * @return Lista de DTOs con información de cuentas
     * @throws Exception Si ocurre error en la consulta
     */
    @Override
    public List<InformacionCuentaDTO> listarCuentasClientes() throws Exception {
        List<Cuenta> cuentas = cuentaRepo.obtenerClientes();
        List<InformacionCuentaDTO> cuentasDTO = new ArrayList<>();

        for (Cuenta cuenta : cuentas) {
            if (cuenta.getUsuario() != null) {
                cuentasDTO.add(new InformacionCuentaDTO(
                        cuenta.getUsuario().getCedula(),
                        cuenta.getUsuario().getNombre(),
                        cuenta.getUsuario().getTelefono(),
                        cuenta.getUsuario().getDireccion(),
                        cuenta.getEmail()
                ));
            } else {
                System.err.println("Cuenta sin usuario: " + cuenta.getId());
            }
        }
        return cuentasDTO;
    }

    /**
     * Obtiene cuenta por correo electrónico
     * @param correo Dirección de correo a buscar
     * @return Cuenta encontrada
     * @throws Exception Si no se encuentra la cuenta
     */
    private Cuenta obtenerPorEmail(String correo) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarCuentaPorCorreo(correo);

        if (cuentaOptional.isEmpty()) {
            throw new Exception("La cuenta con el correo: " + correo + " no existe");
        }
        System.out.println("Hay cuenta");
        return cuentaOptional.get();
    }

    /**
     * Construye claims para tokens JWT con información de la cuenta
     * @param cuenta Cuenta de usuario
     * @return Mapa de claims con información relevante
     * @throws IllegalArgumentException Si la cuenta o usuario son nulos
     */
    private Map<String, Object> construirClaims(Cuenta cuenta) {
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta es nula");
        }

        if (cuenta.getUsuario() == null) {
            throw new IllegalArgumentException("El usuario asociado con la cuenta es nulo");
        }

        // Crear un mapa vacío
        Map<String, Object> claims = new HashMap<>();

        // Añadir los claims con un valor por defecto si son nulos
        claims.put("rol", cuenta.getRol() != null ? cuenta.getRol() : "Desconocido");
        claims.put("nombre", cuenta.getUsuario().getNombre() != null ? cuenta.getUsuario().getNombre() : "Desconocido");
        claims.put("id", cuenta.getId() != null ? cuenta.getId() : "Desconocido");
        claims.put("telefono", cuenta.getUsuario().getTelefono() != null ? cuenta.getUsuario().getTelefono() : "Desconocido");
        claims.put("direccion", cuenta.getUsuario().getDireccion() != null ? cuenta.getUsuario().getDireccion() : "Desconocido");

        return claims;
    }

    /**
     * Activa una cuenta usando código de validación
     * @param validarCuentaDTO DTO con correo y código de activación
     * @return Mensaje de confirmación
     * @throws Exception Si código es inválido, expirado o cuenta en estado incorrecto
     */
    @Override
    public String activarCuenta(ValidarCuentaDTO validarCuentaDTO) throws Exception {
        Optional<Cuenta> cuenta_activacion = cuentaRepo.buscarCuentaPorCorreo(validarCuentaDTO.correo());
        if (cuenta_activacion.isEmpty()) {
            throw new Exception("No se encuentra una cuenta con el correo ingresado");
        }

        Cuenta cuenta_usuario = cuenta_activacion.get();

        if (cuenta_usuario.getEstadoCuenta() == EstadoCuenta.ELIMINADO) {
            throw new Exception("La cuenta no esta disponible en la plataforma");
        }

        if (cuenta_usuario.getEstadoCuenta() == EstadoCuenta.ACTIVO) {
            throw new Exception("La cuenta ya esta activa");
        }

        if (cuenta_usuario.getCodigoValidacionRegistro().getCodigoValidacion().equals(validarCuentaDTO.codigo())){
            if (cuenta_usuario.getCodigoValidacionRegistro().getFechaCreacion().plusMinutes(15).isAfter(LocalDateTime.now())) {
                cuenta_usuario.setEstadoCuenta(EstadoCuenta.ACTIVO);
                cuentaRepo.save(cuenta_usuario);
            } else {
                throw new Exception("Su código de verificación ya expiró");
            }
        } else {
            throw new Exception("El código no es correcto");
        }

        return "Se activo la cuenta correctamente";
    }

    /**
     * Verifica código de sesión y genera token JWT
     * @param verificacionDTO DTO con correo y código de verificación
     * @return Token JWT para autenticación
     * @throws Exception Si código es inválido, expirado o cuenta en estado incorrecto
     */
    @Override
    public TokenDTO verificarCuenta(VerificacionDTO verificacionDTO) throws Exception {
        System.out.println(verificacionDTO.codigo() + " <--CODIGO/CORREO--> " + verificacionDTO.correo());

        Optional<Cuenta> cuenta_activacion = cuentaRepo.buscarCuentaPorCorreo(verificacionDTO.correo());

        if (cuenta_activacion.isEmpty()) {
            throw new Exception("No se encuentra una cuenta con el correo ingresado");
        }

        Cuenta cuenta_usuario = cuenta_activacion.get();

        if (cuenta_usuario.getEstadoCuenta() == EstadoCuenta.ELIMINADO) {
            throw new Exception("La cuenta no está disponible en la plataforma");
        }

        if (cuenta_usuario.getEstadoCuenta() == EstadoCuenta.INACTIVO) {
            throw new Exception("La cuenta no está activa");
        }

        if (cuenta_usuario.getCodigoValidacionSesion() == null ||
                cuenta_usuario.getCodigoValidacionSesion().getCodigoValidacion() == null) {
            throw new Exception("No hay código de validación registrado para esta cuenta");
        }

        System.out.println("fecha codigo ingresada : " + cuenta_usuario.getCodigoValidacionSesion().getCodigoValidacion() );
        if (!cuenta_usuario.getCodigoValidacionSesion().getCodigoValidacion().equals(verificacionDTO.codigo())) {
            throw new Exception("El código no es correcto");
        }

        if (cuenta_usuario.getCodigoValidacionRegistro() == null ||
                cuenta_usuario.getCodigoValidacionRegistro().getFechaCreacion() == null) {
            throw new Exception("No se encontró un código de registro válido");
        }

        if (cuenta_usuario.getCodigoValidacionSesion().getFechaCreacion().plusMinutes(15).isBefore(LocalDateTime.now())) {
            System.out.println("ya expiró");
            throw new Exception("Su código de verificación de ingreso ya expiró");
        }

        // Actualiza el código de sesión
        cuenta_usuario.setCodigoValidacionSesion(new CodigoValidacion(verificacionDTO.codigo(), LocalDateTime.now()));
        cuentaRepo.save(cuenta_usuario);

        // Genera el token
        Map<String, Object> map = construirClaims(cuenta_usuario);
        return new TokenDTO(jwtUtils.generarToken(cuenta_usuario.getEmail(), map));
    }

    /**
     * Crea un nuevo producto en el sistema
     * @param producto DTO con información del producto
     * @throws Exception Si ya existe producto con misma referencia o precio inválido
     */
    @Override
    public void crearProducto(CrearProductoDTO producto) throws Exception {
        if (existeProducto(producto.referencia())) {
            throw new Exception("Ya existe un producto con esta referencia");
        }

        if (producto.precio() <= 0) {
            throw new Exception("El precio no puede ser negativo");
        }

        Producto producto_nuevo = new Producto();
        producto_nuevo.setReferencia(producto.referencia());
        producto_nuevo.setNombre(producto.nombre());
        producto_nuevo.setTipoProducto(producto.tipoProducto());
        producto_nuevo.setPrecio(producto.precio());
        producto_nuevo.setUrlImagen(producto.imageUrl());
        producto_nuevo.setUnidades(producto.unidades());
        producto_nuevo.setDescripcion(producto.descripcion());

        productoRepo.save(producto_nuevo);
    }

    /**
     * Verifica existencia de producto por referencia
     * @param codigo Referencia del producto
     * @return true si el producto ya existe
     */
    private boolean existeProducto(String codigo) {
        return productoRepo.existsById(codigo);
    }

    /**
     * Obtiene cuenta por ID
     * @param id Identificador único de la cuenta
     * @return Cuenta encontrada
     * @throws Exception Si no se encuentra la cuenta
     */
    public Cuenta obtenerCuentaId(String id) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.findById(id);
        if (cuentaOptional.isEmpty()) {
            throw new Exception("La cuenta con el id: " + id + " no existe");
        }
        return cuentaOptional.get();
    }

    /**
     * Obtiene cuenta por correo electrónico
     * @param email Dirección de correo a buscar
     * @return Cuenta encontrada
     * @throws Exception Si no se encuentra la cuenta
     */
    public Cuenta obtenerCuenta(String email) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarCuentaPorCorreo(email);
        if (cuentaOptional.isEmpty()) {
            throw new Exception("La cuenta con el correo: " + email + " no existe");
        }
        return cuentaOptional.get();
    }

// ================= MÉTODOS PARA PRUEBAS UNITARIAS (JUNIT) =================

    /**
     * Genera código de validación para pruebas (misma lógica que producción)
     * @return Código alfanumérico de 6 caracteres
     */
    private String generarCodigoValidacionPrueba() {
        String cadena = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            int indice = (int) (Math.random() * cadena.length());
            resultado.append(cadena.charAt(indice));
        }
        return resultado.toString();
    }

    /**
     * Versión de prueba para obtener cuenta por correo
     * @param correo Correo a buscar
     * @return Cuenta encontrada
     * @throws Exception Si no se encuentra la cuenta
     */
    public Cuenta obtenerPorEmailPrueba(String correo) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarCuentaPorCorreo(correo);
        if (cuentaOptional.isEmpty()) {
            throw new Exception("La cuenta con el correo: " + correo + " no existe");
        }
        return cuentaOptional.get();
    }

    /**
     * Versión simplificada de construirClaims para pruebas
     * @param cuenta Cuenta a procesar
     * @return Mapa con claims básicos
     */
    private Map<String, Object> construirClaimsPrueba(Cuenta cuenta) {
        return Map.of(
                "rol", cuenta.getRol(),
                "nombre", cuenta.getUsuario().getNombre(),
                "id", cuenta.getId()
        );
    }

}
