package co.edu.uniquindio.shopSystem.modelo.vo;

import lombok.*;
import org.bson.types.ObjectId;

@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class DetalleCarrito {

    @EqualsAndHashCode.Include
    private String idDetalleCarrito;
    private int cantidad;
    private String nombreProducto;
    private float precioUnitario;
    private String idProducto;
}