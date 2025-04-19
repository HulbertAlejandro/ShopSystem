package co.edu.uniquindio.shopSystem.repositorios;

import co.edu.uniquindio.shopSystem.modelo.documentos.Inventario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la gestión de operaciones de inventario en MongoDB.
 * Proporciona consultas personalizadas para el manejo de stock de productos.
 */
@Repository
public interface InventarioRepo extends MongoRepository<Inventario, String> {

    /**
     * Busca un documento de inventario por su ID único
     * @param id Identificador único del documento de inventario
     * @return Optional con el inventario encontrado o vacío si no existe
     */
    @Query("{ '_id' : ?0 }")
    Optional<Inventario> findById(String id);

    /**
     * Verifica si un producto específico existe en algún inventario
     * @param idProducto Identificador único del producto a buscar
     * @return Optional con el inventario que contiene el producto
     * @apiNote Busca en el mapa stockProductos donde las claves son IDs de productos
     */
    @Query("{ 'stockProductos.?0' : { $exists: true } }")
    Optional<Inventario> buscarPorProducto(String idProducto);

    /**
     * Verifica si el stock de un producto está por debajo o igual a un límite mínimo
     * @param idProducto Identificador único del producto a verificar
     * @param cantidadMinima Umbral mínimo de stock a comprobar
     * @return Optional con el inventario donde el stock cumple la condición
     * @apiNote Útil para detectar productos que necesitan reposición
     */
    @Query("{ 'stockProductos.?0' : { $lte: ?1 } }")
    Optional<Inventario> verificarStockBajo(String idProducto, int cantidadMinima);
}
