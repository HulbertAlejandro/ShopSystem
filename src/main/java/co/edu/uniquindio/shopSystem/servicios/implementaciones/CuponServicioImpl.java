package co.edu.uniquindio.shopSystem.servicios.implementaciones;

import co.edu.uniquindio.shopSystem.dto.CuponDTOs.CrearCuponDTO;
import co.edu.uniquindio.shopSystem.dto.CuponDTOs.EditarCuponDTO;
import co.edu.uniquindio.shopSystem.dto.CuponDTOs.ObtenerCuponDTO;
import co.edu.uniquindio.shopSystem.modelo.documentos.Cupon;
import co.edu.uniquindio.shopSystem.repositorios.CuponRepo;
import co.edu.uniquindio.shopSystem.servicios.interfaces.CuponServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CuponServicioImpl implements CuponServicio {

    private final CuponRepo cuponRepo;

    @Autowired
    public CuponServicioImpl(CuponRepo cuponRepo) {
        this.cuponRepo = cuponRepo;
    }

    @Override
    public void crearCupon(CrearCuponDTO cuponDTO) throws Exception {
        Cupon cupon = new Cupon();
        cupon.setCodigo(cuponDTO.codigo());
        cupon.setDescuento(cuponDTO.descuento());
        cupon.setNombre(cuponDTO.nombre());
        cupon.setTipo(cuponDTO.tipo());
        cupon.setEstado(cuponDTO.estado());
        cupon.setFechaVencimiento(cuponDTO.fechaVencimiento());

        cuponRepo.save(cupon);
    }

    @Override
    public String editarCupon(EditarCuponDTO cuponDTO) throws Exception {
        Cupon cupon = cuponRepo.findById(cuponDTO.id())
                .orElseThrow(() -> new Exception("Cupón no encontrado"));

        cupon.setCodigo(cuponDTO.codigo());
        cupon.setDescuento(cuponDTO.descuento());
        cupon.setNombre(cuponDTO.nombre());
        cupon.setTipo(cuponDTO.tipo());
        cupon.setEstado(cuponDTO.estado());
        cupon.setFechaVencimiento(cuponDTO.fechaVencimiento());

        cuponRepo.save(cupon);
        return "Cupón editado exitosamente";
    }

    @Override
    public String eliminarCupon(String id) throws Exception {
        if (!cuponRepo.existsById(id)) {
            throw new Exception("Cupón no encontrado");
        }

        cuponRepo.deleteById(id);
        return "Cupón eliminado exitosamente";
    }

    @Override
    public List<ObtenerCuponDTO> listarCupones() {
        return cuponRepo.findAll().stream().map(cupon ->
                new ObtenerCuponDTO(
                        cupon.getId(),
                        cupon.getCodigo(),
                        cupon.getDescuento(),
                        cupon.getNombre(),
                        cupon.getTipo(),
                        cupon.getEstado(),
                        cupon.getFechaVencimiento()
                )
        ).collect(Collectors.toList());
    }
}
