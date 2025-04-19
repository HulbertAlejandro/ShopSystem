
import co.edu.uniquindio.shopSystem.config.JWTUtils;
import co.edu.uniquindio.shopSystem.dto.CuentaDTOs.*;
import co.edu.uniquindio.shopSystem.dto.EmailDTOs.EmailDTO;
import co.edu.uniquindio.shopSystem.dto.TokenDTOs.TokenDTO;
import co.edu.uniquindio.shopSystem.modelo.documentos.Carrito;
import co.edu.uniquindio.shopSystem.modelo.documentos.Cuenta;
import co.edu.uniquindio.shopSystem.modelo.enums.EstadoCuenta;
import co.edu.uniquindio.shopSystem.modelo.enums.Rol;
import co.edu.uniquindio.shopSystem.modelo.vo.CodigoValidacion;
import co.edu.uniquindio.shopSystem.modelo.vo.Usuario;
import co.edu.uniquindio.shopSystem.repositorios.CarritoRepo;
import co.edu.uniquindio.shopSystem.repositorios.CuentaRepo;
import co.edu.uniquindio.shopSystem.repositorios.ProductoRepo;
import co.edu.uniquindio.shopSystem.servicios.implementaciones.CuentaServicioImpl;
import co.edu.uniquindio.shopSystem.servicios.interfaces.EmailServicio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CuentaServicioImplTest {

    @InjectMocks
    private CuentaServicioImpl cuentaServicio;

    @Mock
    private CuentaRepo cuentaRepo;

    @Mock
    private EmailServicio emailServicio;

    @Mock
    private ProductoRepo productoRepo;

    @Mock
    private CarritoRepo carritoRepo;

    @Mock
    private JWTUtils jwtUtils;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cuentaServicio = new CuentaServicioImpl(cuentaRepo, emailServicio, passwordEncoder, jwtUtils, productoRepo, carritoRepo);
    }

    @Test
    void crearCuentaCliente_DeberiaGuardarCuentaYCarrito() throws Exception {
        CrearCuentaDTO dto = new CrearCuentaDTO(
                "1234",               // cedula
                "Pepito",             // nombre
                "3111111111",         // telefono
                "Calle 1",            // direccion
                "pepito@mail.com",    // correo
                "123456",             // password
                "123456"              // confirmaPassword
        );

        when(cuentaRepo.buscarCuentaPorCedula("1234")).thenReturn(Optional.empty());
        when(cuentaRepo.buscarCuentaPorCorreo("pepito@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("hashed_password");

        cuentaServicio.crearCuenta(dto);

        verify(cuentaRepo, times(1)).save(any(Cuenta.class));
        verify(carritoRepo, times(1)).save(any(Carrito.class));
        verify(emailServicio, times(1)).enviarCorreo(any(EmailDTO.class));
    }


    @Test
    void crearCuenta_AdminDebeEstarActiva() throws Exception {
        CrearCuentaDTO dto = new CrearCuentaDTO(
                "9999",              // cedula
                "Admin",             // nombre
                "3123456789",        // telefono
                "Oficina",           // direccion
                "admin@gmail.com",   // correo
                "1234567",           // password
                "1234567"            // confirmaPassword
        );

        when(cuentaRepo.buscarCuentaPorCedula("9999")).thenReturn(Optional.empty());
        when(cuentaRepo.buscarCuentaPorCorreo("admin@gmail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("1234567")).thenReturn("hashed");

        cuentaServicio.crearCuenta(dto);

        ArgumentCaptor<Cuenta> captor = ArgumentCaptor.forClass(Cuenta.class);
        verify(cuentaRepo).save(captor.capture());

        assertEquals(EstadoCuenta.ACTIVO, captor.getValue().getEstadoCuenta());
        assertEquals(Rol.ADMINISTRADOR, captor.getValue().getRol());
        verify(emailServicio, never()).enviarCorreo(any());
    }


    @Test
    void refreshToken_TokenExpirado_GeneraNuevoToken() throws Exception {
        String viejoToken = "token.viejo";
        String correo = "usuario@mail.com";

        Cuenta cuenta = new Cuenta();
        cuenta.setEmail(correo);
        cuenta.setEstadoCuenta(EstadoCuenta.ACTIVO);
        cuenta.setRol(Rol.CLIENTE);
        cuenta.setUsuario(Usuario.builder().cedula("123").build());

        when(jwtUtils.esTokenExpirado(viejoToken)).thenReturn(true);
        when(jwtUtils.obtenerCorreoDesdeToken(viejoToken)).thenReturn(correo);
        when(cuentaRepo.buscarCuentaPorCorreo(correo)).thenReturn(Optional.of(cuenta));
        when(jwtUtils.generarToken(eq(correo), anyMap())).thenReturn("nuevo.token.jwt");

        TokenDTO result = cuentaServicio.refreshToken(new TokenDTO(viejoToken));

        assertEquals("nuevo.token.jwt", result.token());
    }

    @Test
    void editarCuenta_DeberiaActualizarCampos() throws Exception {
        EditarCuentaDTO dto = new EditarCuentaDTO("Pedro", "321", "correo@uni.com", "Cra 1", "123456", "nuevoPassword", "nuevoPassword");

        // Crear una cuenta con un usuario previamente registrado
        Cuenta cuenta = new Cuenta();
        cuenta.setEstadoCuenta(EstadoCuenta.ACTIVO);

        // Construir el objeto Usuario correctamente con el builder
        cuenta.setUsuario(Usuario.builder()
                .cedula("123")             // Cedula del usuario
                .nombre("Juan")            // Nombre del usuario
                .telefono("111")           // Teléfono del usuario
                .direccion("Calle vieja")  // Dirección del usuario
                .build());

        cuenta.setEmail("correo@uni.com");
        cuenta.setPassword("clave");

        // Mock de la búsqueda de la cuenta por correo
        when(cuentaRepo.buscarCuentaPorCorreo("correo@uni.com")).thenReturn(Optional.of(cuenta));

        // Llamar al método editarCuenta
        String id = cuentaServicio.editarCuenta(dto);

        // Verificación de la correcta actualización
        assertEquals("correo@uni.com", cuenta.getEmail());
        verify(cuentaRepo).save(any(Cuenta.class));  // Verifica que se haya guardado la cuenta
    }


    @Test
    void eliminarCuenta_DeberiaCambiarEstado() throws Exception {
        Cuenta cuenta = new Cuenta();
        cuenta.setEstadoCuenta(EstadoCuenta.ACTIVO);
        cuenta.setId("id123");

        when(cuentaRepo.findById("id123")).thenReturn(Optional.of(cuenta));

        String result = cuentaServicio.eliminarCuenta("id123");

        assertEquals("Eliminado", result);
        assertEquals(EstadoCuenta.ELIMINADO, cuenta.getEstadoCuenta());
        verify(cuentaRepo).save(cuenta);
    }

    @Test
    void eliminarCuentaCedula_DeberiaCambiarEstado() throws Exception {
        Cuenta cuenta = new Cuenta();
        cuenta.setEstadoCuenta(EstadoCuenta.ACTIVO);
        cuenta.setId("id123");

        when(cuentaRepo.findById("123456")).thenReturn(Optional.of(cuenta));

        String result = cuentaServicio.eliminarCuentaCedula("123456");

        assertEquals("Eliminado", result);
        assertEquals(EstadoCuenta.ELIMINADO, cuenta.getEstadoCuenta());
    }

    @Test
    void obtenerInformacionCuenta_DeberiaRetornarDTO() throws Exception {
        Cuenta cuenta = new Cuenta();
        cuenta.setId("abc123");
        cuenta.setEmail("ejemplo@mail.com");
        cuenta.setEstadoCuenta(EstadoCuenta.ACTIVO);
        cuenta.setUsuario(Usuario.builder()
                .cedula("999")
                .direccion("Casa")
                .telefono("777")
                .nombre("Luis")
                .build());

        when(cuentaRepo.findById("abc123")).thenReturn(Optional.of(cuenta));

        InformacionCuentaDTO info = cuentaServicio.obtenerInformacionCuenta("abc123");

        assertNotNull(info);
        assertEquals("Luis", info.nombre());
        assertEquals("Casa", info.direccion());
    }
}
