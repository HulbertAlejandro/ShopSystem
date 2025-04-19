
import co.edu.uniquindio.shopSystem.dto.CarritoDTOs.*;
import co.edu.uniquindio.shopSystem.modelo.documentos.*;
import co.edu.uniquindio.shopSystem.modelo.enums.TipoProducto;
import co.edu.uniquindio.shopSystem.modelo.vo.DetalleCarrito;
import co.edu.uniquindio.shopSystem.modelo.vo.Usuario;
import co.edu.uniquindio.shopSystem.repositorios.*;
import co.edu.uniquindio.shopSystem.servicios.implementaciones.CarritoServicioImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CarritoServicioImplTest {

    private CarritoRepo carritoRepo;
    private ProductoRepo productoRepo;
    private CuentaRepo cuentaRepo;
    private CarritoServicioImpl carritoServicio;

    @BeforeEach
    public void setUp() {
        carritoRepo = mock(CarritoRepo.class);
        productoRepo = mock(ProductoRepo.class);
        cuentaRepo = mock(CuentaRepo.class);
        carritoServicio = new CarritoServicioImpl(carritoRepo, productoRepo, cuentaRepo, cuentaRepo);
    }

    @Test
    public void testAgregarItemCarrito_Exito() throws Exception {
        String idUsuario = "usuario123";
        String referencia = "refProd123";

        Usuario usuario = Usuario.builder().cedula("123").build();
        Cuenta cuenta = Cuenta.builder().id(idUsuario).usuario(usuario).build();
        Carrito carrito = Carrito.builder().idUsuario(idUsuario).items(new ArrayList<>()).build();
        Producto producto = Producto.builder().referencia(referencia).unidades(100).precio(2000).build();

        ProductoCarritoDTO dto = new ProductoCarritoDTO(referencia, idUsuario, "Producto Test", TipoProducto.ALIMENTOS, 2, 2000F);

        when(cuentaRepo.findById(idUsuario)).thenReturn(Optional.of(cuenta));
        when(carritoRepo.buscarCarritoPorIdCliente(usuario.getCedula())).thenReturn(Optional.of(carrito));
        when(productoRepo.buscarPorReferencia(referencia)).thenReturn(Optional.of(producto));
        when(productoRepo.findAll()).thenReturn(List.of(producto));
        when(carritoRepo.save(any(Carrito.class))).thenReturn(carrito);

        String resultado = carritoServicio.agregarItemCarrito(dto);
        assertEquals("Item agregado al carrito correctamente", resultado);
    }

    @Test
    public void testAgregarItemCarrito_ProductoDuplicado() throws Exception {
        String idUsuario = "usuario123";
        String referencia = "refProd123";

        DetalleCarrito detalle = DetalleCarrito.builder().idProducto(referencia).build();
        Usuario usuario = Usuario.builder().cedula("123").build();
        Cuenta cuenta = Cuenta.builder().id(idUsuario).usuario(usuario).build();
        Carrito carrito = Carrito.builder().idUsuario(idUsuario).items(new ArrayList<>(List.of(detalle))).build();
        Producto producto = Producto.builder().referencia(referencia).build();
        ProductoCarritoDTO dto = new ProductoCarritoDTO(referencia, idUsuario, "Producto Test", TipoProducto.ALIMENTOS, 2, 2000F);

        when(cuentaRepo.findById(idUsuario)).thenReturn(Optional.of(cuenta));
        when(carritoRepo.buscarCarritoPorIdCliente(usuario.getCedula())).thenReturn(Optional.of(carrito));
        when(productoRepo.buscarPorReferencia(referencia)).thenReturn(Optional.of(producto));

        Exception exception = assertThrows(Exception.class, () -> carritoServicio.agregarItemCarrito(dto));
        assertTrue(exception.getMessage().contains("ya se encuentra en el carrito"));
    }

    @Test
    public void testEliminarItemCarrito_Exito() throws Exception {
        String idDetalle = "detalle123";
        String idCliente = "cliente456";
        Usuario usuario = Usuario.builder().cedula("123").build();
        Cuenta cuenta = Cuenta.builder().id(idCliente).usuario(usuario).build();
        DetalleCarrito item = DetalleCarrito.builder().idDetalleCarrito(idDetalle).idProducto("codigo123").build();
        Producto producto = Producto.builder().referencia("codigo123").build();
        Carrito carrito = Carrito.builder().items(new ArrayList<>(List.of(item))).build();

        when(cuentaRepo.findById(idCliente)).thenReturn(Optional.of(cuenta));
        when(carritoRepo.buscarCarritoPorIdCliente(usuario.getCedula())).thenReturn(Optional.of(carrito));
        when(productoRepo.buscarPorReferencia("codigo123")).thenReturn(Optional.of(producto));
        when(productoRepo.findAll()).thenReturn(List.of(producto));
        when(carritoRepo.save(any(Carrito.class))).thenReturn(carrito);

        String result = carritoServicio.eliminarItemCarrito(idDetalle, idCliente);
        assertEquals("Producto eliminado del carrito", result);
    }

    @Test
    public void testObtenerInformacionCarrito_Exito() throws Exception {
        String idCarrito = "carrito123";
        Carrito carrito = Carrito.builder()
                .id(idCarrito)
                .items(List.of())
                .fecha(LocalDateTime.now())
                .build();

        when(carritoRepo.buscarCarritoPorId(idCarrito)).thenReturn(Optional.of(carrito));

        VistaCarritoDTO vista = carritoServicio.obtenerInformacionCarrito(idCarrito);

        assertNotNull(vista);
        assertEquals(idCarrito, vista.id_carrito());
    }

    @Test
    public void testVaciarCarrito_Exito() throws Exception {
        String idCliente = "cliente123";
        Usuario usuario = Usuario.builder().cedula("111").build();
        Cuenta cuenta = Cuenta.builder().id(idCliente).usuario(usuario).build();
        Carrito carrito = Carrito.builder().items(List.of(DetalleCarrito.builder().idProducto("abc").build())).build();

        when(cuentaRepo.findById(idCliente)).thenReturn(Optional.of(cuenta));
        when(carritoRepo.buscarCarritoPorIdCliente(usuario.getCedula())).thenReturn(Optional.of(carrito));
        when(carritoRepo.save(any(Carrito.class))).thenReturn(carrito);

        String result = carritoServicio.vaciarCarrito(idCliente);
        assertEquals("Carrito vaciado exitosamente", result);
    }
}
