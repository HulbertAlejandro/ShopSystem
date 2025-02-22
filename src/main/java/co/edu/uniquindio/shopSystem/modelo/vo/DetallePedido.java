package co.edu.uniquindio.shopSystem.modelo.vo;

import lombok.*;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class DetallePedido {

    private String idDetallePedido;
    private String idProducto;
    private float precio;
    private String nombreProducto;
    private int cantidad;

    public static DetallePedido fromDetalleCarrito(DetalleCarrito item) {
        return DetallePedido.builder()
                .idDetallePedido(String.valueOf(ObjectId.get()))
                .idProducto(item.getIdProducto())
                .precio(0) // Se calculará luego
                .nombreProducto(item.getNombreProducto())
                .cantidad(item.getCantidad())
                .build();
    }

    public static List<DetallePedido> fromCarritoList(List<DetalleCarrito> itemsCarrito) {
        List<DetallePedido> detallePedidoList = new ArrayList<>();
        for (DetalleCarrito item : itemsCarrito) {
            detallePedidoList.add(fromDetalleCarrito(item));
        }
        return detallePedidoList;
    }
}