package co.edu.uniquindio.shopSystem.servicios.implementaciones;

import co.edu.uniquindio.shopSystem.dto.AbastecimientoDTOs.MostrarOrdenReabastecimientoDTO;
import co.edu.uniquindio.shopSystem.dto.AbastecimientoDTOs.ProductoReabastecerDTO;
import co.edu.uniquindio.shopSystem.dto.ProductoDTOs.ObtenerProductoDTO;
import co.edu.uniquindio.shopSystem.modelo.enums.EstadoReabastecimiento;
import co.edu.uniquindio.shopSystem.repositorios.ReabastecimientoRepo;
import co.edu.uniquindio.shopSystem.servicios.interfaces.ReabastecimientoServicio;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReabastecimientoServicioImpl implements ReabastecimientoServicio {

    private final ReabastecimientoRepo reabastecimientoRepo;

    public ReabastecimientoServicioImpl(ReabastecimientoRepo reabastecimientoRepo) {
        this.reabastecimientoRepo = reabastecimientoRepo;
    }

    @Override
    public List<MostrarOrdenReabastecimientoDTO> listarOrdenesReabastecimiento() throws Exception {
        return reabastecimientoRepo.findAll().stream().map(reabastecimiento -> {
            List<ProductoReabastecerDTO> productos = reabastecimiento.getProductos().stream().map(p ->
                    new ProductoReabastecerDTO(p.getReferenciaProducto(), p.getCantidad(), p.getNombreProducto())
            ).toList();

            return new MostrarOrdenReabastecimientoDTO(
                    reabastecimiento.getId(),
                    reabastecimiento.getFechaCreacion(),
                    productos,
                    reabastecimiento.getEstado()

            );
        }).toList();
    }

}
