package co.edu.uniquindio.shopSystem.modelo.documentos;

import co.edu.uniquindio.shopSystem.modelo.enums.EstadoPedido;
import co.edu.uniquindio.shopSystem.modelo.vo.DetalleCarrito;
import co.edu.uniquindio.shopSystem.modelo.vo.DetallePedido;
import co.edu.uniquindio.shopSystem.modelo.vo.Pago;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document("pedidos")
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class Pedido extends Carrito {

    private ObjectId idCliente;

    @Field("fecha_pedido")
    private LocalDateTime fecha;

    private String codigoPasarela;
    private List<DetallePedido> detallesPedido;
    private Pago pago;
    private String pedidoId;
    private float total;
    private String idCupon;
    private EstadoPedido estado;

    public void setItemsFromCarrito(List<DetalleCarrito> itemsCarrito) {
        this.detallesPedido = itemsCarrito.stream()
                .map(item -> new DetallePedido(/* Conversión personalizada */))
                .toList();
    }
}
