package co.edu.uniquindio.shopSystem.servicios.implementaciones;

import co.edu.uniquindio.shopSystem.config.JWTUtils;
import co.edu.uniquindio.shopSystem.dto.CuentaDTOs.*;
import co.edu.uniquindio.shopSystem.dto.EmailDTOs.EmailDTO;
import co.edu.uniquindio.shopSystem.dto.TokenDTOs.TokenDTO;
import co.edu.uniquindio.shopSystem.modelo.documentos.Cuenta;
import co.edu.uniquindio.shopSystem.modelo.enums.EstadoCuenta;
import co.edu.uniquindio.shopSystem.modelo.enums.Rol;
import co.edu.uniquindio.shopSystem.modelo.vo.CodigoValidacion;
import co.edu.uniquindio.shopSystem.modelo.vo.Usuario;
import co.edu.uniquindio.shopSystem.repositorios.CuentaRepo;
import co.edu.uniquindio.shopSystem.servicios.interfaces.CuentaServicio;
import co.edu.uniquindio.shopSystem.servicios.interfaces.EmailServicio;

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
    private JWTUtils jwtUtils;
    private final CuentaRepo cuentaRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public CuentaServicioImpl(CuentaRepo cuentaRepo, EmailServicio emailServicio, BCryptPasswordEncoder passwordEncoder, JWTUtils jwtUtils) {
        this.cuentaRepo = cuentaRepo;
        this.emailServicio = emailServicio;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

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

        // NESTOR CASTELBLANCO 2/03/25
        // COMENTE LA LINEAS DE ABAJO YA QUE SE ROMPÍA EL BACKEND AL ENVIAR EL CORREO


        // Enviar correo de activación solo si no es administrador
        if (!"admin@gmail.com".equals(cuenta.correo())) {
            emailServicio.enviarCorreo(new EmailDTO("Codigo de activación de cuenta de SG Supermercados",
                    "El código de activación asignado para activar la cuenta es el siguiente: " + codigoActivacion, nuevaCuenta.getEmail()));
        }

    }


    private boolean existeCedula(String cedula) {
        return cuentaRepo.buscarCuentaPorCedula(cedula).isPresent();
    }

    private boolean existeCorreo(String correo) {
        return cuentaRepo.buscarCuentaPorCorreo(correo).isPresent();
    }

    private String generarCodigoValidacion() {
        String cadena = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            int indice = (int) (Math.random() * cadena.length());
            resultado.append(cadena.charAt(indice));
        }

        return resultado.toString();
    }

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

    private Cuenta obtenerCuentaCedula(String id) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarCuentaPorCedula(id);

        if (cuentaOptional.isEmpty()) {
            throw new Exception("La cuenta con la cédula: " + id + " no existe");
        }

        return cuentaOptional.get();
    }

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


    private Cuenta obtenerPorEmail(String correo) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarCuentaPorCorreo(correo);

        if (cuentaOptional.isEmpty()) {
            throw new Exception("La cuenta con el correo: " + correo + " no existe");
        }
        System.out.println("Hay cuenta");
        return cuentaOptional.get();
    }

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

    public Cuenta obtenerCuentaId(String id) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.findById(id);
        if (cuentaOptional.isEmpty()) {
            throw new Exception("La cuenta con el id: " + id + " no existe");
        }

        return cuentaOptional.get();
    }

    public Cuenta obtenerCuenta(String email) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarCuentaPorCorreo(email);
        if (cuentaOptional.isEmpty()) {
            throw new Exception("La cuenta con el correo: " + email + " no existe");
        }

        return cuentaOptional.get();
    }

    //METODOS DE PRUEBA DE JUNIT
    private String generarCodigoValidacionPrueba() {
        String cadena = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            int indice = (int) (Math.random() * cadena.length());
            resultado.append(cadena.charAt(indice));
        }

        return resultado.toString();
    }

    public Cuenta obtenerPorEmailPrueba(String correo) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarCuentaPorCorreo(correo);

        if (cuentaOptional.isEmpty()) {
            throw new Exception("La cuenta con el correo: " + correo + " no existe");
        }

        return cuentaOptional.get();
    }

    private Map<String, Object> construirClaimsPrueba(Cuenta cuenta) {
        return Map.of(
                "rol", cuenta.getRol(),
                "nombre", cuenta.getUsuario().getNombre(),
                "id", cuenta.getId()
        );
    }

}
