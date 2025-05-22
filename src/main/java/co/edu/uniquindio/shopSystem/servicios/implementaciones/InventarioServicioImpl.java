package co.edu.uniquindio.shopSystem.servicios.implementaciones;

import co.edu.uniquindio.shopSystem.dto.AbastecimientoDTOs.OrdenAbastecimientoGlobalDTO;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.CrearProductoDTO;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.EditarProductoDTO;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.InformacionProductoDTO;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.ObtenerProductoDTO;
import co.edu.uniquindio.shopSystem.modelo.documentos.Inventario;
import co.edu.uniquindio.shopSystem.modelo.documentos.Producto;
import co.edu.uniquindio.shopSystem.repositorios.InventarioRepo;
import co.edu.uniquindio.shopSystem.servicios.interfaces.InventarioServicio;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InventarioServicioImpl implements InventarioServicio {

    private final InventarioRepo inventarioRepo;

    public InventarioServicioImpl(InventarioRepo inventarioRepo) {
        this.inventarioRepo = inventarioRepo;
    }

    @Override
    public void crearProducto(CrearProductoDTO productoDTO) throws Exception {
        Inventario producto = new Inventario();
        producto.setReferencia(productoDTO.referencia());
        producto.setNombre(productoDTO.nombre());
        producto.setDescripcion(productoDTO.descripcion());
        producto.setTipoProducto(productoDTO.tipoProducto());
        producto.setUnidades(productoDTO.unidades());
        producto.setPrecio(productoDTO.precio());
        producto.setUrlImagen(productoDTO.imageUrl());
        inventarioRepo.save(producto);
    }

    @Override
    public String editarProducto(EditarProductoDTO productoDTO) throws Exception {

        Inventario producto = inventarioRepo.buscarPorReferencia(productoDTO.referencia())
                .orElseThrow(() -> new Exception("Producto no encontrado"));

        producto.setReferencia(productoDTO.referencia());
        producto.setNombre(productoDTO.nombre());
        producto.setTipoProducto(productoDTO.tipoProducto());
        producto.setUnidades(productoDTO.unidades());
        producto.setPrecio(productoDTO.precio());
        producto.setDescripcion(productoDTO.descripcion());
        producto.setUrlImagen(productoDTO.imageUrl());

        inventarioRepo.save(producto);
        return "Producto editado exitosamente";
    }

    @Override
    public String eliminarProducto(String id) throws Exception {
        if (!inventarioRepo.existsById(id)) {
            throw new Exception("Producto no encontrado");
        }

        inventarioRepo.deleteById(id);
        return "Producto eliminado exitosamente";
    }

    @Override
    public List<ObtenerProductoDTO> listarProductos() {
        return inventarioRepo.findAll().stream().map(producto ->
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
    public InformacionProductoDTO obtenerInformacionProducto(String id) throws Exception {
        Optional<Inventario> producto = inventarioRepo.buscarPorReferencia(id);
        if (producto.isEmpty()) {
            throw new Exception("El producto no existe.");
        }

        Inventario producto_base = producto.get();

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
    public InformacionProductoDTO obtenerProducto(String id) throws Exception {
        Optional<Inventario> producto = inventarioRepo.buscarPorReferencia(id);
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
