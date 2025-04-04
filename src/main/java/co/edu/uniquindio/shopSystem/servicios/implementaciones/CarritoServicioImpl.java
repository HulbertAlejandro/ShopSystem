package co.edu.uniquindio.shopSystem.servicios.implementaciones;

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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de carrito de compras.
 * Proporciona operaciones para gestionar los productos dentro del carrito del usuario.
 */
@Service
@Transactional
public class CarritoServicioImpl implements CarritoServicio {

    /***
     * Repositorios para interactuar con la base de datos
     */
    private final CarritoRepo carritoRepo;
    private final ProductoRepo productoRepo;
    private final CuentaRepo cuentaRepo;

    /**
     * Constructor de la clase que inyecta los repositorios necesarios.
     */
    public CarritoServicioImpl(CarritoRepo carritoRepo, ProductoRepo productoRepo, CuentaRepo cuentaRepo, CuentaRepo cuentaRepo1) {
        this.carritoRepo = carritoRepo;
        this.productoRepo = productoRepo;
        this.cuentaRepo = cuentaRepo;
    }

    /***
     * Agrega un producto al carrito del usuario
     * @param productoCarritoDTO
     * @return
     * @throws Exception
     */
    @Override
    public String agregarItemCarrito(ProductoCarritoDTO productoCarritoDTO) throws Exception {
        // Validación de entrada si el dto llega con los datos necesarios
        if (productoCarritoDTO == null) {
            throw new IllegalArgumentException("El DTO del producto no puede ser nulo");
        }

        //Busca la cuenta del usuario mediante la id del usuario
        Cuenta cuenta = cuentaRepo.findById(productoCarritoDTO.idUsuario())
                .orElseThrow(() -> new Exception("No se encontró la cuenta con ID: " + productoCarritoDTO.idUsuario()));

        //Busca el carrito del usuario mediante la cedula de la cuenta registrada
        Carrito carrito = carritoRepo.buscarCarritoPorIdCliente(cuenta.getUsuario().getCedula())
                .orElseThrow(() -> new Exception("El carrito no existe para el cliente con ID: " + productoCarritoDTO.idUsuario()));

        //Busca el producto seleccionado en el carrito mediante la referencia del mismo
        Producto productoSeleccionado = productoRepo.buscarPorReferencia(productoCarritoDTO.id())
                .orElseThrow(() -> new Exception("Producto no encontrado con referencia: " + productoCarritoDTO.id()));

        //Se agrega la lista de productos a DetalleCarrito para llevar el control del mismo
        List<DetalleCarrito> lista = carrito.getItems();

        //Lista de los productos que están actualmente en el sistema
        List<Producto> productosSistema = productoRepo.findAll();

        /***
         * Ciclo para validar si un producto ya fue ingresado al carrito por medio de la referencia
         * - Si no se encuentra en el carrito se agrega
         * - Si se encuentra en el carrito se le informa al cliente que ya existe ese producto en el carrito
         */
        for (DetalleCarrito detalleCarrito : lista) {
            Optional<Producto> productoOpt = productoRepo.buscarPorReferencia(detalleCarrito.getIdProducto());
            Producto producto = productoOpt.get();
            if (producto.getReferencia().equals(productoCarritoDTO.id())) {
                throw new Exception("El producto seleccionado ya se encuentra en el carrito");
            }
        }

        //Se verifican que las cantidades ingresadas del producto no sean nulas
        if (productoCarritoDTO.unidades() <= 0) {
            throw new IllegalArgumentException("Las unidades deben ser mayores a cero");
        }

        /***
         * Se crea la clase DetalleCarrito para ingresar los datos de los productos seleccionados
         */
        DetalleCarrito detalleCarrito = DetalleCarrito.builder()
                .idDetalleCarrito(String.valueOf(new ObjectId()))
                .cantidad(productoCarritoDTO.unidades())
                .nombreProducto(productoCarritoDTO.nombreProducto())
                .idProducto(productoCarritoDTO.id())
                .precioUnitario(productoCarritoDTO.precio()) // Agregar precio para consistencia
                .build();

        //Se agregan los items al carrito
        carrito.getItems().add(detalleCarrito);
        //Se guarda el carrito en el repositorio
        carritoRepo.save(carrito);
        return "Item agregado al carrito correctamente";
    }

    /***
     * Metodo para eliminar el item de un carrito
     * @param idDetalle
     * @param idCliente
     * @return
     * @throws Exception
     */
    @Override
    public String eliminarItemCarrito(String idDetalle, String idCliente) throws Exception {

        //Se representa el objeto recibido al buscar la cuenta del cliente por medio del ID
        Optional<Cuenta> cuentaOptional = cuentaRepo.findById(idCliente);
        //Se carga la cuenta del cliente obtenida del Optional
        Cuenta cuenta = cuentaOptional.get();
        //Se busca el carrito del cliente por medio de la cédula
        Optional<Carrito> carritoCliente = carritoRepo.buscarCarritoPorIdCliente(cuenta.getUsuario().getCedula());
        //Verifica si el carrito del cliente esta vacío
        if (carritoCliente.isEmpty()) {
            throw new Exception("El carrito no existe");
        }
        //Se obtiene el carrito del cliente proveniente del Optional
        Carrito carrito = carritoCliente.get();
        //Se obtiene la lista de productos seleccionados anteriormente
        List<DetalleCarrito> lista = carrito.getItems();
        //Se obtiene una lista de todos los productos existentes
        List<Producto> productosSistema = productoRepo.findAll();
        //Se crea un arraylist para almacenar los productos del carrito
        List<Producto> productosCarrito = new ArrayList<>();
        //Obtener los productos que están en el carrito
        for (DetalleCarrito detalleCarrito : lista) {
            //Se busca el producto por referencia en la base de datos
            Optional<Producto> productoOpt = productoRepo.buscarPorReferencia(detalleCarrito.getIdProducto());
            //Se verifica que el producto se encuentre en la BD
            if (productoOpt.isPresent()) {
                //Se agrega el producto a la lista de productos
                productosCarrito.add(productoOpt.get());
            } else {
                //Exception en caso de que no se encuentre el evento
                throw new Exception("Producto no encontrado en el carrito");
            }
        }
        //Se elimina el ítem del carrito
        boolean removed = lista.removeIf(i -> i.getIdDetalleCarrito().equals(idDetalle));
        //Si no se puede remover al no existir
        if (!removed) {
            //Envia la Exception de no encontrar el producto en el carrito
            throw new Exception("El producto no se encontró en el carrito");
        }
        //Guardar los cambios en los productos y el carrito
        for (Producto producto : productosSistema) {
            //Guarda los cambios de los productos
            productoRepo.save(producto);
        }
        //Guarda los cambios de los carritos
        carritoRepo.save(carrito);
        //Mensaje de respuesta del metodo eliminarItemCarrito
        return "Producto eliminado del carrito";
    }

    /***
     * Metodo para eliminar el carrito de un cliente
     * @param eliminarCarritoDTO
     * @throws Exception
     */
    @Override
    public void eliminarCarrito(EliminarCarritoDTO eliminarCarritoDTO) throws Exception {
        //Se busca el carrito por el ID del carrito
        Optional<Carrito> carritoOptional = carritoRepo.buscarCarritoPorId(eliminarCarritoDTO.idCarrito());
        //Se verifica que el carrito haya sido obtenido
        if (carritoOptional.isEmpty()) {
            //Si no existe el carrito se manda la Exception
            throw new Exception("El carrito no existe");
        }
        //Se busca obtiene el valor encontrado para el carrito
        Carrito carrito = carritoOptional.get();
        //Se obtiene la lista de elementos del carrito
        List<DetalleCarrito> lista = carrito.getItems();
        //Iterar sobre los detalles del carrito
        for (DetalleCarrito detalleCarrito : lista) {
            //Se busca el producto por código
            Optional<Producto> productoOptional = productoRepo.buscarPorCodigo(detalleCarrito.getIdProducto());
            //Verifica si el producto fue encontrado
            if (productoOptional.isEmpty()) {
                //Exception al no encontrar el producto
                throw new Exception("El producto no existe");
            }
            //Se obtiene el valor obtenido del carrito
            Producto producto = productoOptional.get();
            //Guardar el producto con las características actualizadas
            productoRepo.save(producto);
        }
        //Finalmente, eliminar el carrito
        carritoRepo.delete(carrito);
        //Se crea un nuevo carrito para el cliente y se asignan sus atributos
        Carrito nuevoCarritoCLiente = Carrito.builder()
                .items(new ArrayList<>())
                .fecha(LocalDateTime.now())
                .idUsuario(carrito.getIdUsuario()).build();
        //Se guardan los cambios hechos el carrito
        carritoRepo.save(nuevoCarritoCLiente);
    }

    /***
     * Método para obtener la información del carrito
     * @param id_carrito
     * @return
     * @throws Exception
     */
    @Override
    public VistaCarritoDTO obtenerInformacionCarrito(String id_carrito) throws Exception {
        Optional<Carrito> carritoOptional = carritoRepo.buscarCarritoPorId(id_carrito);
        if (carritoOptional.isEmpty()) {
            throw new Exception("El carrito no existe");
        }

        Carrito carrito = carritoOptional.get();
        List<DetalleCarrito> detallesCarrito = carrito.getItems();
        LocalDateTime fecha = carrito.getFecha();

        return new VistaCarritoDTO(carrito.getId(), detallesCarrito, fecha);
    }

    @Override
    public List<CarritoListDTO> listarCarritos() {
        List<Carrito> carritos = carritoRepo.findAll();  // Obtener todos los carritos
        return carritos.stream().map(carrito -> {
            // Mapear cada Carrito a CarritoListDTO
            return new CarritoListDTO(new ObjectId(carrito.getId()), carrito.getFecha(), carrito.getItems());
        }).collect(Collectors.toList());
    }

    @Override
    public String obtenerIdCarrito(String id) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.findById(id);
        Cuenta cuenta = cuentaOptional.get();
        Optional<Carrito> carritoOptional = carritoRepo.buscarCarritoPorIdCliente(cuenta.getUsuario().getCedula());
        if (carritoOptional.isEmpty()) {
            throw new Exception("El carrito no existe");
        }
        Carrito carrito = carritoOptional.get();
        return carrito.getId();
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