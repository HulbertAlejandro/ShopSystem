package co.edu.uniquindio.shopSystem.modelo.documentos;

import co.edu.uniquindio.shopSystem.modelo.enums.EstadoPedido;
import co.edu.uniquindio.shopSystem.modelo.vo.DetallePedido;
import co.edu.uniquindio.shopSystem.modelo.vo.Pago;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "pedidos")
public class Pedido {

    @Id
    private String pedidoId;
    private ObjectId idCliente;
    @Field("fecha_pedido")
    private LocalDateTime fecha;
    private String codigoPasarela;
    private List<DetallePedido> detallesPedido;
    private Pago pago;
    private float total;
    private String idCupon;
    private EstadoPedido estado;
}
