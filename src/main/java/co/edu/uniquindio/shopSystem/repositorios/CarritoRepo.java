package co.edu.uniquindio.shopSystem.repositorios;

import co.edu.uniquindio.shopSystem.modelo.documentos.Carrito;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoRepo extends MongoRepository<Carrito, String> {

    @Query("{ '_id' : ?0 }")
    Optional<Carrito> findById(ObjectId id);

    @Query("{ 'idUsuario' : ?0 }")
    List<Carrito> buscarPorIdUsuario(ObjectId idUsuario);

    @Query("{ 'fecha' : { $gte: ?0, $lte: ?1 } }")
    List<Carrito> buscarPorRangoDeFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}

