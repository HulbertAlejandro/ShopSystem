package co.edu.uniquindio.shopSystem.repositorios;

import co.edu.uniquindio.shopSystem.modelo.documentos.Carrito;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la gestión de operaciones de base de datos relacionadas con el carrito de compras.
 * Proporciona consultas personalizadas para acceder a los carritos almacenados en MongoDB.
 */
@Repository
public interface CarritoRepo extends MongoRepository<Carrito, String> {

    /**
     * Busca un carrito por su identificador único en la base de datos
     * @param id Identificador único del carrito (campo 'id' del documento)
     * @return Optional que contiene el carrito encontrado o vacío si no existe
     */
    @Query("{ 'id': ?0 }")
    Optional<Carrito> buscarCarritoPorId(String id);

    /**
     * Busca el carrito activo asociado a un cliente específico
     * @param idUsuario Identificador único del usuario/cliente (campo 'idUsuario' del documento)
     * @return Optional con el carrito del usuario o vacío si no existe
     */
    @Query("{ 'idUsuario' : ?0 }")
    Optional<Carrito> buscarCarritoPorIdCliente(String idUsuario);
}

