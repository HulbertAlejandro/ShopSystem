package co.edu.uniquindio.shopSystem.repositorios;

import co.edu.uniquindio.shopSystem.modelo.documentos.Carrito;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarritoRepo extends MongoRepository<Carrito, String> {

    @Query("{ 'id': ?0 }")
    Optional<Carrito> buscarCarritoPorId(String id);

    @Query("{ 'idUsuario' : ?0 }")
    Optional<Carrito>   buscarCarritoPorIdCliente(String idUsuario);

}

