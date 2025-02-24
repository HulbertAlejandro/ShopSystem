package co.edu.uniquindio.shopSystem.repositorios;

import co.edu.uniquindio.shopSystem.modelo.documentos.Inventario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventarioRepo extends MongoRepository<Inventario, String> {

    @Query("{ '_id' : ?0 }")
    Optional<Inventario> findById(String  id);

    @Query("{ 'stockProductos.?0' : { $exists: true } }")
    Optional<Inventario> buscarPorProducto(String idProducto);

    @Query("{ 'stockProductos.?0' : { $lte: ?1 } }")
    Optional<Inventario> verificarStockBajo(String idProducto, int cantidadMinima);
}

