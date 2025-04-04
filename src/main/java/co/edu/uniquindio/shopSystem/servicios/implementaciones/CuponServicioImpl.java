package co.edu.uniquindio.shopSystem.servicios.implementaciones;

import co.edu.uniquindio.shopSystem.dto.CuponDTOs.*;
import co.edu.uniquindio.shopSystem.modelo.documentos.Cupon;
import co.edu.uniquindio.shopSystem.modelo.enums.EstadoCupon;
import co.edu.uniquindio.shopSystem.modelo.enums.EstadoOrden;
import co.edu.uniquindio.shopSystem.modelo.enums.TipoCupon;
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

    @Override
    public InformacionCuponDTO obtenerInformacionCupon(String id) throws Exception {
        Cupon cupon = obtenerCupon(id);
        return new InformacionCuponDTO(
                cupon.getId(),
                cupon.getNombre(),
                cupon.getCodigo(),
                cupon.getDescuento(),
                cupon.getFechaVencimiento(),
                cupon.getTipo(),
                cupon.getEstado()
        );
    }

    private Cupon obtenerCupon(String id) throws Exception {
        Optional<Cupon> cuponOptional = cuponRepo.buscarPorCodigo(id);

        if (cuponOptional.isEmpty()) {
            throw new Exception("El cupón con el id: " + id + " no existe");
        }

        return cuponOptional.get();
    }

    @Override
    public AplicarCuponDTO aplicarCupon(String codigo) throws Exception {
        Optional<Cupon> cuponOptional = cuponRepo.buscarPorCodigo(codigo);

        Cupon cupon = cuponOptional.get();
        if (cupon.getEstado() != EstadoCupon.DISPONIBLE) {
            throw new Exception("El cupón no está activo");
        }

        if (cupon.getFechaVencimiento().isBefore(LocalDateTime.now())) {
            throw new Exception("El cupón ha vencido");
        }

        if (cupon.getTipo() == TipoCupon.MULTIPLE) {
            if(cupon.getUsos() > 3){
                throw new Exception("El cupón ya completo el numero de usos");
            }
        }
        return new AplicarCuponDTO(cupon.getDescuento());
    }

    @Override
    public void registrarUso(String idCupon) throws Exception {
        Optional<Cupon> cuponOptional = cuponRepo.buscarPorCodigo(idCupon);

        if (cuponOptional.isEmpty()) {
            throw new Exception("El cupón no existe o el código es incorrecto");
        }

        Cupon cupon = cuponOptional.get();

        if (cupon.getTipo() == TipoCupon.UNICO) {
            cupon.setEstado(EstadoCupon.NO_DISPONIBLE);
        }
        cupon.setUsos(cupon.getUsos() + 1);
        cuponRepo.save(cupon);
    }
}
