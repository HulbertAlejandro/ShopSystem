package co.edu.uniquindio.shopSystem.servicios.implementaciones;

import ch.qos.logback.core.net.server.Client;
import co.edu.uniquindio.shopSystem.dto.CarritoDTOs.*;
import co.edu.uniquindio.shopSystem.modelo.documentos.Carrito;
import co.edu.uniquindio.shopSystem.modelo.documentos.Cuenta;
import co.edu.uniquindio.shopSystem.modelo.documentos.Producto;
import co.edu.uniquindio.shopSystem.modelo.vo.DetalleCarrito;
import co.edu.uniquindio.shopSystem.repositorios.CarritoRepo;
import co.edu.uniquindio.shopSystem.repositorios.CuentaRepo;
import co.edu.uniquindio.shopSystem.repositorios.ProductoRepo;
import co.edu.uniquindio.shopSystem.servicios.interfaces.CarritoServicio;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CarritoServicioImpl implements CarritoServicio {

    private final CarritoRepo carritoRepo;
    private final ProductoRepo productoRepo;
    private final CuentaRepo cuentaRepo;

    public CarritoServicioImpl(CarritoRepo carritoRepo, ProductoRepo productoRepo, CuentaRepo cuentaRepo, CuentaRepo cuentaRepo1) {
        this.carritoRepo = carritoRepo;
        this.productoRepo = productoRepo;
        this.cuentaRepo = cuentaRepo;
    }

    @Override
    public String agregarItemCarrito(ProductoCarritoDTO productoCarritoDTO) throws Exception {
        // Validación de entrada: el DTO no debe ser nulo
        if (productoCarritoDTO == null) {
            throw new IllegalArgumentException("El DTO del producto no puede ser nulo");
        }

        // 1. Buscar la cuenta del usuario en la base de datos
        Cuenta cuenta = cuentaRepo.findById(productoCarritoDTO.idUsuario())
                .orElseThrow(() -> new Exception("No se encontró la cuenta con ID: " + productoCarritoDTO.idUsuario()));

        // 2. Obtener el carrito del usuario
        Carrito carrito = carritoRepo.buscarCarritoPorIdCliente(cuenta.getUsuario().getCedula())
                .orElseThrow(() -> new Exception("El carrito no existe para el cliente con ID: " + productoCarritoDTO.idUsuario()));

        // 3. Verificar si el producto existe en la base de datos
        Producto productoSeleccionado = productoRepo.buscarPorReferencia(productoCarritoDTO.id())
                .orElseThrow(() -> new Exception("Producto no encontrado con referencia: " + productoCarritoDTO.id()));

        // Validar que el producto no esté duplicado en el carrito
        for (DetalleCarrito detalleCarrito : carrito.getItems()) {
            Optional<Producto> productoOpt = productoRepo.buscarPorReferencia(detalleCarrito.getIdProducto());
            Producto producto = productoOpt.get();
            if (producto.getReferencia().equals(productoCarritoDTO.id())) {
                throw new Exception("El producto seleccionado ya se encuentra en el carrito");
            }
        }

        // 4. Validar que el número de unidades sea mayor a cero
        if (productoCarritoDTO.unidades() <= 0) {
            throw new IllegalArgumentException("Las unidades deben ser mayores a cero");
        }

        // 5. Crear un nuevo detalle de carrito con la información del producto
        DetalleCarrito detalleCarrito = DetalleCarrito.builder()
                .idDetalleCarrito(String.valueOf(new ObjectId()))
                .cantidad(productoCarritoDTO.unidades())
                .nombreProducto(productoCarritoDTO.nombreProducto())
                .idProducto(productoCarritoDTO.id())
                .precioUnitario(productoCarritoDTO.precio())
                .build();

        // 6. Agregar el nuevo ítem al carrito y guardarlo en la base de datos
        carrito.getItems().add(detalleCarrito);
        carritoRepo.save(carrito);

        return "Item agregado al carrito correctamente";
    }

    @Override
    public String eliminarItemCarrito(String idDetalle, String idCliente) throws Exception {
        // 1. Buscar la cuenta del usuario
        Cuenta cuenta = cuentaRepo.findById(idCliente).get();

        // 2. Obtener el carrito del cliente
        Optional<Carrito> carritoCliente = carritoRepo.buscarCarritoPorIdCliente(cuenta.getUsuario().getCedula());
        if (carritoCliente.isEmpty()) {
            throw new Exception("El carrito no existe");
        }

        Carrito carrito = carritoCliente.get();
        List<DetalleCarrito> lista = carrito.getItems();

        // 3. Eliminar el ítem del carrito por su ID
        boolean removed = lista.removeIf(i -> i.getIdDetalleCarrito().equals(idDetalle));
        if (!removed) {
            throw new Exception("El producto no se encontró en el carrito");
        }

        // 4. Guardar los cambios en la base de datos
        carritoRepo.save(carrito);
        return "Producto eliminado del carrito";
    }

    @Override
    public void eliminarCarrito(EliminarCarritoDTO eliminarCarritoDTO) throws Exception {
        // 1. Buscar el carrito por su ID
        Optional<Carrito> carritoOptional = carritoRepo.buscarCarritoPorId(eliminarCarritoDTO.idCarrito());
        if (carritoOptional.isEmpty()) {
            throw new Exception("El carrito no existe");
        }

        // 2. Eliminar el carrito de la base de datos
        carritoRepo.delete(carritoOptional.get());

        // 3. Crear un nuevo carrito vacío para el cliente
        Carrito nuevoCarritoCliente = Carrito.builder()
                .items(new ArrayList<>())
                .fecha(LocalDateTime.now())
                .idUsuario(carritoOptional.get().getIdUsuario())
                .build();

        carritoRepo.save(nuevoCarritoCliente);
    }

    @Override
    public VistaCarritoDTO obtenerInformacionCarrito(String id_carrito) throws Exception {
        // 1. Buscar el carrito por su ID
        Optional<Carrito> carritoOptional = carritoRepo.buscarCarritoPorId(id_carrito);
        if (carritoOptional.isEmpty()) {
            throw new Exception("El carrito no existe");
        }

        // 2. Obtener los detalles del carrito
        Carrito carrito = carritoOptional.get();
        List<DetalleCarrito> detallesCarrito = carrito.getItems();
        LocalDateTime fecha = carrito.getFecha();

        // 3. Retornar la información en un DTO
        return new VistaCarritoDTO(carrito.getId(), detallesCarrito, fecha);
    }

    @Override
    public List<CarritoListDTO> listarCarritos() {
        // 1. Obtener todos los carritos de la base de datos
        List<Carrito> carritos = carritoRepo.findAll();

        // 2. Convertir cada carrito en un DTO y retornarlos
        return carritos.stream().map(carrito ->
                new CarritoListDTO(new ObjectId(carrito.getId()), carrito.getFecha(), carrito.getItems())
        ).collect(Collectors.toList());
    }

    @Override
    public String obtenerIdCarrito(String id) throws Exception {
        // 1. Buscar la cuenta del usuario
        Cuenta cuenta = cuentaRepo.findById(id).get();

        // 2. Obtener el carrito del usuario
        Optional<Carrito> carritoOptional = carritoRepo.buscarCarritoPorIdCliente(cuenta.getUsuario().getCedula());
        if (carritoOptional.isEmpty()) {
            throw new Exception("El carrito no existe");
        }

        // 3. Retornar el ID del carrito
        return carritoOptional.get().getId();
    }

    @Override
    public String actualizarItemCarrito(ActualizarItemCarritoDTO actualizarItemCarritoDTO) throws Exception {
        int nuevaCantidad = actualizarItemCarritoDTO.nuevaCantidad();

        // Buscar la cuenta del cliente
        Optional<Cuenta> cuentaOptional = cuentaRepo.findById(actualizarItemCarritoDTO.idCliente());
        if (cuentaOptional.isEmpty()) {
            throw new Exception("La cuenta del cliente no existe");
        }
        Cuenta cuenta = cuentaOptional.get();

        // Buscar el carrito del cliente
        Optional<Carrito> carritoCliente = carritoRepo.buscarCarritoPorIdCliente(cuenta.getUsuario().getCedula());
        if (carritoCliente.isEmpty()) {
            throw new Exception("El carrito no existe");
        }
        Carrito carrito = carritoCliente.get();

        // Buscar el item en el carrito
        for (DetalleCarrito item : carrito.getItems()) {
            if (item.getIdProducto().equals(actualizarItemCarritoDTO.idProducto())) {

                // Buscar el producto correspondiente
                Optional<Producto> productoOptional = productoRepo.buscarPorReferencia(item.getIdProducto());
                if (productoOptional.isEmpty()) {
                    throw new Exception("El producto no existe");
                }
                Producto producto = productoOptional.get();

                // Calcular la diferencia de unidades (si se agregan o se quitan)
                int diferenciaUnidades = nuevaCantidad - item.getCantidad();

                // Modificar las unidades disponibles en función de la nueva cantidad
                if (producto.getUnidades() - diferenciaUnidades < 0) {
                    throw new Exception("No hay suficiente stock disponible para este producto.");
                }
                producto.setUnidades(producto.getUnidades() - diferenciaUnidades);

                // Actualizar la cantidad en el carrito
                item.setCantidad(nuevaCantidad);

                // Guardar los cambios en el producto y en el carrito
                productoRepo.save(producto);
                carritoRepo.save(carrito);  // Se guarda el carrito, no un solo item

                return "Cantidad de producto en el carrito actualizada exitosamente";
            }
        }

        throw new Exception("El item no existe en el carrito");
    }

    @Override
    public double calcularTotalCarrito(String idCliente) throws Exception {
        Carrito carrito = obtenerCarritoCliente(idCliente);
        double total = 0;

        for (DetalleCarrito item : carrito.getItems()) {
            total += item.getCantidad() * obtenerPrecioProducto(item.getIdProducto());
        }

        return total;
    }

    private double obtenerPrecioProducto(String idProducto) throws Exception {
        Optional<Producto> producto = productoRepo.buscarPorCodigo(idProducto);

        if (producto.isEmpty()) {
            throw new Exception("El producto no existe");
        }

        System.out.println("NOMBRE DEL PRODUCTO: " + producto.get().getNombre());
        return producto.get().getPrecio();
    }

    private Carrito obtenerCarritoCliente(String idCliente) throws Exception {
        Optional<Carrito> carritoOptional = carritoRepo.buscarCarritoPorIdCliente(idCliente);
        if (carritoOptional.isEmpty()) {
            throw new Exception("El carrito con el id: " + idCliente + " no existe");
        }
        return carritoOptional.get();
    }

    @Override
    public String vaciarCarrito(String idCliente) throws Exception {
        Optional<Cuenta> clienteOptional = cuentaRepo.findById(idCliente);
        if (clienteOptional.isEmpty()) {
            throw new Exception("El cliente no existe");
        }
        Cuenta cliente = clienteOptional.get();
        Optional<Carrito> carritoOptional = carritoRepo.buscarCarritoPorIdCliente(cliente.getUsuario().getCedula());
        if (carritoOptional.isEmpty()) {
            throw new Exception("El carrito con el id: " + idCliente + " no existe");
        }

        Carrito carrito = carritoOptional.get();
        carrito.setItems(new ArrayList<>());

        carritoRepo.save(carrito);
        return "Carrito vaciado exitosamente";
    }

}
