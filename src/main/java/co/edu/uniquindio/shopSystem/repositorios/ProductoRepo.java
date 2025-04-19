package co.edu.uniquindio.shopSystem.repositorios;

import co.edu.uniquindio.shopSystem.modelo.documentos.Producto;
import co.edu.uniquindio.shopSystem.modelo.enums.TipoProducto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de operaciones con productos en MongoDB.
 * Proporciona consultas especializadas para el catálogo de productos.
 */
@Repository
public interface ProductoRepo extends MongoRepository<Producto, String> {

    /**
     * Busca un producto por su código único interno
     * @param codigo Identificador único asignado al producto
     * @return Optional con el producto encontrado o vacío
     */
    @Query("{ 'codigo' : ?0 }")
    Optional<Producto> buscarPorCodigo(String codigo);

    /**
     * Busca un producto por su referencia única (SKU/EAN)
     * @param referencia Código único de identificación comercial
     * @return Optional con el producto correspondiente
     */
    @Query("{ 'referencia' : ?0 }")
    Optional<Producto> buscarPorReferencia(String referencia);

    /**
     * Busca productos cuyo nombre contenga el texto especificado (insensible a mayúsculas)
     * @param nombre Fragmento de texto a buscar en el nombre
     * @return Lista de productos con coincidencias parciales en el nombre
     */
    @Query("{ 'nombre' : { $regex: ?0, $options: 'i' } }")
    List<Producto> buscarPorNombre(String nombre);

    /**
     * Filtra productos por su categoría/tipo
     * @param tipoProducto Categoría del producto según el enum TipoProducto
     * @return Lista de productos pertenecientes a la categoría especificada
     */
    @Query("{ 'tipoProducto' : ?0 }")
    List<Producto> buscarPorTipo(TipoProducto tipoProducto);

    /**
     * Encuentra productos con stock igual o por debajo del nivel mínimo indicado
     * @param cantidadMinima Umbral de stock mínimo para alertar
     * @return Lista de productos que necesitan reposición
     */
    @Query("{ 'unidades' : { $lte: ?0 } }")
    List<Producto> buscarPorStockBajo(int cantidadMinima);

    /**
     * Busca productos dentro de un rango de precios específico
     * @param precioMinimo Valor mínimo del rango (inclusive)
     * @param precioMaximo Valor máximo del rango (inclusive)
     * @return Lista de productos que cumplen con el rango de precios
     */
    @Query("{ 'precio' : { $gte: ?0, $lte: ?1 } }")
    List<Producto> buscarPorRangoDePrecio(float precioMinimo, float precioMaximo);
}
