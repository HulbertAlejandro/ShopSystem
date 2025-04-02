package co.edu.uniquindio.shopSystem.modelo.documentos;

import co.edu.uniquindio.shopSystem.modelo.enums.EstadoOrden;
import co.edu.uniquindio.shopSystem.modelo.vo.DetalleCarrito;
import co.edu.uniquindio.shopSystem.modelo.vo.DetalleOrden;
import co.edu.uniquindio.shopSystem.modelo.vo.Pago;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document("ordenes")
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class Orden extends Carrito {

    private ObjectId idCliente;

    @Field("fecha_orden")  // Usamos un nombre de campo diferente en la base de datos
    private LocalDateTime fecha;  // Esto evita el conflicto con el campo 'fecha' en Carrito
    private String codigoPasarela;
    private List<DetalleOrden> detallesOrden;
    private Pago pago;
    private String ordenId;
    private float total;
    private float descuento;
    private float impuesto;
    private String idCupon;
    private EstadoOrden estado;

    // Método que convierte List<DetalleCarrito> a List<DetalleOrden>
    public void setItemsFromCarrito(List<DetalleCarrito> itemsCarrito) {
        this.detallesOrden = itemsCarrito.stream()
                .map(item -> new DetalleOrden(/* Conversión personalizada */))
                .toList();
    }
}
