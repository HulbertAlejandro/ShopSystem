package co.edu.uniquindio.shopSystem.modelo.documentos;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

import java.util.Map;

@Document("inventario")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Inventario {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    // Mapa de productos con su cantidad disponible
    private Map<Producto, Integer> stockProductos;

    // Método para actualizar la cantidad de un producto en el inventario
    public void actualizarStock(Producto producto, int cantidad) {
        stockProductos.put(producto, stockProductos.getOrDefault(producto, 0) + cantidad);
    }
}
