package co.edu.uniquindio.shopSystem.repositorios;

import co.edu.uniquindio.shopSystem.modelo.documentos.Orden;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de operaciones de base de datos con órdenes de compra.
 * Proporciona métodos para buscar órdenes por diferentes criterios usando MongoDB.
 */
@Repository
public interface OrdenRepo extends MongoRepository<Orden, String> {

    /**
     * Busca una orden por su ID en formato String
     * @param ordenId Identificador único de la orden (en formato hexadecimal)
     * @return Optional con la orden encontrada o vacío si no existe
     */
    @Query("{ _id : ?0 }")
    Optional<Orden> buscarOrdenPorId(String ordenId);

    /**
     * Busca una orden usando el ObjectId de MongoDB
     * @param ordenId ObjectId generado por MongoDB
     * @return Optional con la orden correspondiente
     * @apiNote Útil para consultas directas con ObjectId nativo
     */
    @Query("{ _id : ?0 }")
    Optional<Orden> buscarOrdenPorObjectId(ObjectId ordenId);

    /**
     * Versión alternativa para buscar por ObjectId en formato String
     * @param ordenId String representando un ObjectId válido
     * @return Optional con la orden si existe
     * @throws IllegalArgumentException Si el formato del String no es válido
     */
    @Query("{ _id : ?0 }")
    Optional<Orden> buscarOrdenPorObjectId(String ordenId);

    /**
     * Obtiene todas las órdenes asociadas a un cliente usando su ObjectId
     * @param idCliente ObjectId del cliente en MongoDB
     * @return Lista de órdenes del cliente
     */
    @Query("{ idCliente : ?0 }")
    List<Orden> findByIdCliente(ObjectId idCliente);

    /**
     * Versión alternativa para buscar órdenes por ID de cliente en String
     * @param idCliente ID del cliente en formato String
     * @return Lista de órdenes asociadas al cliente
     */
    @Query("{ idCliente : ?0 }")
    List<Orden> findByIdCliente(String idCliente);
}

