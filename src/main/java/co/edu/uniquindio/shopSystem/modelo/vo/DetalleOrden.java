package co.edu.uniquindio.shopSystem.modelo.vo;

import co.edu.uniquindio.shopSystem.dto.OrdenDTO.CrearOrdenDTO;
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
public class DetalleOrden {

    private String idDetalleOrden;
    private String idProducto;
    private float precio;
    private String nombreProducto;
    private int cantidad;

}
