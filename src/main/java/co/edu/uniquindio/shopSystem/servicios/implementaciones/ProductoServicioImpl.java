package co.edu.uniquindio.shopSystem.servicios.implementaciones;

import co.edu.uniquindio.shopSystem.dto.AbastecimientoDTOs.IdOrdenReabastecimientoDTO;
import co.edu.uniquindio.shopSystem.dto.AbastecimientoDTOs.OrdenAbastecimientoGlobalDTO;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.CrearProductoDTO;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.EditarProductoDTO;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.InformacionProductoDTO;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.ObtenerProductoDTO;
import co.edu.uniquindio.shopSystem.modelo.documentos.Inventario;
import co.edu.uniquindio.shopSystem.modelo.documentos.Producto;
import co.edu.uniquindio.shopSystem.modelo.documentos.Reabastecimiento;
import co.edu.uniquindio.shopSystem.modelo.enums.EstadoReabastecimiento;
import co.edu.uniquindio.shopSystem.modelo.vo.ProductoReabastecido;
import co.edu.uniquindio.shopSystem.repositorios.InventarioRepo;
import co.edu.uniquindio.shopSystem.repositorios.ProductoRepo;
import co.edu.uniquindio.shopSystem.repositorios.ReabastecimientoRepo;
import co.edu.uniquindio.shopSystem.servicios.interfaces.InventarioServicio;
import co.edu.uniquindio.shopSystem.servicios.interfaces.ProductoServicio;
import jakarta.security.auth.message.MessagePolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductoServicioImpl implements ProductoServicio {

    private final ProductoRepo productoRepo;
    private final ReabastecimientoRepo reabastecimientoRepo;
    private final InventarioRepo inventarioRepo;

    @Autowired
    public ProductoServicioImpl(ProductoRepo productoRepo, ReabastecimientoRepo reabastecimientoRepo, InventarioRepo inventarioRepo) {
        this.productoRepo = productoRepo;
        this.reabastecimientoRepo = reabastecimientoRepo;
        this.inventarioRepo = inventarioRepo;
    }

    /**
     * Crea un nuevo producto en el sistema
     * @param productoDTO DTO con los datos del producto a crear
     * @throws Exception Si ocurre un error durante la persistencia
     */
    @Override
    public void crearProducto(CrearProductoDTO productoDTO) throws Exception {
        Producto producto = new Producto();
        producto.setReferencia(productoDTO.referencia());
        producto.setNombre(productoDTO.nombre());
        producto.setTipoProducto(productoDTO.tipoProducto());
        producto.setUnidades(productoDTO.unidades());
        producto.setPrecio(productoDTO.precio());
        producto.setUrlImagen(productoDTO.imageUrl()); // <-- importante
        producto.setDescripcion(productoDTO.descripcion()); // <-- importante
        productoRepo.save(producto);
    }


    /**
     * Actualiza la información de un producto existente
     * @param productoDTO DTO con los nuevos datos del producto
     * @return Mensaje de confirmación de la operación
     * @throws Exception Si el producto no existe o falla la actualización
     */
    @Override
    public String editarProducto(EditarProductoDTO productoDTO) throws Exception {

        Producto producto = productoRepo.buscarPorReferencia(productoDTO.referencia())
                .orElseThrow(() -> new Exception("Producto no encontrado"));

        producto.setReferencia(productoDTO.referencia());
        producto.setNombre(productoDTO.nombre());
        producto.setTipoProducto(productoDTO.tipoProducto());
        producto.setUnidades(productoDTO.unidades());
        producto.setPrecio(productoDTO.precio());
        producto.setDescripcion(productoDTO.descripcion());
        producto.setUrlImagen(productoDTO.imageUrl());

        productoRepo.save(producto);
        return "Producto editado exitosamente";
    }

    /**
     * Elimina permanentemente un producto del sistema
     * @param id Identificador único del producto
     * @return Mensaje de confirmación de la eliminación
     * @throws Exception Si el producto no existe
     */
    @Override
    public String eliminarProducto(String id) throws Exception {
        if (!productoRepo.existsById(id)) {
            throw new Exception("Producto no encontrado");
        }

        productoRepo.deleteById(id);
        return "Producto eliminado exitosamente";
    }

    /**
     * Obtiene una lista de todos los productos registrados en el sistema
     * @return Lista de DTOs con información básica de los productos
     */
    @Override
    public List<ObtenerProductoDTO> listarProductos() {
        return productoRepo.findAll().stream().map(producto ->
                new ObtenerProductoDTO(
                        producto.getCodigo(),
                        producto.getReferencia(),
                        producto.getNombre(),
                        producto.getTipoProducto(),
                        producto.getUrlImagen(),
                        producto.getUnidades(),
                        producto.getPrecio(),
                        producto.getDescripcion()
                )
        ).collect(Collectors.toList());
    }

    /**
     * Obtiene información detallada de un producto específico
     * @param id Identificador único del producto
     * @return DTO con todos los datos del producto
     * @throws Exception Si el producto no existe
     */
    @Override
    public InformacionProductoDTO obtenerInformacionProducto(String id) throws Exception {
        Optional<Producto> producto = productoRepo.buscarPorReferencia(id);
        if (producto.isEmpty()) {
            throw new Exception("El producto no existe.");
        }

        Producto producto_base = producto.get();

        return new InformacionProductoDTO(
                producto_base.getReferencia(),
                producto_base.getNombre(),
                producto_base.getTipoProducto(),
                producto_base.getUrlImagen(),
                producto_base.getUnidades(),
                producto_base.getPrecio(),
                producto_base.getDescripcion()
        );
    }

    /**
     * Obtiene la información completa de un producto (versión con logs de depuración)
     * @param id Identificador único del producto
     * @return DTO con todos los datos del producto
     * @throws Exception Si el producto no existe
     */
    @Override
    public InformacionProductoDTO obtenerProducto(String id) throws Exception {
        Optional<Producto> producto = productoRepo.buscarPorReferencia(id);
        System.out.println("Producto buscado: " + producto.get().getNombre());
        if (producto.isEmpty()) {
            throw new Exception("El producto no existe.");
        }
        System.out.println("Producto: " + producto.get().getNombre());
        return new InformacionProductoDTO(
                producto.get().getReferencia(),
                producto.get().getNombre(),
                producto.get().getTipoProducto(),
                producto.get().getUrlImagen(), // <-- Este se mapea como imageUrl
                producto.get().getUnidades(),
                producto.get().getPrecio(),
                producto.get().getDescripcion()
        );
    }

    @Override
    public void crearOrdenAbastecimiento(OrdenAbastecimientoGlobalDTO orden) {

        List<ProductoReabastecido> productos = orden.productos().stream()
                .map(dto -> ProductoReabastecido.builder()
                        .referenciaProducto(dto.referenciaProducto())
                        .nombreProducto(dto.nombreProducto())
                        .cantidad(dto.cantidadAbastecer())
                        .build())
                .toList();

        Reabastecimiento reabastecimiento = Reabastecimiento.builder()
                .fechaCreacion(LocalDateTime.now())
                .productos(productos)
                .estado(EstadoReabastecimiento.PENDIENTE)// si viene del DTO
                .build();

        reabastecimientoRepo.save(reabastecimiento);
    }

    @Override
    public void aplicarOrdenAbastecimiento(String idOrden) throws Exception{

        ArrayList<Inventario> productosAlteradosInventarios = new ArrayList<>();
        ArrayList<Producto> productosAlteradosTienda = new ArrayList<>();

        Reabastecimiento reabastecimiento = reabastecimientoRepo.findById(idOrden)
                .orElseThrow(() -> new Exception("La orden de reabastecimiento no existe."));

        if (reabastecimiento.getEstado() == EstadoReabastecimiento.ENTREGADO){
            throw new Exception("La orden de reabastecimiento ya fue entregada.");
        }

        if (reabastecimiento.getEstado() == EstadoReabastecimiento.CANCELADO){
            throw new Exception("La orden de reabastecimiento ya fue cancelada.");
        }

        for (ProductoReabastecido productoOrdenado : reabastecimiento.getProductos()) {

            Inventario productoInventario = inventarioRepo.buscarPorReferencia(productoOrdenado.getReferenciaProducto())
                    .orElseThrow(() -> new Exception("El producto con referencia " + productoOrdenado.getReferenciaProducto() + " no existe en la bodega."));

            Optional<Producto> productoTiendaOptional = productoRepo.buscarPorReferencia(productoOrdenado.getReferenciaProducto());

            Producto productoTienda = new Producto();
            if (productoTiendaOptional.isEmpty()) {
                Producto crearProducto = new Producto();
                crearProducto.setNombre(productoOrdenado.getNombreProducto());
                crearProducto.setReferencia(productoOrdenado.getReferenciaProducto());
                crearProducto.setDescripcion(productoInventario.getDescripcion());
                crearProducto.setTipoProducto(productoInventario.getTipoProducto());
                crearProducto.setUrlImagen(productoInventario.getUrlImagen());
                crearProducto.setUnidades(productoInventario.getUnidades());
                crearProducto.setPrecio(productoInventario.getPrecio());
                productosAlteradosTienda.add(crearProducto);
            } else {
                productoTienda = productoTiendaOptional.get();
            }

            if (productoInventario.getUnidades() < productoOrdenado.getCantidad()) {
                throw new Exception("La cantidad solicitada del producto " + productoInventario.getNombre() + " no se encuentra disponible.");
            }

            productoInventario.setUnidades(productoInventario.getUnidades() - productoOrdenado.getCantidad());
            productoTienda.setUnidades(productoTienda.getUnidades() + productoOrdenado.getCantidad());

            productosAlteradosInventarios.add(productoInventario);
            productosAlteradosTienda.add(productoTienda);
        }

        System.out.println(productosAlteradosInventarios);

        for (Producto producto : productosAlteradosTienda){
            System.out.println("Productos que se van a cargar a la tienda: " );
            System.out.println(producto.getNombre() + " " + producto.getUnidades() + " " + producto.getPrecio());
        }

        productoRepo.saveAll(productosAlteradosTienda);
        inventarioRepo.saveAll(productosAlteradosInventarios);

        reabastecimiento.setEstado(EstadoReabastecimiento.ENTREGADO);
        reabastecimientoRepo.save(reabastecimiento);
    }


}
