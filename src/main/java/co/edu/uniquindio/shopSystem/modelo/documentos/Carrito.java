package co.edu.uniquindio.shopSystem.modelo.documentos;

import co.edu.uniquindio.shopSystem.modelo.vo.DetalleCarrito;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "carritos")
public class Carrito {

    @Id
    private String id;
    private List<DetalleCarrito> items;
    private LocalDateTime fecha;
    private ObjectId idUsuario;
}
