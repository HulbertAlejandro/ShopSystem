package co.edu.uniquindio.shopSystem.repositorios;

import co.edu.uniquindio.shopSystem.modelo.documentos.Producto;
import co.edu.uniquindio.shopSystem.modelo.enums.TipoProducto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepo extends MongoRepository<Producto, String> {

    @Query("{ 'codigo' : ?0 }")
    Optional<Producto> buscarPorCodigo(String codigo);

    @Query("{ 'referencia' : ?0 }")
    Optional<Producto> buscarPorReferencia(String referencia);

    @Query("{ 'nombre' : { $regex: ?0, $options: 'i' } }")
    List<Producto> buscarPorNombre(String nombre);

    @Query("{ 'tipoProducto' : ?0 }")
    List<Producto> buscarPorTipo(TipoProducto tipoProducto);

    @Query("{ 'unidades' : { $lte: ?0 } }")
    List<Producto> buscarPorStockBajo(int cantidadMinima);

    @Query("{ 'precio' : { $gte: ?0, $lte: ?1 } }")
    List<Producto> buscarPorRangoDePrecio(float precioMinimo, float precioMaximo);
}
