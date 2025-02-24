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

@Repository
public interface CuponRepo extends MongoRepository<Cupon, String> {

    @Query("{ 'codigo' : ?0 }")
    Optional<Cupon> buscarPorCodigo(String codigo);

    @Query("{ 'tipo' : ?0 }")
    List<Cupon> buscarPorTipo(TipoCupon tipo);

    @Query("{ 'estado' : ?0 }")
    List<Cupon> buscarPorEstado(EstadoCupon estado);

    @Query("{ 'fechaVencimiento' : { $lt: ?0 } }")
    List<Cupon> buscarCuponesVencidos(LocalDateTime fechaActual);

    @Query("{ 'fechaVencimiento' : { $gte: ?0 } }")
    List<Cupon> buscarCuponesActivos(LocalDateTime fechaActual);
}
