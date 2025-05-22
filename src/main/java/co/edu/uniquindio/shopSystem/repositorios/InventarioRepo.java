package co.edu.uniquindio.shopSystem.repositorios;

import co.edu.uniquindio.shopSystem.modelo.documentos.Inventario;
import co.edu.uniquindio.shopSystem.modelo.enums.TipoProducto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepo extends MongoRepository<Inventario, String> {

    @Query("{ 'codigo' : ?0 }")
    Optional<Inventario> buscarPorCodigo(String codigo);

    @Query("{ 'referencia' : ?0 }")
    Optional<Inventario> buscarPorReferencia(String referencia);

    @Query("{ 'nombre' : { $regex: ?0, $options: 'i' } }")
    List<Inventario> buscarPorNombre(String nombre);

    @Query("{ 'tipoInventario' : ?0 }")
    List<Inventario> buscarPorTipo(TipoProducto tipoInventario);

    @Query("{ 'unidades' : { $lte: ?0 } }")
    List<Inventario> buscarPorStockBajo(int cantidadMinima);

    @Query("{ 'precio' : { $gte: ?0, $lte: ?1 } }")
    List<Inventario> buscarPorRangoDePrecio(float precioMinimo, float precioMaximo);

}

