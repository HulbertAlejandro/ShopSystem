package co.edu.uniquindio.shopSystem.modelo.documentos;

import co.edu.uniquindio.shopSystem.modelo.enums.TipoProducto;
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
    private String codigo;
    private String referencia;
    private String nombre;
    private String descripcion;
    private TipoProducto tipoProducto;
    private String urlImagen;
    private int unidades;
    private float precio;

}
