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

    @Override
    public String editarProducto(EditarProductoDTO productoDTO) throws Exception {
        Producto producto = productoRepo.buscarPorCodigo(productoDTO.codigo())
                .orElseThrow(() -> new Exception("Producto no encontrado"));

        producto.setReferencia(productoDTO.referencia());
        producto.setNombre(productoDTO.nombre());
        producto.setTipoProducto(productoDTO.tipoProducto());
        producto.setUnidades(productoDTO.unidades());
        producto.setPrecio(productoDTO.precio());

        productoRepo.save(producto);
        return "Producto editado exitosamente";
    }

    @Override
    public String eliminarProducto(String id) throws Exception {
        if (!productoRepo.existsById(id)) {
            throw new Exception("Producto no encontrado");
        }

        productoRepo.deleteById(id);
        return "Producto eliminado exitosamente";
    }

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

    @Override
    public InformacionProductoDTO obtenerInformacionProducto(String id) throws Exception{
        Optional<Producto> producto = productoRepo.buscarPorReferencia(id);
        Producto producto_base = producto.get();
        if (producto.isEmpty()) {
            throw new Exception("El producto no está existe.");
        }

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

    @Override
    public Producto obtenerProducto(String id) throws Exception {
        Optional<Producto> producto = productoRepo.buscarPorReferencia(id);
        System.out.println("Producto buscado: " + producto.get().getNombre());
        if (producto.isEmpty()) {
            throw new Exception("El producto no existe.");
        }
        return producto.get();
    }


}
