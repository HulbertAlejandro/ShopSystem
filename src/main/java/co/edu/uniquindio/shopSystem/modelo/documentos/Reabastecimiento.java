package co.edu.uniquindio.shopSystem.modelo.documentos;

import co.edu.uniquindio.shopSystem.modelo.enums.EstadoReabastecimiento;
import co.edu.uniquindio.shopSystem.modelo.vo.ProductoReabastecido;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document("reabastecimiento")
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class Reabastecimiento {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private LocalDateTime fechaCreacion;

    private List<ProductoReabastecido> productos;

    private Long proveedorId;

    private String observaciones;

    private EstadoReabastecimiento estado;
}
