package co.edu.uniquindio.shopSystem.modelo.vo;

import co.edu.uniquindio.shopSystem.modelo.documentos.Producto;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

import java.time.LocalDateTime;

@Document("movimientos_inventario")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MovimientoInventario {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private Producto producto;
    private int cantidad; // Positivo para ingreso, negativo para salida
    private LocalDateTime fechaMovimiento;
    private String tipoMovimiento; // "ENTRADA" o "SALIDA"

    public MovimientoInventario(Producto producto, int cantidad, String tipoMovimiento) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.tipoMovimiento = tipoMovimiento;
        this.fechaMovimiento = LocalDateTime.now();
    }
}

