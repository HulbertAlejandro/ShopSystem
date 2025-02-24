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

@Repository
public interface PedidoRepo extends MongoRepository<Pedido, String> {

    @Query("{ 'idCliente' : ?0 }")
    List<Pedido> buscarPorIdCliente(ObjectId idCliente);

    @Query("{ 'estado' : ?0 }")
    List<Pedido> buscarPorEstado(EstadoPedido estado);

    @Query("{ 'fecha' : { $gte: ?0, $lte: ?1 } }")
    List<Pedido> buscarPorRangoDeFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    @Query("{ 'codigoPasarela' : ?0 }")
    Optional<Pedido> buscarPorCodigoPasarela(String codigoPasarela);

    @Query("{ 'idCupon' : ?0 }")
    List<Pedido> buscarPorCuponAplicado(String idCupon);

    @Query("{ 'total' : { $gte: ?0 } }")
    List<Pedido> buscarPorTotalMinimo(float totalMinimo);
}
