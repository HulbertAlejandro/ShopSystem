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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
    public String crearCupon(CrearCuponDTO cuponDTO) throws Exception {
        Optional<Cupon> cuponOptional = cuponRepo.buscarPorCodigo(cuponDTO.codigo());
        if (cuponOptional.isPresent()) {
            throw new Exception("Ya existe un cupón con este código");
        }

        // Validación manual de longitud del código
        if (cuponDTO.codigo().length() > 10) {
            throw new Exception("El código del cupón no debe exceder los 10 caracteres");
        }

        // Validación manual de longitud del nombre
        if (cuponDTO.nombre().length() > 50) {
            throw new Exception("El nombre del cupón no debe exceder los 50 caracteres");
        }

        // Validación del descuento
        if (cuponDTO.descuento() <= 0 || cuponDTO.descuento() > 100) {
            throw new Exception("El descuento debe estar entre 1 y 100%");
        }

        // Validación de fechas
        if (cuponDTO.fechaInicio().isAfter(cuponDTO.fechaVencimiento())) {
            throw new Exception("La fecha de inicio no puede ser posterior a la fecha de vencimiento");
        }

        if (cuponDTO.fechaVencimiento().isBefore(LocalDateTime.now())) {
            throw new Exception("La fecha de vencimiento debe estar en el futuro");
        }

        Cupon cupon = new Cupon();
        cupon.setCodigo(cuponDTO.codigo());
        cupon.setDescuento(cuponDTO.descuento());
        cupon.setNombre(cuponDTO.nombre());
        cupon.setTipo(cuponDTO.tipo());
        cupon.setEstado(cuponDTO.estado());
        cupon.setFechaVencimiento(cuponDTO.fechaVencimiento());
        cupon.setUsos(0);
        Cupon cuponCreado = cuponRepo.save(cupon);
        return cuponCreado.getId();
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
