package co.edu.uniquindio.shopSystem.repositorios;

import co.edu.uniquindio.shopSystem.modelo.documentos.Pedido;
import co.edu.uniquindio.shopSystem.modelo.enums.EstadoPedido;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de operaciones con pedidos en MongoDB.
 * Proporciona consultas especializadas para el seguimiento y análisis de pedidos.
 */
@Repository
public interface PedidoRepo extends MongoRepository<Pedido, String> {

    /**
     * Obtiene todos los pedidos asociados a un cliente específico
     * @param idCliente Identificador único del cliente en formato ObjectId
     * @return Lista de pedidos del cliente en cualquier estado
     */
    @Query("{ 'idCliente' : ?0 }")
    List<Pedido> buscarPorIdCliente(ObjectId idCliente);

    /**
     * Filtra pedidos por su estado actual
     * @param estado Estado del pedido según el enum EstadoPedido (ej: PENDIENTE, ENVIADO)
     * @return Lista de pedidos en el estado especificado
     */
    @Query("{ 'estado' : ?0 }")
    List<Pedido> buscarPorEstado(EstadoPedido estado);

    /**
     * Busca pedidos dentro de un rango de fechas (inclusive)
     * @param fechaInicio Fecha inicial del rango
     * @param fechaFin Fecha final del rango
     * @return Lista de pedidos creados en el período especificado
     */
    @Query("{ 'fecha' : { $gte: ?0, $lte: ?1 } }")
    List<Pedido> buscarPorRangoDeFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Busca un pedido por su código único de transacción en la pasarela de pagos
     * @param codigoPasarela Identificador proporcionado por la pasarela de pagos (ej: MercadoPago)
     * @return Optional con el pedido correspondiente si existe
     */
    @Query("{ 'codigoPasarela' : ?0 }")
    Optional<Pedido> buscarPorCodigoPasarela(String codigoPasarela);

    /**
     * Obtiene los pedidos donde se aplicó un cupón específico
     * @param idCupon Identificador del cupón aplicado
     * @return Lista de pedidos que utilizaron el cupón
     */
    @Query("{ 'idCupon' : ?0 }")
    List<Pedido> buscarPorCuponAplicado(String idCupon);

    /**
     * Filtra pedidos por monto total mínimo
     * @param totalMinimo Valor mínimo del total del pedido (inclusive)
     * @return Lista de pedidos que igualan o superan el monto especificado
     */
    @Query("{ 'total' : { $gte: ?0 } }")
    List<Pedido> buscarPorTotalMinimo(float totalMinimo);
}
