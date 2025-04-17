
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.*;
import co.edu.uniquindio.shopSystem.modelo.documentos.Producto;
import co.edu.uniquindio.shopSystem.modelo.enums.TipoProducto;
import co.edu.uniquindio.shopSystem.repositorios.ProductoRepo;
import co.edu.uniquindio.shopSystem.servicios.implementaciones.ProductoServicioImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductoServicioImplTest {

    @InjectMocks
    private ProductoServicioImpl productoServicio;

    @Mock
    private ProductoRepo productoRepo;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCrearProducto() throws Exception {
        CrearProductoDTO dto = new CrearProductoDTO(
                "REF123",
                "Producto A",
                TipoProducto.ELECTRONICA,
                "imagen.jpg",
                10,
                99.99f,
                "Descripción producto"
        );

        productoServicio.crearProducto(dto);

        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepo).save(captor.capture());

        Producto producto = captor.getValue();
        assertEquals("REF123", producto.getReferencia());
        assertEquals("Producto A", producto.getNombre());
        assertEquals(TipoProducto.ELECTRONICA, producto.getTipoProducto());
        assertEquals("imagen.jpg", producto.getUrlImagen());
        assertEquals(10, producto.getUnidades());
        assertEquals(99.99f, producto.getPrecio());
        assertEquals("Descripción producto", producto.getDescripcion());
    }

    @Test
    public void testEditarProducto_existente() throws Exception {
        Producto producto = new Producto();
        producto.setReferencia("REF456");

        when(productoRepo.buscarPorReferencia("REF456")).thenReturn(Optional.of(producto));

        EditarProductoDTO dto = new EditarProductoDTO(
                "ID456",
                "REF456",
                "Producto Editado",
                "imagen-edit.jpg",
                TipoProducto.HOGAR,
                5,
                49.99f,
                "Desc editada"
        );

        String result = productoServicio.editarProducto(dto);

        verify(productoRepo).save(any(Producto.class));
        assertEquals("Producto editado exitosamente", result);
        assertEquals("Producto Editado", producto.getNombre());
        assertEquals("imagen-edit.jpg", producto.getUrlImagen());
    }

    @Test
    public void testEditarProducto_noExistente() {
        when(productoRepo.buscarPorReferencia("REF999")).thenReturn(Optional.empty());

        EditarProductoDTO dto = new EditarProductoDTO(
                "ID999",
                "REF999",
                "Producto Inexistente",
                "url",
                TipoProducto.ELECTRONICA,
                0,
                0f,
                "Desc"
        );

        Exception exception = assertThrows(Exception.class, () -> productoServicio.editarProducto(dto));
        assertEquals("Producto no encontrado", exception.getMessage());
    }

    @Test
    public void testEliminarProducto_existente() throws Exception {
        when(productoRepo.existsById("ID789")).thenReturn(true);

        String resultado = productoServicio.eliminarProducto("ID789");

        verify(productoRepo).deleteById("ID789");
        assertEquals("Producto eliminado exitosamente", resultado);
    }

    @Test
    public void testEliminarProducto_noExistente() {
        when(productoRepo.existsById("ID404")).thenReturn(false);

        Exception exception = assertThrows(Exception.class, () -> productoServicio.eliminarProducto("ID404"));
        assertEquals("Producto no encontrado", exception.getMessage());
    }

    @Test
    public void testListarProductos() {
        Producto producto = new Producto();
        producto.setCodigo("C001");
        producto.setReferencia("REF1");
        producto.setNombre("Producto 1");
        producto.setTipoProducto(TipoProducto.ELECTRONICA);
        producto.setUrlImagen("url1");
        producto.setUnidades(3);
        producto.setPrecio(30.5f);
        producto.setDescripcion("Desc 1");

        when(productoRepo.findAll()).thenReturn(List.of(producto));

        List<ObtenerProductoDTO> productos = productoServicio.listarProductos();

        assertEquals(1, productos.size());
        ObtenerProductoDTO dto = productos.get(0);
        assertEquals("C001", dto.codigo());
        assertEquals("REF1", dto.referencia());
    }

    @Test
    public void testObtenerInformacionProducto_existente() throws Exception {
        Producto producto = new Producto();
        producto.setReferencia("REF2");
        producto.setNombre("Producto Detalle");
        producto.setTipoProducto(TipoProducto.HOGAR);
        producto.setUrlImagen("img.jpg");
        producto.setUnidades(4);
        producto.setPrecio(25.99f);
        producto.setDescripcion("Descripcion larga");

        when(productoRepo.buscarPorReferencia("REF2")).thenReturn(Optional.of(producto));

        InformacionProductoDTO info = productoServicio.obtenerInformacionProducto("REF2");

        assertEquals("Producto Detalle", info.nombre());
        assertEquals(TipoProducto.HOGAR, info.tipoProducto());
    }

    @Test
    public void testObtenerInformacionProducto_noExistente() {
        when(productoRepo.buscarPorReferencia("XXX")).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> productoServicio.obtenerInformacionProducto("XXX"));
        assertEquals("El producto no existe.", ex.getMessage());
    }

    @Test
    public void testObtenerProducto_conLogs() throws Exception {
        Producto producto = new Producto();
        producto.setReferencia("REF_LOG");
        producto.setNombre("Producto Log");
        producto.setTipoProducto(TipoProducto.ELECTRONICA);
        producto.setUrlImagen("img-log.jpg");
        producto.setUnidades(1);
        producto.setPrecio(10.0f);
        producto.setDescripcion("Log test");

        when(productoRepo.buscarPorReferencia("REF_LOG")).thenReturn(Optional.of(producto));

        InformacionProductoDTO info = productoServicio.obtenerProducto("REF_LOG");

        assertEquals("Producto Log", info.nombre());
        assertEquals("img-log.jpg", info.imageUrl());
    }
}
