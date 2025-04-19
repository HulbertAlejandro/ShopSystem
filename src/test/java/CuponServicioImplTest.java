
import co.edu.uniquindio.shopSystem.dto.CuponDTOs.*;
import co.edu.uniquindio.shopSystem.modelo.documentos.Cupon;
import co.edu.uniquindio.shopSystem.modelo.enums.EstadoCupon;
import co.edu.uniquindio.shopSystem.modelo.enums.TipoCupon;
import co.edu.uniquindio.shopSystem.repositorios.CuponRepo;
import co.edu.uniquindio.shopSystem.servicios.implementaciones.CuponServicioImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CuponServicioImplTest {

    @InjectMocks
    private CuponServicioImpl cuponServicio;

    @Mock
    private CuponRepo cuponRepo;

    private Cupon cupon;
    private CrearCuponDTO crearCuponDTO;
    private EditarCuponDTO editarCuponDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup de un cupon para pruebas
        cupon = Cupon.builder()
                .id("1")
                .codigo("CUPON1")
                .descuento(10)
                .nombre("Descuento 10%")
                .tipo(TipoCupon.UNICO)
                .estado(EstadoCupon.DISPONIBLE)
                .fechaVencimiento(LocalDateTime.now().plusDays(1))
                .usos(0)
                .build();

        // Setup del DTO para crear cupon
        crearCuponDTO = new CrearCuponDTO(
                "CUPON1",
                "Descuento 10%",
                "Descripción del cupón",
                10,
                TipoCupon.UNICO,
                EstadoCupon.DISPONIBLE,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );

        // Setup del DTO para editar cupon
        editarCuponDTO = new EditarCuponDTO(
                "1",
                "CUPON1",
                15,
                "Descuento 15%",
                TipoCupon.UNICO,
                EstadoCupon.DISPONIBLE,
                LocalDateTime.now().plusDays(1)
        );
    }

    @Test
    void testCrearCuponExitoso() throws Exception {
        when(cuponRepo.buscarPorCodigo(crearCuponDTO.codigo())).thenReturn(Optional.empty());
        when(cuponRepo.save(any(Cupon.class))).thenReturn(cupon);

        String resultado = cuponServicio.crearCupon(crearCuponDTO);

        assertEquals("1", resultado);
        verify(cuponRepo, times(1)).save(any(Cupon.class));
    }

    @Test
    void testCrearCuponConCodigoExistente() throws Exception {
        when(cuponRepo.buscarPorCodigo(crearCuponDTO.codigo())).thenReturn(Optional.of(cupon));

        Exception exception = assertThrows(Exception.class, () -> cuponServicio.crearCupon(crearCuponDTO));
        assertEquals("Ya existe un cupón con este código", exception.getMessage());
    }

    @Test
    void testEditarCuponExitoso() throws Exception {
        when(cuponRepo.findById(editarCuponDTO.id())).thenReturn(Optional.of(cupon));
        when(cuponRepo.save(any(Cupon.class))).thenReturn(cupon);

        String resultado = cuponServicio.editarCupon(editarCuponDTO);

        assertEquals("Cupón editado exitosamente", resultado);
        verify(cuponRepo, times(1)).save(any(Cupon.class));
    }

    @Test
    void testEditarCuponNoExistente() throws Exception {
        when(cuponRepo.findById(editarCuponDTO.id())).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> cuponServicio.editarCupon(editarCuponDTO));
        assertEquals("Cupón no encontrado", exception.getMessage());
    }

    @Test
    void testEliminarCuponExitoso() throws Exception {
        when(cuponRepo.buscarPorCodigo(cupon.getCodigo())).thenReturn(Optional.of(cupon));

        String resultado = cuponServicio.eliminarCupon(cupon.getCodigo());

        assertEquals("Cupón eliminado exitosamente", resultado);
        verify(cuponRepo, times(1)).save(any(Cupon.class));
    }

    @Test
    void testEliminarCuponNoExistente() throws Exception {
        when(cuponRepo.buscarPorCodigo(cupon.getCodigo())).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> cuponServicio.eliminarCupon(cupon.getCodigo()));
        assertEquals("Cupón no encontrado", exception.getMessage());
    }

    @Test
    void testAplicarCuponExitoso() throws Exception {
        when(cuponRepo.buscarPorCodigo(cupon.getCodigo())).thenReturn(Optional.of(cupon));

        AplicarCuponDTO resultado = cuponServicio.aplicarCupon(cupon.getCodigo());

        assertNotNull(resultado);
        assertEquals(10, resultado.descuento());
    }

    @Test
    void testAplicarCuponInactivo() throws Exception {
        cupon.setEstado(EstadoCupon.NO_DISPONIBLE);
        when(cuponRepo.buscarPorCodigo(cupon.getCodigo())).thenReturn(Optional.of(cupon));

        Exception exception = assertThrows(Exception.class, () -> cuponServicio.aplicarCupon(cupon.getCodigo()));
        assertEquals("El cupón no está activo", exception.getMessage());
    }

    @Test
    void testAplicarCuponVencido() throws Exception {
        cupon.setFechaVencimiento(LocalDateTime.now().minusDays(1));
        when(cuponRepo.buscarPorCodigo(cupon.getCodigo())).thenReturn(Optional.of(cupon));

        Exception exception = assertThrows(Exception.class, () -> cuponServicio.aplicarCupon(cupon.getCodigo()));
        assertEquals("El cupón ha vencido", exception.getMessage());
    }

    @Test
    void testAplicarCuponConUsosExcedidos() throws Exception {
        // Configura el cupón para que tenga el número máximo de usos (por ejemplo, 5)
        cupon.setUsos(5); // Ajusta según el límite que hayas definido en tu aplicación
        when(cuponRepo.buscarPorCodigo(cupon.getCodigo())).thenReturn(Optional.of(cupon));

        // Ejecuta la prueba y verifica que la excepción sea lanzada
        Exception exception = assertThrows(Exception.class, () -> cuponServicio.aplicarCupon(cupon.getCodigo()));

        // Verifica el mensaje de la excepción
        assertEquals("El cupón ya completó el número de usos", exception.getMessage());
    }

    @Test
    void testRegistrarUsoExitoso() throws Exception {
        when(cuponRepo.buscarPorCodigo(cupon.getCodigo())).thenReturn(Optional.of(cupon));
        when(cuponRepo.save(any(Cupon.class))).thenReturn(cupon);

        cuponServicio.registrarUso(cupon.getCodigo());

        assertEquals(1, cupon.getUsos());
        verify(cuponRepo, times(1)).save(any(Cupon.class));
    }

    @Test
    void testRegistrarUsoCuponNoExistente() throws Exception {
        when(cuponRepo.buscarPorCodigo(cupon.getCodigo())).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> cuponServicio.registrarUso(cupon.getCodigo()));
        assertEquals("El cupón no existe o el código es incorrecto", exception.getMessage());
    }
}
