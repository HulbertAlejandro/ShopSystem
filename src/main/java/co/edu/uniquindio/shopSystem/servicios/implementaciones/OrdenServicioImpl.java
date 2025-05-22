package co.edu.uniquindio.shopSystem.servicios.implementaciones;

import co.edu.uniquindio.shopSystem.dto.OrdenDTO.CrearOrdenDTO;
import co.edu.uniquindio.shopSystem.dto.OrdenDTO.InformacionOrdenDTO;
import co.edu.uniquindio.shopSystem.dto.OrdenDTO.ItemsDTO;
import co.edu.uniquindio.shopSystem.modelo.documentos.Orden;
import co.edu.uniquindio.shopSystem.modelo.documentos.Producto;
import co.edu.uniquindio.shopSystem.modelo.enums.EstadoOrden;
import co.edu.uniquindio.shopSystem.modelo.vo.DetalleCarrito;
import co.edu.uniquindio.shopSystem.modelo.vo.DetalleOrden;
import co.edu.uniquindio.shopSystem.modelo.vo.Pago;
import co.edu.uniquindio.shopSystem.repositorios.CuentaRepo;
import co.edu.uniquindio.shopSystem.repositorios.CuponRepo;
import co.edu.uniquindio.shopSystem.repositorios.OrdenRepo;
import co.edu.uniquindio.shopSystem.repositorios.ProductoRepo;
import co.edu.uniquindio.shopSystem.servicios.interfaces.CarritoServicio;
import co.edu.uniquindio.shopSystem.servicios.interfaces.CuponServicio;
import co.edu.uniquindio.shopSystem.servicios.interfaces.OrdenServicio;
import co.edu.uniquindio.shopSystem.servicios.interfaces.ProductoServicio;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import jakarta.validation.constraints.NotNull;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.ObjectError;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrdenServicioImpl implements OrdenServicio {

    private final CarritoServicio carritoServicio;
    private final CuponServicio cuponServicio;

    /**
     * Procesa notificaciones de MercadoPago para actualizar el estado de las órdenes
     * @param request Mapa con los datos de la notificación
     * @throws Exception Si ocurre error al procesar la notificación
     */
    @Override
    public void recibirNotificacionMercadoPago(Map<String, Object> request) throws Exception {
        try {

            // Obtener el tipo de notificación (ejemplo: "payment")
            String tipo = (String) request.get("type");

            // Si la notificación es de un pago, obtener el ID del pago
            if ("payment".equals(tipo)) {
                Map<String, Object> data = (Map<String, Object>) request.get("data");
                String idPago = data.get("id").toString();

                // Obtener la información del pago desde MercadoPago
                PaymentClient client = new PaymentClient();
                Payment payment = client.get(Long.parseLong(idPago));

                // Obtener el id de la orden desde los metadatos del pago
                String idOrden = payment.getMetadata().get("id_orden").toString();

                // Buscar la orden en la base de datos
                Optional<Orden> ordenOptional = ordenRepo.buscarOrdenPorId(idOrden);
                Orden orden = ordenOptional.get();

                // Crear el objeto Pago y asignarlo a la orden
                Pago pago = crearPago(payment);
                orden.setPago(pago);

                actualizarCantidadesInventario(orden);

                // Actualizar el estado de la orden según el estado del pago
                if ("approved".equals(payment.getStatus())) {
                    orden.setEstado(EstadoOrden.PAGADA);
                } else if ("rejected".equals(payment.getStatus())) {
                    orden.setEstado(EstadoOrden.CANCELADA);
                }

                // Guardar la orden actualizada
                ordenRepo.save(orden);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error procesando la notificación de MercadoPago: " + e.getMessage());
        }
    }

    /**
     * Actualiza las cantidades en inventario de los productos después de una compra
     * @param orden Orden completada con los productos vendidos
     */
    private void actualizarCantidadesInventario(Orden orden) {
        for (DetalleOrden productoOrden : orden.getDetallesOrden()){
            Optional<Producto> producto = productoRepo.buscarPorReferencia(productoOrden.getIdProducto());
            Producto p = producto.get();

            p.setUnidades(p.getUnidades() - productoOrden.getCantidad());
            productoRepo.save(p);
        }
        System.out.println("Se actualizaron las unidades existentes de productos");
    }

    /**
     * Crea un objeto Pago a partir de los datos de MercadoPago
     * @param payment Objeto de pago de MercadoPago
     * @return Entidad Pago con los datos mapeados
     */
    private Pago crearPago(Payment payment) {
        Pago pago = new Pago();
        pago.setIdPago(payment.getId().toString());
        pago.setFecha(payment.getDateCreated().toLocalDateTime());
        pago.setEstado(payment.getStatus());
        pago.setDetalleEstado(payment.getStatusDetail());
        pago.setTipoPago(payment.getPaymentTypeId());
        pago.setMoneda(payment.getCurrencyId());
        pago.setCodigoAutorizacion(payment.getAuthorizationCode());
        return pago;
    }

    private final OrdenRepo ordenRepo;
    private final CuponRepo cuponRepo;
    private final CuentaRepo cuentaRepo;
    private final ProductoRepo productoRepo;
    private final ProductoServicio productoServicio;

    public OrdenServicioImpl(OrdenRepo ordenRepo, CuponRepo cuponRepo, CuentaRepo cuentaRepo, ProductoRepo productoRepo, ProductoServicio productoServicio, CarritoServicio carritoServicio, CuponServicio cuponServicio) {
        this.ordenRepo = ordenRepo;
        this.cuponRepo = cuponRepo;
        this.cuentaRepo = cuentaRepo;
        this.productoRepo = productoRepo;
        this.productoServicio = productoServicio;
        this.carritoServicio = carritoServicio;
        this.cuponServicio = cuponServicio;
    }

    /**
     * Crea una nueva orden de compra con validación de concurrencia
     * @param crearOrdenDTO DTO con los datos de la orden
     * @return ID de la orden creada
     * @throws Exception Si ya existe una orden para el cliente o fallan validaciones
     */
    @Override
    public String crearOrden(CrearOrdenDTO crearOrdenDTO) throws Exception {
        synchronized (this) { // Bloquea para evitar condiciones de carrera en concurrencia
            if (existeOrden(crearOrdenDTO.idCliente())) {
                throw new Exception("Ya existe una orden con este código");
            }

            List<DetalleOrden> detallesOrden = convertirDetalleOrden(crearOrdenDTO.items());

            Orden nuevaOrden = new Orden();
            nuevaOrden.setIdCliente(new ObjectId(crearOrdenDTO.idCliente()));
            nuevaOrden.setFecha(LocalDateTime.now());
            nuevaOrden.setCodigoPasarela(crearOrdenDTO.codigoPasarela());
            nuevaOrden.setDetallesOrden(detallesOrden);
            nuevaOrden.setPago(new Pago());
            nuevaOrden.setTotal(crearOrdenDTO.total());
            nuevaOrden.setIdCupon(crearOrdenDTO.codigoCupon());
            nuevaOrden.setEstado(EstadoOrden.DISPONIBLE);
            nuevaOrden.setDescuento(crearOrdenDTO.descuento());
            nuevaOrden.setImpuesto(crearOrdenDTO.impuesto());

            // ✅ Guardar la orden y recuperar el ID generado por MongoDB
            Orden ordenGuardada = ordenRepo.save(nuevaOrden);

            // ✅ Vaciar carrito y registrar cupón
            carritoServicio.vaciarCarrito(crearOrdenDTO.idCliente());
            if (!crearOrdenDTO.codigoCupon().isEmpty()) {
                cuponServicio.registrarUso(crearOrdenDTO.codigoCupon());
            }
            // ✅ Retornar el ID de la orden creada
            return ordenGuardada.getId();
        }
    }

    /**
     * Verifica la existencia de una orden por ID
     * @param id Identificador de la orden
     * @return true si la orden existe
     */
    private boolean existeOrden(String id) {
        return ordenRepo.buscarOrdenPorId(id).isPresent();
    }

    /**
     * Convierte items DTO a entidades DetalleOrden
     * @param itemDTO Lista de items del carrito
     * @return Lista de detalles de orden convertidos
     */
    public List<DetalleOrden> convertirDetalleOrden(@NotNull(message = "Debe proporcionar al menos un ítem en la orden") List<ItemsDTO> itemDTO) {
        List<DetalleOrden> detalleOrdenList = new ArrayList<>();
        for (ItemsDTO item : itemDTO) {
            DetalleOrden detalleOrden = new DetalleOrden();
            detalleOrden.setIdDetalleOrden(new ObjectId().toString()); // Generar un ID único si no se proporciona
            detalleOrden.setIdProducto(item.referencia());
            detalleOrden.setPrecio(item.precio());
            detalleOrden.setNombreProducto(item.nombre());
            detalleOrden.setCantidad(item.cantidad());

            detalleOrdenList.add(detalleOrden);
        }
        return detalleOrdenList;
    }

    /**
     * Convierte items de orden a entidades DetalleCarrito
     * @param items Lista de items de la orden
     * @return Lista de detalles de carrito convertidos
     */
    private List<DetalleCarrito> convertirDetalleOrdenACarrito(@NotNull(message = "Debe proporcionar al menos un ítem en la orden") List<ItemsDTO> items) {
        List<DetalleCarrito> detallesCarrito = new ArrayList<>();
        for (ItemsDTO item : items) {
            DetalleCarrito detalleCarrito = new DetalleCarrito();
            detalleCarrito.setIdDetalleCarrito(new ObjectId().toString()); // Generar un ID único
            detalleCarrito.setCantidad(item.cantidad());
            detalleCarrito.setNombreProducto(item.nombre());
            detalleCarrito.setPrecioUnitario(item.precio());
            detalleCarrito.setIdProducto(item.referencia());

            detallesCarrito.add(detalleCarrito);
        }
        return detallesCarrito;
    }

    /**
     * Genera una preferencia de pago en MercadoPago
     * @param idOrden Identificador de la orden
     * @return Objeto Preference de MercadoPago
     * @throws Exception Si la orden está cancelada, ya pagada o hay discrepancias en totales
     */
    @Override
    public Preference realizarPago(String idOrden) throws Exception {

        Orden ordenGuardada = obtenerOrden(idOrden);
        List<PreferenceItemRequest> itemsPasarela = new ArrayList<>();

        if (ordenGuardada.getEstado() == EstadoOrden.CANCELADA) {
            throw new Exception("LA ORDEN SELECCIONADA FUE CANCELADA");
        }

        if (ordenGuardada.getEstado() == EstadoOrden.PAGADA) {
            throw new Exception("LA ORDEN SELECCIONADA YA FUE PAGADA");
        }

        BigDecimal totalCalculado = BigDecimal.ZERO;

        for (DetalleOrden item : ordenGuardada.getDetallesOrden()) {
            BigDecimal precioUnitario = BigDecimal.valueOf(item.getPrecio()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(item.getCantidad()));
            totalCalculado = totalCalculado.add(subtotal);

            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .id(item.getIdProducto())
                    .title(item.getNombreProducto())
                    .quantity(item.getCantidad())
                    .currencyId("COP")
                    .unitPrice(precioUnitario) // Precio unitario
                    .build();

            itemsPasarela.add(itemRequest);
        }

        // Comparar el total calculado con el total de la orden
        BigDecimal totalOrden = BigDecimal.valueOf(ordenGuardada.getTotal()).setScale(2, RoundingMode.HALF_UP);

        if (totalCalculado.compareTo(totalOrden) != 0) {
            // Ajuste para asegurar que el total de MercadoPago sea igual al de la orden
            BigDecimal ajuste = totalOrden.subtract(totalCalculado);
            itemsPasarela.add(
                    PreferenceItemRequest.builder()
                            .id("ajuste")
                            .title("Ajuste de precio")
                            .quantity(1)
                            .currencyId("COP")
                            .unitPrice(ajuste)
                            .build()
            );
        }

        MercadoPagoConfig.setAccessToken("APP_USR-6804257661611202-040220-21c5efb630df7224dc0e46b3e957cf3a-2368856858");

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success("https://shop-system-frontend-822c9.web.app")
                .failure("https://shop-system-frontend-822c9.web.app")
                .pending("https://shop-system-frontend-822c9.web.app")
                .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .backUrls(backUrls)
                .items(itemsPasarela)
                .metadata(Map.of("id_orden", ordenGuardada.getId()))
                .notificationUrl("https://shop-system-frontend-822c9.web.app/api/auth/mercadopago/notificacion")
                .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        ordenGuardada.setCodigoPasarela(preference.getId());
        ordenRepo.save(ordenGuardada);

        return preference;
    }

    /**
     * Obtiene una orden por su ID
     * @param idOrden Identificador de la orden
     * @return Entidad Orden encontrada
     * @throws Exception Si no se encuentra la orden
     */
    public Orden obtenerOrden(String idOrden) throws Exception {
        System.out.println("🔍 Buscando orden con ID: " + idOrden);

        Optional<Orden> ordenOptional = ordenRepo.buscarOrdenPorObjectId(new ObjectId(idOrden));

        if (!ordenOptional.isPresent()) {
            System.out.println(" No se encontró ninguna orden con ese ID en la base de datos.");
            throw new Exception("La orden con el id: " + idOrden + " no existe");
        }

        System.out.println("✅ Orden encontrada: " + ordenOptional.get());
        return ordenOptional.get();
    }

    /**
     * Lista todas las órdenes de un cliente
     * @param idCliente Identificador del cliente
     * @return Lista de DTOs con información de las órdenes
     * @throws Exception Si no existen órdenes para el cliente
     */
    @Override
    public List<InformacionOrdenDTO> ordenesUsuario(ObjectId idCliente) throws Exception {
        List<Orden> ordenes = ordenRepo.findByIdCliente(idCliente);
        if (ordenes.isEmpty()) {
            throw new Exception("No existen órdenes para el usuario.");
        }

        return ordenes.stream().map(orden -> new InformacionOrdenDTO(
                orden.getId(),
                idCliente.toString(),
                orden.getCodigoPasarela(),
                convertirListaADTO(orden.getDetallesOrden()), // Aquí pasamos los detalles correctamente
                orden.getTotal(),
                orden.getDescuento(),
                orden.getImpuesto(),
                orden.getEstado(),
                orden.getIdCupon()

        )).collect(Collectors.toList());
    }

    /**
     * Obtiene los detalles completos de una orden específica
     * @param idOrden Identificador de la orden
     * @return DTO con toda la información de la orden
     * @throws Exception Si no se encuentra la orden
     */
    @Override
    public InformacionOrdenDTO obtenerOrdenCliente(String idOrden) throws Exception {
        Optional<Orden> ordenOptional = ordenRepo.buscarOrdenPorId(idOrden);
        if (!ordenOptional.isPresent()) { // Verificamos antes de llamar get()
            throw new Exception("La orden con el id: " + idOrden + " no existe");
        }

        Orden orden = ordenOptional.get();

        return new InformacionOrdenDTO(
                orden.getId(),
                orden.getIdCliente().toString(),
                orden.getCodigoPasarela(),
                convertirListaADTO(orden.getDetallesOrden()),
                orden.getTotal(),
                orden.getDescuento(),
                orden.getImpuesto(),
                orden.getEstado(),
                orden.getIdCupon()
        );
    }

    /**
     * Convierte un detalle de orden a DTO
     * @param detalleOrden Entidad DetalleOrden a convertir
     * @return DTO con los datos mapeados
     */
    public ItemsDTO convertirADTO(DetalleOrden detalleOrden) {
        return new ItemsDTO(
                detalleOrden.getIdProducto(),
                detalleOrden.getNombreProducto(),
                detalleOrden.getCantidad(),
                detalleOrden.getPrecio(),
                detalleOrden.getIdDetalleOrden()
        );
    }

    /**
     * Convierte una lista de detalles de orden a DTOs
     * @param detallesOrden Lista de entidades DetalleOrden
     * @return Lista de DTOs convertidos
     */
    public List<ItemsDTO> convertirListaADTO(List<DetalleOrden> detallesOrden) {
        return detallesOrden.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

}
