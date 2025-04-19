import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import co.edu.uniquindio.shopSystem.dto.OrdenDTO.CrearOrdenDTO;
import co.edu.uniquindio.shopSystem.dto.OrdenDTO.InformacionOrdenDTO;
import co.edu.uniquindio.shopSystem.dto.OrdenDTO.ItemsDTO;
import co.edu.uniquindio.shopSystem.modelo.documentos.Orden;
import co.edu.uniquindio.shopSystem.modelo.vo.DetalleOrden;
import co.edu.uniquindio.shopSystem.repositorios.OrdenRepo;
import co.edu.uniquindio.shopSystem.servicios.implementaciones.OrdenServicioImpl;
import co.edu.uniquindio.shopSystem.servicios.interfaces.CarritoServicio;
import co.edu.uniquindio.shopSystem.servicios.interfaces.CuponServicio;
import co.edu.uniquindio.shopSystem.servicios.interfaces.ProductoServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;

public class OrdenServicioImplTest {

    @Mock
    private OrdenRepo ordenRepo;

    @Mock
    private CarritoServicio carritoServicio;

    @Mock
    private CuponServicio cuponServicio;

    @Mock
    private ProductoServicio productoServicio;

    private OrdenServicioImpl ordenServicio;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Asegúrate de inicializar correctamente todas las dependencias necesarias
        ordenServicio = new OrdenServicioImpl(ordenRepo, null, null, null, null, carritoServicio, cuponServicio);
    }

    @Test
    public void testCrearOrden() throws Exception {
        // Datos de prueba con un ObjectId válido
        ObjectId idCliente = new ObjectId(); // Genera un ObjectId válido
        CrearOrdenDTO crearOrdenDTO = new CrearOrdenDTO(
                idCliente.toString(), // Usa el ID válido generado
                "codigo_pasarela",
                List.of(new ItemsDTO("ref1", "producto1", 2, 100.0f, "id1")),
                200.0f,
                0.0f,
                0.0f,
                ""
        );

        // Comportamiento esperado
        when(ordenRepo.buscarOrdenPorId(anyString())).thenReturn(Optional.empty()); // No existe la orden

        Orden ordenGuardada = new Orden();
        ordenGuardada.setId("orden_id");
        ordenGuardada.setIdCliente(idCliente);
        // Asegúrate de inicializar detallesOrden con una lista vacía (o la lista que esperes)
        ordenGuardada.setDetallesOrden(List.of()); // Aquí estamos inicializando detallesOrden con una lista vacía

        when(ordenRepo.save(any(Orden.class))).thenReturn(ordenGuardada);

        // Llamada al método
        String idOrdenCreada = ordenServicio.crearOrden(crearOrdenDTO);

        // Verificación de resultados
        assertEquals("orden_id", idOrdenCreada); // El ID generado por MongoDB debería coincidir
        verify(ordenRepo, times(1)).save(any(Orden.class)); // Verificar que se llame al método save
        verify(carritoServicio, times(1)).vaciarCarrito(idCliente.toString()); // Verificar que se vacíe el carrito
        verify(cuponServicio, never()).registrarUso(anyString()); // No se usó cupón en este caso
    }

    @Test
    public void testCrearOrdenYaExistente() throws Exception {
        // Datos de prueba con un ObjectId válido
        ObjectId idCliente = new ObjectId(); // Genera un ObjectId válido
        CrearOrdenDTO crearOrdenDTO = new CrearOrdenDTO(
                idCliente.toString(), // Usa el ID válido generado
                "codigo_pasarela",
                List.of(new ItemsDTO("ref1", "producto1", 2, 100.0f, "id1")),
                200.0f,
                0.0f,
                0.0f,
                ""
        );

        // Comportamiento esperado
        when(ordenRepo.buscarOrdenPorId(anyString())).thenReturn(Optional.of(new Orden())); // Ya existe la orden

        // Llamada al método
        Exception exception = assertThrows(Exception.class, () -> {
            ordenServicio.crearOrden(crearOrdenDTO);
        });

        // Verificación de resultados
        assertEquals("Ya existe una orden con este código", exception.getMessage()); // Excepción esperada
        verify(ordenRepo, never()).save(any(Orden.class)); // No debe guardar la orden
    }

    @Test
    public void testObtenerOrdenCliente() throws Exception {
        // Crear ID válidos
        String idOrden = "orden_id";
        ObjectId idCliente = new ObjectId();

        // Crear objeto orden con todos los datos necesarios
        Orden orden = new Orden();
        orden.setId(idOrden);
        orden.setIdCliente(idCliente);

        // Crear DetalleOrden de prueba
        DetalleOrden detalle = new DetalleOrden();
        detalle.setIdDetalleOrden(new ObjectId().toHexString());
        detalle.setIdProducto("idProducto123");
        detalle.setPrecio(99.99f);
        detalle.setNombreProducto("Producto de prueba");
        detalle.setCantidad(2);

        // Asignar la lista de detalles a la orden
        orden.setDetallesOrden(List.of(detalle));

        // Configurar el mock
        when(ordenRepo.buscarOrdenPorId(idOrden)).thenReturn(Optional.of(orden));

        // Ejecutar método a probar
        InformacionOrdenDTO informacionOrdenDTO = ordenServicio.obtenerOrdenCliente(idOrden);

        // Verificar resultados
        assertNotNull(informacionOrdenDTO);
        assertEquals(idOrden, informacionOrdenDTO.idOrden());
        assertEquals(idCliente.toHexString(), informacionOrdenDTO.idCliente()); // Esto depende de cómo lo devuelvas
        assertFalse(informacionOrdenDTO.items().isEmpty());
    }

}
