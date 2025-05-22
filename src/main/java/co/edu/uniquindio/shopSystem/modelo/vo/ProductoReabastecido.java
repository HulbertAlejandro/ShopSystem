package co.edu.uniquindio.shopSystem.modelo.vo;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductoReabastecido {

    private String referenciaProducto;
    private String nombreProducto;
    private int cantidad;
}