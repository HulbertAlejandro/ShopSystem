package co.edu.uniquindio.shopSystem.servicios.implementaciones;

import co.edu.uniquindio.shopSystem.config.JWTUtils;
import co.edu.uniquindio.shopSystem.dto.CuentaDTOs.*;
import co.edu.uniquindio.shopSystem.dto.EmailDTOs.EmailDTO;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.CrearProductoDTO;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.EditarProductoDTO;
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

/**
 * Servicio que implementa la lógica de negocio relacionada con la gestión de cuentas de usuario.
 */
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

    /**
     * Refresca el token JWT si el token actual ha expirado.
     *
     * @param tokenDTO DTO que contiene el token actual.
     * @return Un nuevo TokenDTO con un token JWT actualizado.
     * @throws Exception Si el token aún es válido o si la cuenta asociada no está activa.
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
     * Crea una nueva cuenta de usuario. Si el correo corresponde al administrador, activa la cuenta inmediatamente;
     * de lo contrario, la cuenta queda inactiva y se envía un código de activación al correo.
     *
     * @param cuenta Objeto con los datos necesarios para crear la cuenta.
     * @throws Exception Si ya existe una cuenta con la cédula o correo proporcionado.
     */
    @Override
    public void crearCuenta(CrearCuentaDTO cuenta) throws Exception {

        System.out.println("Datos de la cuenta que ingresaron: "+ cuenta.password() + "  " + cuenta.confirmaPassword());

        if (existeCedula(cuenta.cedula())) {
            throw new Exception("Ya existe una cuenta con esta cedula");
        }

        if (existeCorreo(cuenta.correo())) {
            throw new Exception("Ya existe una cuenta con este correo");
        }

        Cuenta nuevaCuenta = new Cuenta();
        nuevaCuenta.setEmail(cuenta.correo());

        nuevaCuenta.setPassword(passwordEncoder.encode(cuenta.password()));

        // Verifica si el correo es admin@gmail.com
        if ("admin@gmail.com".equals(cuenta.correo()) && "1234567".equals(cuenta.password())) {
            nuevaCuenta.setRol(Rol.ADMINISTRADOR);  // Asigna el rol de ADMINISTRADOR
            nuevaCuenta.setEstadoCuenta(EstadoCuenta.ACTIVO);  // Asigna la cuenta como ACTIVA
        } else {
            nuevaCuenta.setRol(Rol.CLIENTE);  // Si no es admin, asigna el rol de CLIENTE
            nuevaCuenta.setEstadoCuenta(EstadoCuenta.INACTIVO);  // La cuenta estará INACTIVA por defecto
        }

        nuevaCuenta.setFechaRegistro(LocalDateTime.now());
        nuevaCuenta.setUsuario(Usuario.builder()
                .cedula(cuenta.cedula())
                .direccion(cuenta.direccion())
                .nombre(cuenta.nombre())
                .telefono(cuenta.telefono()).build());

        String codigoActivacion = generarCodigoValidacion();
        nuevaCuenta.setCodigoValidacionRegistro(
                new CodigoValidacion(
                        codigoActivacion,
                        LocalDateTime.now()
                )
        );

        cuentaRepo.save(nuevaCuenta);

        Carrito carrito = Carrito.builder()
                .idUsuario(nuevaCuenta.getUsuario().getCedula())
                .fecha(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        carritoRepo.save(carrito);

        // Enviar correo de activación solo si no es administrador
        if (!"admin@gmail.com".equals(cuenta.correo())) {
            emailServicio.enviarCorreo(new EmailDTO("Codigo de activación de cuenta de SG Supermercados",
                    "El código de activación asignado para activar la cuenta es el siguiente: " + codigoActivacion, nuevaCuenta.getEmail()));
        }

    }

    /**
     * Verifica si ya existe una cuenta registrada con la cédula proporcionada.
     *
     * @param cedula Cédula a verificar.
     * @return true si existe una cuenta con esa cédula, false en caso contrario.
     */
    private boolean existeCedula(String cedula) {
        return cuentaRepo.buscarCuentaPorCedula(cedula).isPresent();
    }

    /**
     * Verifica si ya existe una cuenta registrada con el correo proporcionado.
     *
     * @param correo Correo a verificar.
     * @return true si existe una cuenta con ese correo, false en caso contrario.
     */
    private boolean existeCorreo(String correo) {
        return cuentaRepo.buscarCuentaPorCorreo(correo).isPresent();
    }

    /**
     * Genera un código de validación aleatorio de 6 caracteres, compuesto por letras mayúsculas y números.
     *
     * @return Código de validación generado.
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
     * Edita la información personal y de acceso de una cuenta existente.
     *
     * @param cuenta DTO con los nuevos datos de la cuenta.
     * @return ID de la cuenta modificada.
     * @throws Exception Si la cuenta no está activa.
     */
    @Override
    public String editarCuenta(EditarCuentaDTO cuenta) throws Exception {

        Cuenta cuentaUsuario = obtenerCuenta(cuenta.correo());

        if (cuentaUsuario.getEstadoCuenta() != EstadoCuenta.ACTIVO) {
            throw new Exception("La cuenta no está activa");
        }

        cuentaUsuario.getUsuario().setNombre(cuenta.nombre());
        cuentaUsuario.getUsuario().setDireccion(cuenta.direccion());
        cuentaUsuario.getUsuario().setTelefono(cuenta.telefono());
        cuentaUsuario.getUsuario().setCedula(cuentaUsuario.getUsuario().getCedula());
        cuentaUsuario.setEmail(cuenta.correo());
        cuentaUsuario.setPassword(cuentaUsuario.getPassword());

        cuentaRepo.save(cuentaUsuario);

        return cuentaUsuario.getId();
    }

    /**
     * Elimina lógicamente una cuenta por su ID, cambiando su estado a ELIMINADO.
     *
     * @param id ID de la cuenta a eliminar.
     * @return Mensaje de confirmación.
     * @throws Exception Si la cuenta no está activa.
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
     * Elimina lógicamente una cuenta buscando por cédula, cambiando su estado a ELIMINADO.
     *
     * @param id Cédula de la cuenta a eliminar.
     * @return Mensaje de confirmación.
     * @throws Exception Si la cuenta no existe.
     */
    @Override
    public String eliminarCuentaCedula(String id) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarCuentaPorCedula(id);

        if (cuentaOptional.isPresent()) {
            Cuenta cuentaUsuario = cuentaOptional.get();
            cuentaUsuario.setEstadoCuenta(EstadoCuenta.ELIMINADO);
            cuentaRepo.save(cuentaUsuario);
            return "Eliminado";
        } else {
            throw new Exception("Cuenta no encontrada con la cédula: " + id);
        }
    }

    /**
     * Obtiene una cuenta a partir de su cédula.
     *
     * @param id Cédula del usuario asociada a la cuenta.
     * @return La cuenta correspondiente a la cédula.
     * @throws Exception Si no existe ninguna cuenta con la cédula proporcionada.
     */
    private Cuenta obtenerCuentaCedula(String id) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarCuentaPorCedula(id);

        if (cuentaOptional.isEmpty()) {
            throw new Exception("La cuenta con la cédula: " + id + " no existe");
        }

        return cuentaOptional.get();
    }

    /**
     * Obtiene la información personal y de contacto de una cuenta activa.
     *
     * @param id ID de la cuenta.
     * @return DTO con la información de la cuenta, o null si la cuenta no existe o no está activa.
     * @throws Exception Si ocurre un error al obtener la cuenta.
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
     * Genera y envía un código de recuperación de contraseña al correo asociado con la cuenta.
     *
     * @param enviarCodigoDTO DTO con el correo del usuario que solicita el código.
     * @return Mensaje de confirmación del envío.
     * @throws Exception Si el correo no está registrado, la cuenta no está activa, o ocurre un error al enviar el correo.
     */
    @Override
    public String enviarCodigoRecuperacionPassword(EnviarCodigoDTO enviarCodigoDTO) throws Exception {
        // Extrae el correo del DTO
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
     * Cambia la contraseña de una cuenta si el código de verificación es válido y no ha expirado.
     *
     * @param cambiarPasswordDTO DTO con el correo, nuevo password y código de verificación.
     * @return Mensaje de éxito si el cambio es correcto.
     * @throws Exception Si el correo no está registrado, la cuenta no está activa, el código es incorrecto o ha expirado.
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
     * Inicia sesión de un usuario verificando credenciales y enviando un código de activación por correo.
     *
     * @param loginDTO DTO con correo y contraseña.
     * @return Token de autenticación.
     * @throws Exception Si el usuario no existe, está eliminado, no está activo o las credenciales son incorrectas.
     */
    @Override
    public TokenDTO iniciarSesion(LoginDTO loginDTO) throws Exception {
        try {
            // Obtén la cuenta por el correo electrónico
            Cuenta cuenta = obtenerPorEmail(loginDTO.correo());

            System.out.println("Credenciales del usuario encontrado: " + cuenta.getEmail());
            System.out.println("Estado de la cuenta: " + cuenta.getEstadoCuenta());

            if (cuenta.getEstadoCuenta() == EstadoCuenta.ELIMINADO) {
                throw new Exception("La cuenta no esta registrada");
            }
            // Verifica si la cuenta está activa
            if (cuenta.getEstadoCuenta() != EstadoCuenta.ACTIVO) {
                throw new Exception("La cuenta no está activa");
            }

            // Verifica si la contraseña ingresada coincide
            if (!passwordEncoder.matches(loginDTO.password(), cuenta.getPassword())) {
                throw new Exception("La contraseña es incorrecta");
            }

            // Genera el token
            Map<String, Object> map = construirClaims(cuenta);
            //Envia el correo del codigo para iniciar sesion


            String codigoActivacion = generarCodigoValidacion();
            cuenta.setCodigoValidacionSesion(
                    new CodigoValidacion(
                            codigoActivacion,
                            LocalDateTime.now()
                    )
            );

            cuentaRepo.save(cuenta);

            // NESTOR CASTELBLANCO 2/03/25
            // COMENTE LA LINEAS DE ABAJO YA QUE SE ROMPÍA EL BACKEND AL ENVIAR EL CORREO


            // Enviar correo de activación solo si no es administrador
            if (!"admin@gmail.com".equals(cuenta.getEmail())) {
                emailServicio.enviarCorreo(new EmailDTO("Codigo de verificacion para iniciar sesion en SG Supermercados",
                        "El código de verificacion asignado para activar la cuenta es el siguiente: " + codigoActivacion, cuenta.getEmail()));
            }

            return new TokenDTO(jwtUtils.generarToken(cuenta.getEmail(), map));
        } catch (Exception e) {
            // Registra el error completo
            System.out.println("Error en la autenticación: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error en la autenticación: " + e.getMessage());
        }
    }

    /**
     * Lista las cuentas de usuarios con rol cliente.
     *
     * @return Lista de DTOs con la información de cada cliente.
     * @throws Exception Si ocurre un error al consultar los datos.
     */
    @Override
    public List<InformacionCuentaDTO> listarCuentasClientes() throws Exception {
        List<Cuenta> cuentas = cuentaRepo.obtenerClientesActivos();
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
     * Obtiene una cuenta por su correo.
     *
     * @param correo Correo electrónico de la cuenta.
     * @return Cuenta encontrada.
     * @throws Exception Si no se encuentra una cuenta con el correo dado.
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
     * Construye el mapa de claims que se incluirán en el token JWT.
     *
     * @param cuenta Cuenta desde la cual construir los claims.
     * @return Mapa con la información relevante del usuario.
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
     * Activa una cuenta si el código de verificación de registro es válido y no ha expirado.
     *
     * @param validarCuentaDTO DTO con correo y código de verificación.
     * @return Mensaje de éxito si la cuenta se activa correctamente.
     * @throws Exception Si la cuenta ya está activa, no existe o el código es inválido o expiró.
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
     * Verifica una cuenta para permitir el inicio de sesión después de ingresar el código enviado por correo.
     *
     * @param verificacionDTO DTO con el correo y código de verificación.
     * @return Token de sesión si la verificación es exitosa.
     * @throws Exception Si el código es inválido, ha expirado o la cuenta no está disponible.
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
     * Crea un nuevo producto, validando que no exista previamente y que el precio sea válido.
     *
     * @param producto DTO con la información del nuevo producto.
     * @throws Exception Si ya existe un producto con la misma referencia o el precio es inválido.
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
     * Verifica si un producto con el código especificado existe en el repositorio.
     *
     * @param codigo el código del producto a verificar
     * @return true si el producto existe, false en caso contrario
     */
    private boolean existeProducto(String codigo) {
        return productoRepo.existsById(codigo);
    }

    /**
     * Obtiene una cuenta por su ID.
     *
     * @param id el ID de la cuenta a buscar
     * @return la cuenta correspondiente al ID
     * @throws Exception si no se encuentra ninguna cuenta con el ID proporcionado
     */
    public Cuenta obtenerCuentaId(String id) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.findById(id);
        if (cuentaOptional.isEmpty()) {
            throw new Exception("La cuenta con el id: " + id + " no existe");
        }

        return cuentaOptional.get();
    }

    /**
     * Obtiene una cuenta por su correo electrónico.
     *
     * @param email el correo electrónico de la cuenta
     * @return la cuenta correspondiente al correo electrónico
     * @throws Exception si no se encuentra ninguna cuenta con el correo proporcionado
     */
    public Cuenta obtenerCuenta(String email) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarCuentaPorCorreo(email);
        if (cuentaOptional.isEmpty()) {
            throw new Exception("La cuenta con el correo: " + email + " no existe");
        }

        return cuentaOptional.get();
    }

    //METODOS DE PRUEBA DE JUNIT

    /**
     * Genera un código de validación aleatorio de 6 caracteres (letras mayúsculas y dígitos).
     *
     * @return el código de validación generado
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
     * Obtiene una cuenta por correo electrónico (versión utilizada en pruebas).
     *
     * @param correo el correo electrónico de la cuenta
     * @return la cuenta correspondiente al correo
     * @throws Exception si no se encuentra ninguna cuenta con el correo especificado
     */
    public Cuenta obtenerPorEmailPrueba(String correo) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarCuentaPorCorreo(correo);

        if (cuentaOptional.isEmpty()) {
            throw new Exception("La cuenta con el correo: " + correo + " no existe");
        }

        return cuentaOptional.get();
    }

    /**
     * Construye un mapa de claims para pruebas a partir de una cuenta dada.
     *
     * @param cuenta la cuenta desde la cual extraer los datos
     * @return un mapa con los claims: rol, nombre de usuario e ID
     */
    private Map<String, Object> construirClaimsPrueba(Cuenta cuenta) {
        return Map.of(
                "rol", cuenta.getRol(),
                "nombre", cuenta.getUsuario().getNombre(),
                "id", cuenta.getId()
        );
    }

}
