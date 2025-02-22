package co.edu.uniquindio.shopSystem.modelo.vo;

import co.edu.uniquindio.shopSystem.modelo.documentos.Producto;
import co.edu.uniquindio.shopSystem.modelo.enums.EstadoReposicion;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

import java.time.LocalDateTime;

@Document("reposiciones_producto")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ReposicionProducto {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private Producto producto;
    private int cantidadSolicitada;
    private LocalDateTime fechaSolicitud;
    private EstadoReposicion estado; // "PENDIENTE", "EN PROCESO", "COMPLETADO"

    public ReposicionProducto(Producto producto, int cantidadSolicitada) {
        this.producto = producto;
        this.cantidadSolicitada = cantidadSolicitada;
        this.fechaSolicitud = LocalDateTime.now();
        this.estado = EstadoReposicion.PENDIENTE;
    }
}
