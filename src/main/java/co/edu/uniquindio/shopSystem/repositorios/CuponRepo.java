package co.edu.uniquindio.shopSystem.repositorios;

import co.edu.uniquindio.shopSystem.modelo.documentos.Cupon;
import co.edu.uniquindio.shopSystem.modelo.enums.EstadoCupon;
import co.edu.uniquindio.shopSystem.modelo.enums.TipoCupon;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de operaciones de base de datos con cupones.
 * Proporciona consultas personalizadas para diferentes necesidades de gestión de cupones.
 */
@Repository
public interface CuponRepo extends MongoRepository<Cupon, String> {

    /**
     * Busca un cupón por su código único
     * @param codigo Código identificador del cupón (ej: "VERANO2023")
     * @return Optional con el cupón encontrado o vacío si no existe
     */
    @Query("{ 'codigo' : ?0 }")
    Optional<Cupon> buscarPorCodigo(String codigo);

    /**
     * Obtiene todos los cupones de un tipo específico
     * @param tipo Tipo de cupón según el enum TipoCupon (DESCUENTO, ENVIO_GRATIS, etc)
     * @return Lista de cupones que coinciden con el tipo solicitado
     */
    @Query("{ 'tipo' : ?0 }")
    List<Cupon> buscarPorTipo(TipoCupon tipo);

    /**
     * Filtra cupones por su estado actual
     * @param estado Estado del cupón según el enum EstadoCupon (ACTIVO, USADO, EXPIRADO)
     * @return Lista de cupones en el estado especificado
     */
    @Query("{ 'estado' : ?0 }")
    List<Cupon> buscarPorEstado(EstadoCupon estado);

    /**
     * Encuentra cupones cuya fecha de vencimiento ya pasó
     * @param fechaActual Fecha de referencia para la comparación
     * @return Lista de cupones vencidos (con fecha anterior a la actual)
     */
    @Query("{ 'fechaVencimiento' : { $lt: ?0 } }")
    List<Cupon> buscarCuponesVencidos(LocalDateTime fechaActual);

    /**
     * Obtiene cupones que aún están vigentes
     * @param fechaActual Fecha de referencia para la comparación
     * @return Lista de cupones no vencidos (con fecha igual o posterior a la actual)
     */
    @Query("{ 'fechaVencimiento' : { $gte: ?0 } }")
    List<Cupon> buscarCuponesActivos(LocalDateTime fechaActual);
}
