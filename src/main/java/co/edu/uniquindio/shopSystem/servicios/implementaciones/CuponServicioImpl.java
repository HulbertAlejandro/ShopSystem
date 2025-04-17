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

    /**
     * Crea un nuevo cupón en el sistema con validaciones de negocio
     * @param cuponDTO DTO con los datos del cupón a crear
     * @return ID del cupón creado
     * @throws Exception Si el código ya existe, validaciones fallidas o errores de persistencia
     */
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

    /**
     * Actualiza la información de un cupón existente
     * @param cuponDTO DTO con los nuevos datos del cupón
     * @return Mensaje de confirmación
     * @throws Exception Si el cupón no existe o hay errores de validación
     */
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

    /**
     * Elimina permanentemente un cupón del sistema
     * @param id Identificador único del cupón
     * @return Mensaje de confirmación
     * @throws Exception Si el cupón no existe
     */
    @Override
    public String eliminarCupon(String id) throws Exception {
        Optional<Cupon> cuponOptional = cuponRepo.buscarPorCodigo(id);
        if (cuponOptional.isEmpty()) {
            throw new Exception("Cupón no encontrado");
        }
        Cupon cupon = cuponOptional.get();
        cupon.setEstado(EstadoCupon.NO_DISPONIBLE);
        cuponRepo.save(cupon);
        return "Cupón eliminado exitosamente";
    }

    /**
     * Obtiene una lista de todos los cupones registrados en el sistema
     * @return Lista de DTOs con información básica de los cupones
     * @throws Exception Si ocurre un error en la consulta
     */
    @Override
    public List<ObtenerCuponDTO> listarCupones() throws Exception{
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

    /**
     * Obtiene información detallada de un cupón específico
     * @param id Identificador único del cupón
     * @return DTO con todos los datos del cupón
     * @throws Exception Si el cupón no existe
     */
    private Cupon obtenerCupon(String id) throws Exception {
        Optional<Cupon> cuponOptional = cuponRepo.buscarPorCodigo(id);

        if (cuponOptional.isEmpty()) {
            throw new Exception("El cupón con el id: " + id + " no existe");
        }

        return cuponOptional.get();
    }

    /**
     * Valida y aplica un cupón para su uso en una compra
     * @param codigo Código único del cupón
     * @return DTO con el descuento aplicable
     * @throws Exception Si el cupón no es válido (vencido, inactivo, usos excedidos)
     */
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

    /**
     * Registra el uso de un cupón y actualiza su estado según su tipo
     * @param idCupon Identificador único del cupón utilizado
     * @throws Exception Si el cupón no existe o no se puede actualizar
     */
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
