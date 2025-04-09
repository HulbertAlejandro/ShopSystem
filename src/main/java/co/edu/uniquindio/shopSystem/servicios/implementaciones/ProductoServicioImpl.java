package co.edu.uniquindio.shopSystem.servicios.implementaciones;

import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.CrearProductoDTO;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.EditarProductoDTO;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.InformacionProductoDTO;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.ObtenerProductoDTO;
import co.edu.uniquindio.shopSystem.modelo.documentos.Producto;
import co.edu.uniquindio.shopSystem.repositorios.ProductoRepo;
import co.edu.uniquindio.shopSystem.servicios.interfaces.ProductoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductoServicioImpl implements ProductoServicio {

    private final ProductoRepo productoRepo;

    @Autowired
    public ProductoServicioImpl(ProductoRepo productoRepo) {
        this.productoRepo = productoRepo;
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
}
